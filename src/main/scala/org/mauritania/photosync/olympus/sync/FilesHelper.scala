package org.mauritania.photosync.olympus.sync

import java.io.{File, FileNotFoundException}
import org.slf4j.LoggerFactory

object FilesHelper {

  private val logger = LoggerFactory.getLogger(this.getClass)

  def mkdirs(directory: File): File = {
    directory.mkdirs
    if (!directory.isDirectory) {
      throw new FileNotFoundException(s"Failed to create directory $directory")
    }
    directory
  }
}