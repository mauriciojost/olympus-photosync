package org.mauritania.photosync.olympus.client

import java.io.{File, FileOutputStream}
import java.net.URL
import java.nio.channels.Channels
import org.mauritania.photosync.olympus.FilesHelper
import org.mauritania.photosync.olympus.sync.FileInfo
import org.slf4j.LoggerFactory
import scala.io.Source
import scala.util.Try

class CameraClient(
  configuration: CameraClientConfig,
  urlTranslator: URL => URL = identity
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

    files.foreach(file => logger.info(s"Detected remote file: $file"))

    files

  }

  private[client] def htmlQuery(relativeUrl: String): Seq[String] = {
    val url = new URL(configuration.serverProtocol, configuration.serverName, configuration.serverPort, relativeUrl)
    val newUrl = urlTranslator(url)
    Source.fromURL(newUrl).getLines().toList
  }

  def downloadFile(folderName: String, remoteFileId: String, localTargetDirectory: File): Try[File] = {
    val urlSourceFile = new URL(
      configuration.serverProtocol,
      configuration.serverName,
      configuration.serverPort,
      generateRelativeUrl(Some(folderName), Some(remoteFileId))
    )
    val urlSourceFileTranslated = urlTranslator(urlSourceFile)
    Try {
      val rbc = Channels.newChannel(urlSourceFileTranslated.openStream());
      val directory = new File(localTargetDirectory, folderName)
      FilesHelper.mkdirs(directory)
      val destinationFile = new File(directory, remoteFileId)
      val fos = new FileOutputStream(destinationFile);
      fos.getChannel().transferFrom(rbc, 0, Long.MaxValue);
      destinationFile.getAbsoluteFile
    }
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
          case fileRegex(fileId, fileSizeBytes, x, y, z) => Some(FileInfo(folder, fileId, fileSizeBytes.toLong))
          case _ => None
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
}

