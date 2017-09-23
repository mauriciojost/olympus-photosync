package org.mauritania.photosync.olympus.sync

import java.io.File

import org.apache.commons.io.FileUtils
import org.mauritania.photosync.TestHelper

trait TempDir {

  def withTmpDir[T](f: (File) => T): T = {
    val tmpDir = TestHelper.createTmpDir("tmpdir")
    try {
      f(tmpDir)
    } finally {
      FileUtils.deleteDirectory(tmpDir);
    }
  }

}
