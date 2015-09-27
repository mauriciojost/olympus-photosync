package org.mauritania.photosync

import java.io.File
import java.nio.file.{Paths, Files}
import java.nio.charset.StandardCharsets

object TestHelper {

  // Helpers
  def touchFile(parent: File, filename: String): File = {
    val f = new File(parent, filename)
    f.createNewFile()
    f.deleteOnExit()

    f
  }

  def createTmpFile(prefix: String, size: Long): File = {
    val file = File.createTempFile(prefix, "tmp")
    file.deleteOnExit()
    Files.write(Paths.get(file.getAbsolutePath()), (" " * size.toInt).getBytes(StandardCharsets.UTF_8))

    file
  }

  def createTmpDir(prefix: String): File = {
    val file = File.createTempFile("test", "tmp")
    file.delete()
    file.mkdir()
    file.deleteOnExit()

    file
  }

}
