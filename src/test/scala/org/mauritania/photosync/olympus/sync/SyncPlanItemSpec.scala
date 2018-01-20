package org.mauritania.photosync.olympus.sync

import java.io.File

import org.mauritania.photosync.olympus.sync.SyncPlanItem.Index
import org.specs2.mutable.Specification

class SyncPlanItemSpec extends Specification {

  val OlympFolder = "100OLYMP"
  val AnyDirectory = new File(".")
  val AFileInfo = FileInfo(OlympFolder, "photo.jpg", 10L)

  "correctly set the downloaded status if already downloaded" in {
    val localFiles = Map(AFileInfo.getFileId -> AFileInfo)
    val remoteFiles = Map(AFileInfo.getFileId -> AFileInfo)

    // The manager should tell the file's already synchronized/downloaded
    val syncPlanItem = SyncPlanItem(AFileInfo, Index(0, 10), localFiles, remoteFiles)
    syncPlanItem.downloadStatus mustEqual SyncPlanItem.Downloaded

  }

  "correctly set the downloaded status if not downloaded yet" in {

    // File downloaded already but it has different length than the local file
    val fi5 = AFileInfo.copy(size = 5L)
    val fi10 = AFileInfo.copy(size = 10L)
    val localFiles = Map(fi5.getFileId -> fi5)
    val remoteFiles = Map(fi10.getFileId -> fi10)

    val syncPlanItem = SyncPlanItem(fi10, Index(0, 10), localFiles, remoteFiles)
    syncPlanItem.downloadStatus mustEqual SyncPlanItem.PartiallyDownloaded

  }

  "correctly tell if a file was not downloaded" in {

    // Simulate camera telling there is one file to be downloaded
    val localFiles = Map.empty[String, FileInfo]
    val remoteFiles = Map(AFileInfo.getFileId -> AFileInfo)

    // The manager should tell the file has not been donwloaded yet
    val syncPlanItem = SyncPlanItem(AFileInfo, Index(0, 10), localFiles, remoteFiles)
    syncPlanItem.downloadStatus mustEqual SyncPlanItem.OnlyRemote

  }

  "correctly tell if a file is present only locally" in {

    // No files in camera, a file locally
    val remoteFiles = Map.empty[String, FileInfo]
    val localFiles = Map(AFileInfo.getFileId -> AFileInfo)

    // The manager should tell the file is only locally
    val syncPlanItem = SyncPlanItem(AFileInfo, Index(0, 10), localFiles, remoteFiles)
    syncPlanItem.downloadStatus mustEqual SyncPlanItem.OnlyLocal

  }

}
