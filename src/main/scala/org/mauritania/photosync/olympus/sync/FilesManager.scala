package org.mauritania.photosync.olympus.sync

import java.io.File

import org.mauritania.photosync.olympus.client.FileInfo

import scala.collection.immutable.Seq
import scala.util.Try

trait FilesManager {
  /**
    * List files that are in the local filesystem.
    *
    * @return a list of [[FileInfo]]
    */
  def listLocalFiles(): Seq[FileInfo]

  /**
    * List files that are in the remote filesystem (camera).
    *
    * @return a list of [[FileInfo]]
    */
  def listRemoteFiles(): Seq[FileInfo]

  /**
    * Prepare a plan to synchronize remote files with local files.
    *
    * @return sequence of [[SyncPlanItem]] to proceed with the synchronization
    */
  def syncPlan(): Seq[SyncPlanItem]

  /**
    * Synchronize remote files with local files.
    * Synchronization is one-way (remote to local).
    *
    * @return result of the synchronization
    */
  def sync(): Seq[Try[File]]

  /**
    * Synchronize a single file based on the synchronization plan item of
    * it (local status, remote status, file info, etc.).
    *
    * @param syncPlanItem item in the synchronization plan
    * @return the local result of the synchronization
    */
  def syncFile(syncPlanItem: SyncPlanItem): Try[File]
}
