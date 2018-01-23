package org.mauritania.photosync.olympus.client

import java.io.{File, FileOutputStream}
import java.nio.channels.Channels
import org.mauritania.photosync.olympus.sync.{Directories, FileInfo}
import org.slf4j.LoggerFactory
import scala.util.Try
import scala.collection.immutable.Seq
import org.mauritania.photosync.olympus.client.CameraClient.ConnectTimeoutMs

/**
  * Camera client.
  * Provides primitives to interact with the camera server: list files, download, etc.
  * Connects using HTTP protocol.
  *
  * @param configuration [[CameraClientConfig]] instance containing server parameters
  */
class CameraClient(
  configuration: CameraClientConfig
) {

  val logger = LoggerFactory.getLogger(this.getClass)

  def listFiles(): Seq[FileInfo] = {
    val htmlLines = htmlQuery(generateRelativeUrl())

    logger.debug("HTML ROOT BEGIN")
    htmlLines.foreach(logger.debug)
    logger.debug("HTML ROOT END")

    val folders = generateDirectoriesListFromHtml(htmlLines)

    folders.foreach(folder => logger.info(s"Detected remote folder: $folder"))

    val files = folders.flatMap { folder =>
      val htmlLinesFolder = htmlQuery(generateRelativeUrl(Some(folder)))
      logger.debug(s"HTML ROOT BEGIN ($folder)")
      htmlLinesFolder.foreach(logger.debug)
      logger.debug(s"HTML ROOT END ($folder)\n")
      generateFilesListFromHtml(htmlLinesFolder, folder)
    }

    files.foreach(file => logger.info(s"Detected remote file: $file (created on ${file.getHumanDate})"))

    files
  }

  def downloadFile(folderName: String, remoteFileId: String, localTargetDirectory: File): Try[File] = {
    val urlSourceFile = configuration.fileUrl(generateRelativeUrl(Some(folderName), Some(remoteFileId)))
    Try {
      val inputStream = urlSourceFile.openStream()
      try {
        val channel = Channels.newChannel(inputStream)
        val directory = new File(localTargetDirectory, folderName)
        Directories.mkdirs(directory)
        val destinationFile = new File(directory, remoteFileId)
        val outputStream = new FileOutputStream(destinationFile)
        outputStream.getChannel.transferFrom(channel, 0, Long.MaxValue)
        destinationFile.getAbsoluteFile
      } finally {
        inputStream.close()
      }
    }
  }

  def shutDown(): Unit = {
    logger.info("Shutting down")
    htmlQuery("/exec_pwoff.cgi")
    logger.info("Shutdown complete")
  }

  private[client] def htmlQuery(relativeUrl: String): Seq[String] = {
    val url = configuration.fileUrl(relativeUrl)
    logger.info(s"Querying URL $url...")
    val connection = url.openConnection
    connection.setConnectTimeout(ConnectTimeoutMs)
    val inputStream = connection.getInputStream
    val responseLines = io.Source.fromInputStream(inputStream).getLines().toList
    if (inputStream != null) inputStream.close
    responseLines
  }

  private def generateDirectoriesListFromHtml(htmlLines: Seq[String]): Seq[String] = {
    val fileRegex = configuration.fileRegex.r
    val folderNames = htmlLines.flatMap(
      htmlLineToBeParsed =>
        htmlLineToBeParsed match {
          case fileRegex(folderName, fileSizeBytes, _, _, _) => Some(folderName)
          case _ => None
        }
    )

    folderNames.distinct
  }

  private def generateFilesListFromHtml(htmlLines: Seq[String], folder: String): Seq[FileInfo] = {
    val fileRegex = configuration.fileRegex.r
    val fileIdsAndSize = htmlLines.flatMap(
      htmlLineToBeParsed =>
        htmlLineToBeParsed match {
          case fileRegex(fileId, fileSizeBytes, _, date, time) =>
            Some(FileInfo(folder, fileId, fileSizeBytes.toLong, date.toInt))
          case _ =>
            None
        }
    )

    fileIdsAndSize.distinct
  }

  private def generateRelativeUrl(folder: Option[String] = None, file: Option[String] = None): String = {
    val fileStr = file.map(CameraClient.UrlSeparator + _).mkString
    val folderAndFileStr = folder.map(CameraClient.UrlSeparator + _ + fileStr).mkString
    configuration.serverBaseUrl + folderAndFileStr
  }
}

object CameraClient {
  val UrlSeparator = "/"
  val ConnectTimeoutMs = 5000
}

