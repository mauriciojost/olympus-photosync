package org.mauritania.photosync.olympus.sync

import java.io.File

import org.mauritania.photosync.olympus.FilesManager

import scala.collection.immutable.Seq
import scala.util.{Success, Try}

case class FilesManagerMock(val config: FilesManagerImpl.Config) extends FilesManager {
  val Day20100101 = FileInfo.MinMachineDayticks
  val Day20100103 = FileInfo.MinMachineDayticks + 2

  val FileInfos = Seq(
    FileInfo("DSC", "XXX100.ORF", 10L, Day20100101),
    FileInfo("DSC", "XXX101.ORF", 11L, Day20100101),
    FileInfo("DSC", "XXX103.AVI", 15L, Day20100103),
    FileInfo("DSC", "YYX101.MP4", 20L, Day20100103)
  )

  override def listLocalFiles(): Seq[FileInfo] = {
    FileInfos
  }

  override def listRemoteFiles(): Seq[FileInfo] = {
    Thread.sleep(3000)
    FileInfos
  }

  override def syncPlan() = {
    def tof(f: FileInfo, i: Int) = SyncPlanItem(f, SyncPlanItem.Index(i, 10), true)
    val fis = listRemoteFiles.filter(FileInfoFilter.isFileEligible(_, config.mediaFilter))
    fis.zipWithIndex.map { case (f, i) => tof(f, i) }
  }

  override def sync(): Seq[Try[File]] = {
    FileInfos.map(f => Success(new File(f.name)))
  }

  override def syncFile(syncPlanItem: SyncPlanItem): Try[File] = {
    Thread.sleep(1000)
    Success(new File(syncPlanItem.fileInfo.name))
  }
}
