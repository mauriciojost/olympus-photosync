package org.mauritania.photosync.olympus.sync

import java.io.File
import org.slf4j.LoggerFactory
import org.mauritania.photosync.main.Starter.FileInfo
import org.mauritania.photosync.olympus.api.CameraApi

class FilesManager(
  api: CameraApi,
  outputDir: String = "output"
) {

  val logger = LoggerFactory.getLogger(this.getClass)

  def isDownloaded(fileId: String, remoteFiles: List[FileInfo]): Boolean = {
    val locals = listLocalFiles()
    val localSize: Long = locals.toMap.get(fileId).getOrElse(0)
    val remoteSize: Long = remoteFiles.toMap.get(fileId).getOrElse(0)
    localSize == remoteSize
  }

  def listLocalFiles(): List[FileInfo] = {
    val files = new File(outputDir).listFiles()
    val filesAndSizes = files.map(file => (file.getName, file.length())).toList
    filesAndSizes
  }

  def listRemoteFiles(): List[FileInfo] = {
    val filesAndSizes = api.listFiles()
    filesAndSizes
  }

  def sync(): Unit = {
    val remoteFiles = api.listFiles()
    val outputDirectory = new File(outputDir)
    outputDirectory.mkdir()

    remoteFiles.foreach {
      fileIdAndSize => {
        val downloaded = isDownloaded(fileIdAndSize._1, remoteFiles)
        if (!downloaded) {
          logger.debug("Downloading file {} with size {}", fileIdAndSize._1, fileIdAndSize._2)
          api.downloadFile(fileIdAndSize._1, outputDirectory)
        } else {
          logger.debug("Skipping file {} as it's been already downloaded", fileIdAndSize._1)
        }
      }
    }
  }

}
