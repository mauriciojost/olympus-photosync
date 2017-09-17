package org.mauritania.photosync.olympus

import java.io.{FileNotFoundException, File}

object FilesHelper {

  def mkdirs(directory: File): File = {
    directory.mkdirs
    if (!directory.isDirectory) {
      throw new FileNotFoundException(s"Failed to create directory $directory")
    }
    directory
  }
}