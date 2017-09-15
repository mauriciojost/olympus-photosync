package org.mauritania.photosync.olympus.client

import java.io.{File, FileOutputStream}
import java.net.URL
import java.nio.channels.Channels
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
    val htmlLines = Source.fromURL(
      urlTranslator(
        new URL(
          configuration.serverProtocol,
          configuration.serverName,
          configuration.serverPort,
          generateRelativeUrl
        )
      )
    ).getLines().toSeq

    logger.info("DUMP>>>")
    htmlLines.foreach(logger.info)
    logger.info("<<<DUMP")

    generateFilesListFromHtml(htmlLines)
  }

  def downloadFile(remoteFileId: String, localTargetDirectory: File): Try[File] = {
    val urlSourceFile = urlTranslator(
      new URL(
        configuration.serverProtocol,
        configuration.serverName,
        configuration.serverPort,
        generateRelativeUrl.concat(remoteFileId)
      )
    )
    Try {
      val rbc = Channels.newChannel(urlSourceFile.openStream());
      val destinationFile = new File(localTargetDirectory, remoteFileId)
      val fos = new FileOutputStream(destinationFile);
      fos.getChannel().transferFrom(rbc, 0, Long.MaxValue);
      destinationFile.getAbsoluteFile
    }
  }

  private def generateFilesListFromHtml(htmlLines: Seq[String]): Seq[FileInfo] = {
    val fileRegex = configuration.fileRegex.r
    val fileIdsAndSize = htmlLines.flatMap(
      htmlLineToBeParsed =>
        htmlLineToBeParsed match {
          case fileRegex(fileId, fileSizeBytes) => Some(FileInfo(fileId, fileSizeBytes.toLong))
          case _ => None
        }
    )

    fileIdsAndSize.distinct
  }

  private def generateRelativeUrl: String = {
    configuration.serverBaseUrl + "/" + configuration.serverFolderName + "/"
  }

}

