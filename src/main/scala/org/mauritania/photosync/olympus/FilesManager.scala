package org.mauritania.photosync.olympus

import java.io.File

import org.mauritania.photosync.olympus.sync.FileInfo
import org.mauritania.photosync.olympus.sync.FilesManagerImpl.SyncPlanItem
import scala.collection.immutable.Seq

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
    * @return list of local [[File]] that were successfully synchronized
    */
  def sync(): Seq[File]

  /**
    * Synchronize a single file based on its info, local and remote state.
    *
    * @param fileInfo
    * @param localFilesMap
    * @param remoteFilesMap
    * @return
    */
  def syncFile(fileInfo: FileInfo, localFilesMap: Map[String, FileInfo], remoteFilesMap: Map[String, FileInfo]): Option[File]
}
