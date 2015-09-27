package org.mauritania.photosync.olympus.sync

import java.io.File
import org.slf4j.LoggerFactory
import org.mauritania.photosync.olympus.client.CameraClient

class FilesManager(
  api: CameraClient,
  outputDir: File
) {

  type FileInfo = (String, Long)

  val logger = LoggerFactory.getLogger(this.getClass)

  def isDownloaded(fileId: String, remoteFiles: List[FileInfo]): Boolean = {
    val locals = listLocalFiles()
    val localSize: Long = locals.toMap.get(fileId).getOrElse(0)
    val remoteSize: Long = remoteFiles.toMap.get(fileId).getOrElse(0)
    localSize == remoteSize
  }

  def listLocalFiles(): List[FileInfo] = {
    val files = outputDir.listFiles()
    val filesAndSizes = files.map(file => (file.getName, file.length())).toList
    filesAndSizes
  }

  def listRemoteFiles(): List[FileInfo] = {
    val filesAndSizes = api.listFiles()
    filesAndSizes
  }

  def sync(): List[File] = {
    val remoteFiles = api.listFiles()
    val outputDirectory = outputDir
    outputDirectory.mkdir()

    remoteFiles.flatMap {
      fileIdAndSize => {
        val downloaded = isDownloaded(fileIdAndSize._1, remoteFiles)
        if (!downloaded) {
          logger.debug("Downloading file {} with size {}", fileIdAndSize._1, fileIdAndSize._2)
          val downloadedFile = api.downloadFile(fileIdAndSize._1, outputDirectory)
          Some(downloadedFile)
        } else {
          logger.debug("Skipping file {} as it's been already downloaded", fileIdAndSize._1)
          None
        }
      }
    }
  }

}
