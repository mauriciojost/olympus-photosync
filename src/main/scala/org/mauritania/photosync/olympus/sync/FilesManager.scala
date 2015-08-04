package org.mauritania.photosync.olympus.sync

import java.io.File
//import com.typesafe.scalalogging.Logger
import org.mauritania.photosync.main.Starter.FileInfo
import org.mauritania.photosync.olympus.api.CameraApi
//import org.slf4j.LoggerFactory

class FilesManager(
                  api: CameraApi,
                  outputDir : String = "output"
                    ) {

  //val logger = Logger(LoggerFactory.getLogger(this.getClass))


  def isDownloaded(fileId: String, remoteFiles: List[FileInfo]): Boolean = {
    val locals = listLocalFiles()
    val localSize: Long = locals.toMap.get(fileId).getOrElse(0)
    val remoteSize: Long = remoteFiles.toMap.get(fileId).getOrElse(0)
    //logger.info("Local file size: $localSize (real size is $remoteSize)")
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
        val downloaded = isDownloaded(fileIdAndSize._1, remoteFiles )
        //logger.debug(s"File $fileIdAndSize._1 with size $fileIdAndSize._2 downloaded $downloaded")
        if (!downloaded) {
          api.downloadFile(fileIdAndSize._1, outputDirectory)
        }
      }
    }
  }

}
