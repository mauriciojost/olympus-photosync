package org.mauritania.photosync.olympus.sync

import java.io.File

import org.mauritania.photosync.olympus.sync.SyncPlanItem.Index
import org.specs2.mutable.Specification

class SyncPlanItemSpec extends Specification {

  val OlympFolder = "100OLYMP"
  val AnyDirectory = new File(".")

  "correctly set the downloaded status if already downloaded" in {
    val fi = FileInfo(OlympFolder, "photo.jpg", 10L)
    val localFiles = Map(fi.getFileId -> fi)
    val remoteFiles = Map(fi.getFileId -> fi)

    // The manager should tell the file's already synchronized/downloaded
    val syncPlanItem = SyncPlanItem(fi, Index(0, 10), localFiles, remoteFiles)
    syncPlanItem.isDownloaded mustEqual true

  }

  "correctly set the downloaded status if not downloaded yet" in {

    // File downloaded already but it has different length than the local file
    val fi0 = FileInfo(OlympFolder, "photo.jpg", 0L)
    val fi10 = FileInfo(OlympFolder, "photo.jpg", 10L)
    val localFiles = Map(fi0.getFileId -> fi0)
    val remoteFiles = Map(fi10.getFileId -> fi10)

    val syncPlanItem = SyncPlanItem(fi10, Index(0, 10), localFiles, remoteFiles)
    syncPlanItem.isDownloaded mustEqual false

  }

  "correctly tell if a file was not downloaded" in {

    // Simulate camera telling there is one file to be downloaded
    val localFiles = Map.empty[String, FileInfo]
    val fi = FileInfo(OlympFolder, "photo.jpg", 10L)
    val remoteFiles = Map(fi.getFileId -> fi)

    // The manager should tell the file has not been donwloaded yet
    val syncPlanItem = SyncPlanItem(fi, Index(0, 10), localFiles, remoteFiles)
    syncPlanItem.isDownloaded mustEqual false

  }

}
