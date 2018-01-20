package org.mauritania.photosync.olympus.sync

import java.io.{File, FileFilter}

import org.mauritania.photosync.olympus.client.CameraClient
import org.mauritania.photosync.olympus.sync.FilesManagerImpl.Config
import org.slf4j.LoggerFactory

import scala.collection.immutable.Seq
import scala.util.{Failure, Success, Try}

/**
  * Manages the file synchronization between camera and local filesystem.
  * @param api instance of [[CameraClient]] to be used to contact the camera
  * @param config
  */
class FilesManagerImpl(
  api: CameraClient,
  config: Config
) extends FilesManager {

  import FilesManagerImpl._

  override def listLocalFiles(): Seq[FileInfo] = {
    val directories = if (!config.outputDir.isDirectory)
      Seq.empty[File]
    else
      Seq.empty[File] ++ config.outputDir.listFiles(FilesManagerImpl.DirectoriesFilter)

    directories.flatMap { directory =>
      val files = directory.listFiles()
      val filesAndSizes = files.map(file => FileInfo(directory.getName, file.getName, file.length()))
      filesAndSizes
    }
  }

  override def listRemoteFiles(): Seq[FileInfo] = {
    val files = api.listFiles()
    val filteredFiles = files.filter(FileInfoFilter.isFileEligible(_, config.mediaFilter))
    filteredFiles
  }

  override def syncPlan(): Seq[SyncPlanItem] = {
    def toMap(s: Seq[FileInfo]) = s.map(i => (i.getFileId, i)).toMap

    val remoteFiles = listRemoteFiles()
    val localFiles = listLocalFiles()
    val remoteFilesMap = toMap(remoteFiles)
    val localFilesMap = toMap(localFiles)

    remoteFiles.zipWithIndex.map {
      case (fileInfo, index) => SyncPlanItem(fileInfo, SyncPlanItem.Index(index, remoteFiles.length), localFilesMap, remoteFilesMap)
    }
  }

  override def sync(): Seq[Try[File]] = {
    val syncPlanItems = syncPlan()
    syncPlanItems.map {
      case syncPlanItem @ SyncPlanItem(fileInfo, SyncPlanItem.Index(index, total), status) =>
        logger.info(s"Downloading ${index + 1} / ${total}...")
        syncFile(syncPlanItem)
    }
  }

  override def syncFile(
    syncPlanItem: SyncPlanItem
  ): Try[File] = {
    syncPlanItem.downloadStatus match {
      case i @ (SyncPlanItem.Downloaded | SyncPlanItem.OnlyLocal) =>
        logger.debug(s"Skipping file ${syncPlanItem.fileInfo} as it's been already downloaded")
        Failure(new AlreadyDownloadedException(syncPlanItem.fileInfo.name))
      case i @ (SyncPlanItem.OnlyRemote | SyncPlanItem.PartiallyDownloaded) =>
        logger.debug(s"Downloading file ${syncPlanItem.fileInfo} (previous status ${syncPlanItem.downloadStatus})")
        api.downloadFile(syncPlanItem.fileInfo.folder, syncPlanItem.fileInfo.name, config.outputDir)
    }
  }
}

object FilesManagerImpl {

  private val logger = LoggerFactory.getLogger(this.getClass)

  val DirectoriesFilter = new FileFilter {
    override def accept(pathname: File): Boolean = pathname.isDirectory
  }

  case class Config(
    outputDir: File,
    mediaFilter: FileInfoFilter.Criteria = FileInfoFilter.Criteria.Bypass
  )

}
