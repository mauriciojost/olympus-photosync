package org.mauritania.photosync.olympus.sync

import java.io.{File, FileNotFoundException}

object FilesHelper {

  def mkdirs(directory: File): File = {
    directory.mkdirs
    if (!directory.isDirectory) {
      throw new FileNotFoundException(s"Failed to create directory $directory")
    }
    directory
  }
}