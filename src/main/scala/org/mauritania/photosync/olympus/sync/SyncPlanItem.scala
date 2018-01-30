package org.mauritania.photosync.olympus.sync

import org.mauritania.photosync.olympus.client.FileInfo
import org.mauritania.photosync.olympus.sync.SyncPlanItem.{Downloaded, DownloadedStatus, Index}

case class SyncPlanItem(
  fileInfo: FileInfo,
  index: Index,
  downloadStatus: DownloadedStatus
)

object SyncPlanItem {

  sealed trait DownloadedStatus
  case object Downloaded extends DownloadedStatus
  case object OnlyLocal extends DownloadedStatus
  case object OnlyRemote extends DownloadedStatus
  case object PartiallyDownloaded extends DownloadedStatus

  case class Index(i: Int, total: Int) {
    def percentage: Float = i.toFloat / total
    def percentageAsStr: String = (percentage * 100).toInt + "%"
  }

  def apply(fileInfo: FileInfo, index: Index, local: Map[String, FileInfo], remote: Map[String, FileInfo]): SyncPlanItem = {
    val status = isDownloaded(fileInfo, local, remote)
    SyncPlanItem(fileInfo, index, status)
  }

  private def isDownloaded(fileInfo: FileInfo, localFiles: Map[String, FileInfo], remoteFiles: Map[String, FileInfo]): DownloadedStatus = {
    val localSize = localFiles.get(fileInfo.getFileId).map(_.size)
    val remoteSize = remoteFiles.get(fileInfo.getFileId).map(_.size)
    (localSize, remoteSize) match {
      case (Some(loc), Some(rem)) if (loc == rem) => Downloaded // both present, same size
      case (Some(loc), Some(rem)) if (loc != rem) => PartiallyDownloaded // both present, different size
      case (Some(loc), None) => OnlyLocal // local but not remote
      case (None, Some(rem)) => OnlyRemote // remote but not local
    }
  }

}

