package org.mauritania.photosync.olympus.sync

import java.io.File
import java.nio.file.FileAlreadyExistsException

object Directories {

  def mkdirs(directory: File): File = {
    directory.mkdirs
    if (!directory.isDirectory) {
      throw new FileAlreadyExistsException(s"Failed to create directory $directory")
    }
    directory
  }
}
