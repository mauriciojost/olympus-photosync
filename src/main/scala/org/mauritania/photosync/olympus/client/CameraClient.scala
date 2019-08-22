package org.mauritania.photosync.olympus.client

import java.io.{File, FileOutputStream}
import java.net.URL
import java.nio.channels.Channels
import java.nio.charset.StandardCharsets
import java.time._

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
    val rootHtmlLines = httpGetAsString(rootUrl)

    logger.debug(s"Html root begin ($rootUrl)")
    rootHtmlLines.foreach(logger.debug)
    logger.debug("Html root end")

    val remoteDirs = dirsFromRootHtml(rootHtmlLines)

    remoteDirs.foreach(folder => logger.info(s"Detected remote folder: $folder"))

    val files = remoteDirs.flatMap { dir =>
      val dirUrl = baseDirFileUrl(Some(configuration.serverBaseUrl), Some(dir))
      val dirHtmlLines = httpGetAsString(dirUrl)
      logger.debug(s"Html for directory begin ($dirUrl / $dir)")
      dirHtmlLines.foreach(logger.debug)
      logger.debug(s"Html for directory end\n")
      filesFromDirHtml(dirHtmlLines, dir)
    }

    files.foreach(file => logger.info(s"Detected remote file: $file (created on ${file.humanDateTime})"))

    files
  }

  private def setDateTime(destinationFile: File, dateTime: ZonedDateTime): Unit = {
    if (configuration.preserveCreationDate) {
      val epochSecs = dateTime.toEpochSecond
      val success = destinationFile.setLastModified(epochSecs * 1000)
      if (!success) {
        logger.warn(s"Could not setup file date for: ${destinationFile.getName}")
      }
    }
  }

  /**
    * Downloads a specific file
    *
    * @param file the remote file information
    * @param localTargetDirectory the target local directory
    * @param dateTime the date time to be set as 'modified' in the newly created file (in user's timezone)
    * @return the [[Try]] containing the downloaded local file
    */
  def downloadFile(file: FileInfo, localTargetDirectory: File): Try[File] = {
    val urlSourceFile = configuration.fileUrl(baseDirFileUrl(Some(configuration.serverBaseUrl), Some(file.folder), Some(file.name)))
    Try {
      val inputStream = urlSourceFile.openStream()
      try {
        val channel = Channels.newChannel(inputStream)
        val localDirectory = new File(localTargetDirectory, file.folder)
        Directories.mkdirs(localDirectory)
        val destinationFile = new File(localDirectory, file.name)
        val outputStream = new FileOutputStream(destinationFile)
        outputStream.getChannel.transferFrom(channel, 0, Long.MaxValue)
        setDateTime(destinationFile, file.humanDateTime.atZone(configuration.zoneOffset))
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
    Try(httpGet(rootUrl, IsConnectedTimeout, IsConnectedTimeout)).isSuccess
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
    val reply = httpGetAsString("/exec_pwoff.cgi")
    logger.info(s"Shutdown complete: $reply")
    reply
  }

  /**
    * Does a GET to the given relative url (transforms into text)
    * @param relativeUrl
    * @return the collection of lines result of the query
    */
  private[client] def httpGetAsString(relativeUrl: String): Seq[String] = {
    logger.debug(s"Querying URL $relativeUrl...")
    val str = new String(httpGet(relativeUrl), StandardCharsets.ISO_8859_1)
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
  private[client] def httpGet(relativeUrl: String, readTimeout: Int = ReadTimeoutMs, connectTimeout: Int = ConnectTimeoutMs): Array[Byte] = {
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
            Some(FileInfo(fileDir, fileName, fileSizeBytes.toLong, date.toInt, time.toInt, Some(thumbnail)))
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
  val IsConnectedTimeout = 1000 // TODO make configurable
  val ConnectTimeoutMs = 20000 // TODO make configurable
  val ReadTimeoutMs = 20000 // TODO make configurable
  val NewLineSplit = "\\r?\\n"
  val LocalZoneOffset = OffsetDateTime.now().getOffset()
}

