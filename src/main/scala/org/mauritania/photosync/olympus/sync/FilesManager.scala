package org.mauritania.photosync.olympus.sync

import java.io.File

import org.mauritania.photosync.olympus.client.CameraClient
import org.slf4j.LoggerFactory

import scala.util.{Success, Failure}

class FilesManager(
  api: CameraClient,
  outputDir: File
) {

  val logger = LoggerFactory.getLogger(this.getClass)

  private[sync] def isDownloaded(fileId: String, localFiles: Map[String, Long], remoteFiles: Map[String, Long]): Boolean = {
    val localSize = localFiles.get(fileId)
    val remoteSize = remoteFiles.get(fileId)
    localSize == remoteSize
  }

  def listLocalFiles(): Set[FileInfo] = {
    val files = outputDir.listFiles().toSet
    val filesAndSizes = files.map(file => FileInfo(file.getName, file.length()))
    filesAndSizes
  }

  def listRemoteFiles(): Set[FileInfo] = {
    val filesAndSizes = api.listFiles()
    filesAndSizes
  }

  def sync(): Set[File] = {
    def toMap(s: Set[FileInfo]) = s.flatMap(FileInfo.unapply).toMap
    val remoteFiles = listRemoteFiles()
    val localFiles = listLocalFiles()
    val remoteFilesMap = toMap(remoteFiles)
    val localFilesMap = toMap(localFiles)

    outputDir.mkdir() // it may exist already

    remoteFiles.zipWithIndex.flatMap {
      case (fileInfo, index) =>
        logger.info(s"Downloading $index / ${remoteFiles.size}...")
        syncFile(fileInfo, localFilesMap, remoteFilesMap)
    }
  }

  private def syncFile(
    fileInfo: FileInfo,
    localFilesMap: Map[String, Long],
    remoteFilesMap: Map[String, Long]
  ): Option[File] = {
    isDownloaded(fileInfo.name, localFilesMap, remoteFilesMap) match {
      case false => {
        logger.debug(s"Downloading file ${fileInfo}")
        val downloadedFile = api.downloadFile(fileInfo.name, outputDir)
        downloadedFile match {
          case Success(file) =>
            Some(file)
          case Failure(error) =>
            logger.error(s"Exception downloading ${fileInfo}", error)
            None
        }
      }
      case true => {
        logger.debug(s"Skipping file ${fileInfo} as it's been already downloaded")
        None
      }
    }
  }
}
