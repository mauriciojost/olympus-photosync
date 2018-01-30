package org.mauritania.photosync.olympus.sync

import java.io.File

import org.mauritania.photosync.olympus.client.FileInfo
import org.mauritania.photosync.olympus.sync.SyncPlanItem.Downloaded

import scala.collection.immutable.Seq
import scala.util.{Success, Try}

/**
  * Mock class.
  *
  * Currently using it only for UX reasons.
  * Will be integrated soon as part of the unit testing for the UI.
  */
case class FilesManagerMock(val config: FilesManagerImpl.Config) extends FilesManager {

  val ListRemoteOpDelayMs = 3000
  val DownloadOpDelayMs = 1000

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
    Thread.sleep(ListRemoteOpDelayMs)
    FileInfos
  }

  override def syncPlan() = {
    def tof(f: FileInfo, i: Int) = SyncPlanItem(f, SyncPlanItem.Index(i, 10), Downloaded)
    val fis = listRemoteFiles.filter(FileInfoFilter.isFileEligible(_, config.mediaFilter))
    fis.zipWithIndex.map { case (f, i) => tof(f, i) }
  }

  override def sync(): Seq[Try[File]] = {
    FileInfos.map(f => Success(new File(f.name)))
  }

  override def syncFile(syncPlanItem: SyncPlanItem): Try[File] = {
    Thread.sleep(DownloadOpDelayMs)
    Success(new File(syncPlanItem.fileInfo.name))
  }
}
