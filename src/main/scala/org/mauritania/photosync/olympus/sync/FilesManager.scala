package org.mauritania.photosync.olympus.sync

import java.io.{File, FileFilter}

import org.mauritania.photosync.olympus.client.CameraClient
import org.mauritania.photosync.olympus.sync.FilesManager.Config
import org.slf4j.LoggerFactory

import scala.util.{Failure, Success}

class FilesManager(
  api: CameraClient,
  config: Config
) {

  val logger = LoggerFactory.getLogger(this.getClass)

  private[sync] def isDownloaded(fileInfo: FileInfo, localFiles: Map[String, Long], remoteFiles: Map[String, Long]): Boolean = {
    val localSize = localFiles.get(fileInfo.getFileId)
    val remoteSize = remoteFiles.get(fileInfo.getFileId)
    localSize == remoteSize
  }

  def listLocalFiles(): Seq[FileInfo] = {
    if (!config.outputDir.isDirectory) {
      throw new IllegalArgumentException(s"${config.outputDir} is not a directory")
    }
    val directories = config.outputDir.listFiles(FilesManager.DirectoriesFilter)
    directories.flatMap { directory =>
      val files = directory.listFiles()
      val filesAndSizes = files.map(file => FileInfo(directory.getName, file.getName, file.length()))
      filesAndSizes
    }
  }

  def listRemoteFiles(): Seq[FileInfo] = {
    val files = api.listFiles()
    val filteredFiles = files.filter(FileInfoFilter.isFileEligible(_, config.mediaFilter))
    filteredFiles
  }

  def sync(): Seq[File] = {
    def toMap(s: Seq[FileInfo]) = s.map(i => (i.getFileId, i.size)).toMap
    val remoteFiles = listRemoteFiles()
    val localFiles = listLocalFiles()
    val remoteFilesMap = toMap(remoteFiles)
    val localFilesMap = toMap(localFiles)

    FilesHelper.mkdirs(config.outputDir)

    remoteFiles.zipWithIndex.flatMap {
      case (fileInfo, index) =>
        logger.info(s"Downloading ${index + 1} / ${remoteFiles.size}...")
        syncFile(fileInfo, localFilesMap, remoteFilesMap)
    }
  }

  private def syncFile(
    fileInfo: FileInfo,
    localFilesMap: Map[String, Long],
    remoteFilesMap: Map[String, Long]
  ): Option[File] = {
    isDownloaded(fileInfo, localFilesMap, remoteFilesMap) match {
      case false => {
        logger.debug(s"Downloading file ${fileInfo}")
        val downloadedFile = api.downloadFile(fileInfo.folder, fileInfo.name, config.outputDir)
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

object FilesManager {
  val DirectoriesFilter = new FileFilter {
    override def accept(pathname: File): Boolean = pathname.isDirectory
  }

  case class Config(
    outputDir: File,
    mediaFilter: FileInfoFilter.Criteria = FileInfoFilter.Criteria.Bypass
  )
}
