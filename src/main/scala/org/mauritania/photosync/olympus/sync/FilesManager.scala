package org.mauritania.photosync.olympus.sync

import java.io.{File, FileFilter}

import org.mauritania.photosync.olympus.client.CameraClient
import org.mauritania.photosync.olympus.sync.FilesManager.Config
import org.slf4j.LoggerFactory
import scala.collection.immutable.Seq

import scala.util.{Failure, Success}

/**
  * Manages the file synchronization between camera and local filesystem.
  * @param api instance of [[CameraClient]] to be used to contact the camera
  * @param config
  */
class FilesManager(
  api: CameraClient,
  config: Config
) {

  import FilesManager._

  private[sync] def isDownloaded(fileInfo: FileInfo, localFiles: Map[String, FileInfo], remoteFiles: Map[String, FileInfo]): Boolean = {
    val localSize = localFiles.get(fileInfo.getFileId).map(_.size)
    val remoteSize = remoteFiles.get(fileInfo.getFileId).map(_.size)
    localSize == remoteSize
  }

  /**
    * List files that are in the local filesystem.
    * @return a list of [[FileInfo]]
    */
  def listLocalFiles(): Seq[FileInfo] = {
    if (!config.outputDir.isDirectory) {
      throw new IllegalArgumentException(s"${config.outputDir} is not a directory")
    }
    val directories = Seq.empty[File] ++ config.outputDir.listFiles(FilesManager.DirectoriesFilter)
    directories.flatMap { directory =>
      val files = directory.listFiles()
      val filesAndSizes = files.map(file => FileInfo(directory.getName, file.getName, file.length()))
      filesAndSizes
    }
  }

  /**
    * List files that are in the remote filesystem (camera).
    * @return a list of [[FileInfo]]
    */
  def listRemoteFiles(): Seq[FileInfo] = {
    val files = api.listFiles()
    val filteredFiles = files.filter(FileInfoFilter.isFileEligible(_, config.mediaFilter))
    filteredFiles
  }

  /**
    * Prepare a plan to synchronize remote files with local files.
    *
    * @return sequence of [[SyncPlanItem]] to proceed with the synchronization
    */
  def syncPlan(): Seq[SyncPlanItem] = {
    def toMap(s: Seq[FileInfo]) = s.map(i => (i.getFileId, i)).toMap

    val remoteFiles = listRemoteFiles()
    val localFiles = listLocalFiles()
    val remoteFilesMap = toMap(remoteFiles)
    val localFilesMap = toMap(localFiles)

    remoteFiles.zipWithIndex.map {
      case (fileInfo, index) => SyncPlanItem(fileInfo, index, localFilesMap, remoteFilesMap)
    }
  }

  /**
    * Synchronize remote files with local files.
    * Synchronization is one-way (remote to local).
    *
    * @return list of local [[File]] that were successfully synchronized
    */
  def sync(): Seq[File] = {
    Directories.mkdirs(config.outputDir)
    val syncPlanItems = syncPlan()
    syncPlanItems.flatMap {
      case SyncPlanItem(fileInfo, index, localFilesMap, remoteFilesMap) =>
        logger.info(s"Downloading ${index + 1} / ${remoteFilesMap.size}...")
        syncFile(fileInfo, localFilesMap, remoteFilesMap)
    }
  }

  /**
    * Synchronize a single file based on its info, local and remote state.
    * @param fileInfo
    * @param localFilesMap
    * @param remoteFilesMap
    * @return
    */
  def syncFile(
    fileInfo: FileInfo,
    localFilesMap: Map[String, FileInfo],
    remoteFilesMap: Map[String, FileInfo]
  ): Option[File] = {
    if (isDownloaded(fileInfo, localFilesMap, remoteFilesMap)) {
      logger.debug(s"Skipping file $fileInfo as it's been already downloaded")
      None
    } else {
      logger.debug(s"Downloading file $fileInfo")
      val downloadedFile = api.downloadFile(fileInfo.folder, fileInfo.name, config.outputDir)
      downloadedFile match {
        case Success(file) =>
          Some(file)
        case Failure(error) =>
          logger.error(s"Exception downloading $fileInfo", error)
          None
      }
    }
  }
}

object FilesManager {

  private val logger = LoggerFactory.getLogger(this.getClass)

  val DirectoriesFilter = new FileFilter {
    override def accept(pathname: File): Boolean = pathname.isDirectory
  }

  case class Config(
    outputDir: File,
    mediaFilter: FileInfoFilter.Criteria = FileInfoFilter.Criteria.Bypass
  )

  case class SyncPlanItem(f: FileInfo, i: Int, local: Map[String, FileInfo], remote: Map[String, FileInfo])

}
