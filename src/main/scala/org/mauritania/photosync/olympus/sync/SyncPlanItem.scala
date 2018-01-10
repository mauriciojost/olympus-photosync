package org.mauritania.photosync.olympus.sync

import org.mauritania.photosync.olympus.sync.SyncPlanItem.Index

case class SyncPlanItem(
  fileInfo: FileInfo,
  index: Index,
  isDownloaded: Boolean
)

object SyncPlanItem {

  case class Index(i: Int, total: Int)

  def apply(fileInfo: FileInfo, index: Index, local: Map[String, FileInfo], remote: Map[String, FileInfo]): SyncPlanItem = {
    val status = isDownloaded(fileInfo, local, remote)
    SyncPlanItem(fileInfo, index, status)
  }

  private def isDownloaded(fileInfo: FileInfo, localFiles: Map[String, FileInfo], remoteFiles: Map[String, FileInfo]): Boolean = {
    val localSize = localFiles.get(fileInfo.getFileId).map(_.size)
    val remoteSize = remoteFiles.get(fileInfo.getFileId).map(_.size)
    localSize == remoteSize
  }

}

