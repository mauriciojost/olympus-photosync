package org.mauritania.photosync.olympus.sync

import java.io.File
import java.nio.file.FileAlreadyExistsException

import org.specs2.mutable.Specification

class DirectoriesSpec extends Specification with TempDir {

  "The directories" should {

    "correctly create a non existent directory" in {
      withTmpDir { tmpDir =>
        val newDir = new File(tmpDir, "dir1")

        Directories.mkdirs(newDir)

        newDir.isFile must beFalse
        newDir.isDirectory must beTrue
      }
    }

    "fail to create a directory if a file already exists with such name" in {
      withTmpDir { tmpDir =>
        val newDir = new File(tmpDir, "dir1")
        newDir.createNewFile() // create file with same name
        Directories.mkdirs(newDir) must throwA[FileAlreadyExistsException]("dir1")
      }
    }

  }

}
