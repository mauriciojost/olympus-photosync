package org.mauritania.photosync.olympus.client

import java.io.{File, FileOutputStream}
import java.net.URL
import java.nio.channels.Channels
import java.nio.charset.StandardCharsets

import org.mauritania.photosync.olympus.sync.Directories
import org.slf4j.LoggerFactory

import scala.util.Try
import scala.collection.immutable.Seq

import scala.reflect.io.Streamable

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

  import CameraClient._

  val logger = LoggerFactory.getLogger(this.getClass)

  /**
    * Lists all remote files
    * @return the list of remote [[FileInfo]] with their attributes
    */
  def listFiles(): Seq[FileInfo] = {
    val rootUrl = baseDirFileUrl(Some(configuration.serverBaseUrl))
    val rootHtmlLines = getAsString(rootUrl)

    logger.debug(s"Html root begin ($rootUrl)")
    rootHtmlLines.foreach(logger.debug)
    logger.debug("Html root end")

    val remoteDirs = dirsFromRootHtml(rootHtmlLines)

    remoteDirs.foreach(folder => logger.info(s"Detected remote folder: $folder"))

    val files = remoteDirs.flatMap { dir =>
      val dirUrl = baseDirFileUrl(Some(configuration.serverBaseUrl), Some(dir))
      val dirHtmlLines = getAsString(dirUrl)
      logger.debug(s"Html for directory begin ($dirUrl / $dir)")
      dirHtmlLines.foreach(logger.debug)
      logger.debug(s"Html for directory end\n")
      filesFromDirHtml(dirHtmlLines, dir)
    }

    files.foreach(file => logger.info(s"Detected remote file: $file (created on ${file.getHumanDate})"))

    files
  }

  /**
    * Downloads a specific file
    * @param remoteDir the remote directory
    * @param remoteFile the remote filename
    * @param localTargetDirectory the target local directory
    * @return the [[Try]] containing the downloaded local file
    */
  def downloadFile(remoteDir: String, remoteFile: String, localTargetDirectory: File): Try[File] = {
    val urlSourceFile = configuration.fileUrl(baseDirFileUrl(Some(configuration.serverBaseUrl), Some(remoteDir), Some(remoteFile)))
    Try {
      val inputStream = urlSourceFile.openStream()
      try {
        val channel = Channels.newChannel(inputStream)
        val localDirectory = new File(localTargetDirectory, remoteDir)
        Directories.mkdirs(localDirectory)
        val destinationFile = new File(localDirectory, remoteFile)
        val outputStream = new FileOutputStream(destinationFile)
        outputStream.getChannel.transferFrom(channel, 0, Long.MaxValue)
        destinationFile.getAbsoluteFile
      } finally {
        inputStream.close()
      }
    }
  }

  /**
    * Tells if the camera is reachable
    * @return true if connected
    */
  def isConnected(): Boolean = {
    val rootUrl = baseDirFileUrl(Some(configuration.serverBaseUrl))
    Try(get(rootUrl, IsConnectedTimeout, IsConnectedTimeout)).isSuccess
  }

  /**
    * Retrieves the URL of the thumbnail of a given media file
    * @param remoteDir directory
    * @param remoteFile file
    * @return the URL pointing to the thumbnail image
    */
  private[client] def thumbnailFileUrl(remoteDir: String, remoteFile: String): URL = {
    val fileUrlPart = baseDirFileUrl(Some(configuration.serverBaseUrl), Some(remoteDir), Some(remoteFile))
    val relativeUrl = s"/get_thumbnail.cgi?DIR=$fileUrlPart"
    val fullUrl = configuration.fileUrl(relativeUrl)
    fullUrl
  }

  /**
    * Tries to shutdown the camera
    * @return the response from the server
    */
  def shutDown(): Seq[String] = {
    val reply = getAsString("/exec_pwoff.cgi")
    logger.info(s"Shutdown complete: $reply")
    reply
  }

  /**
    * Does a GET to the given relative url (transforms into text)
    * @param relativeUrl
    * @return the collection of lines result of the query
    */
  private[client] def getAsString(relativeUrl: String): Seq[String] = {
    logger.debug(s"Querying URL $relativeUrl...")
    val str = new String(get(relativeUrl), StandardCharsets.ISO_8859_1)
    val strLines = str.split(NewLineSplit)
    Seq.empty[String] ++ strLines
  }

  /**
    * Does a GET to the given relative url
    * @param relativeUrl relative url to perform the verb against (base url is taken from configuration)
    * @param readTimeout read timeout as per [[java.net.URLConnection]]
    * @param connectTimeout connect timeout as per [[java.net.URLConnection]]
    * @return the reply result of the query
    */
  private[client] def get(relativeUrl: String, readTimeout: Int = ReadTimeoutMs, connectTimeout: Int = ConnectTimeoutMs): Array[Byte] = {
    val url = configuration.fileUrl(relativeUrl)
    val connection = url.openConnection
    connection.setConnectTimeout(ConnectTimeoutMs)
    connection.setReadTimeout(ReadTimeoutMs)
    val inputStream = connection.getInputStream
    try {
      Streamable.bytes(inputStream)
    } finally {
      inputStream.close()
    }
  }

  /**
    * Gets the collection of directories from HTML at root level
    * @param rootHtmlLines text lines as obtained from a GET at root level
    * @return the collection of remote directories
    */
  private def dirsFromRootHtml(rootHtmlLines: Seq[String]): Seq[String] = {
    val fileRegex = configuration.fileRegex.r
    val folderNames = rootHtmlLines.flatMap(
      htmlLineToBeParsed =>
        htmlLineToBeParsed match {
          case fileRegex(folderName, fileSizeBytes, _, _, _) => Some(folderName)
          case _ => None
        }
    )

    folderNames.distinct
  }

  /**
    * Gets the collection of files from HTML at directory level
    * @param dirHtmlLines text lines as obtained from a GET at dir level
    * @param fileDir directory that is being targetted
    * @return the collection of [[FileInfo]] inside such directory
    */
  private def filesFromDirHtml(dirHtmlLines: Seq[String], fileDir: String): Seq[FileInfo] = {
    val fileRegex = configuration.fileRegex.r
    val fileIdsAndSize = dirHtmlLines.flatMap(
      htmlLineToBeParsed =>
        htmlLineToBeParsed match {
          case fileRegex(fileName, fileSizeBytes, _, date, time) =>
            val thumbnail = thumbnailFileUrl(fileDir, fileName)
            Some(FileInfo(fileDir, fileName, fileSizeBytes.toLong, date.toInt, Some(thumbnail)))
          case _ =>
            None
        }
    )

    fileIdsAndSize.distinct
  }

  /**
    * Builds a relative URL from the arguments
    * @param base base url
    * @param dir directory
    * @param file file
    * @return the resulting url
    */
  private def baseDirFileUrl(base: Option[String], dir: Option[String] = None, file: Option[String] = None): String = {
    val filePart = file.map(UrlSeparator + _).mkString // "" or /file
    val dirFilePart = dir.map(UrlSeparator + _ + filePart).mkString // "" or /dir + <file>
    val baseDirFilePart = base.map(_ + dirFilePart).mkString // "" or /base + <dirfile>
    baseDirFilePart
  }
}

object CameraClient {
  val UrlSeparator = "/"
  val IsConnectedTimeout = 1000
  val ConnectTimeoutMs = 20000
  val ReadTimeoutMs = 20000
  val NewLineSplit = "\\r?\\n"
}

