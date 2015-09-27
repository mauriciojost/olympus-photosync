package org.mauritania.photosync.olympus.client

import java.io.{File, FileOutputStream}
import java.net.URL
import java.nio.channels.Channels
import org.slf4j.LoggerFactory
import scala.io.Source

class CameraClient(
  configuration: CameraClientConfig,
  urlTranslator: URL => URL
) {

  val logger = LoggerFactory.getLogger(this.getClass)

  def listFiles(): List[(String, Long)] = {
    logger.debug("listFiles")

    val htmlLines = Source.fromURL(
      urlTranslator(
        new URL(
          configuration.serverProtocol,
          configuration.serverName,
          configuration.serverPort,
          generateRelativeUrl)
      )
    ).getLines().toList

    logger.info("DUMP>>>")
    htmlLines.foreach(line => logger.info("@" + line + "@"))
    logger.info("<<<DUMP")

    generateFilesListFromHtml(htmlLines)
  }

  def downloadFile(remoteFileId: String, localTargetDirectory: File): File = {
    logger.debug("downloadFile")
    val urlSourceFile = urlTranslator(
      new URL(
        configuration.serverProtocol,
        configuration.serverName,
        configuration.serverPort,
        generateRelativeUrl.concat(remoteFileId)
      )
    )
    val rbc = Channels.newChannel(urlSourceFile.openStream());
    val destinationFile = new File(localTargetDirectory, remoteFileId)
    val fos = new FileOutputStream(destinationFile);
    fos.getChannel().transferFrom(rbc, 0, Long.MaxValue);
    destinationFile.getAbsoluteFile
  }

  private def generateFilesListFromHtml(htmlLines: List[String]): List[(String, Long)] = {
    val fileIdsAndSize = htmlLines.flatMap(
      htmlLineToBeParsed =>
        htmlLineToBeParsed match {
          case configuration.fileRegex(fileId, fileSizeBytes) => Some((fileId, fileSizeBytes.toLong))
          case _ => None
        }
    ).toList

    fileIdsAndSize
  }

  private def generateRelativeUrl: String = {
    configuration.serverBaseUrl + "/" + configuration.serverFolderName + "/"
  }

}

