package org.mauritania.photosync.olympus.sync

import java.io.File
import java.time.LocalDateTime

import org.mauritania.photosync.TestHelper
import org.mauritania.photosync.olympus.client.{CameraClient, FileInfo}
import org.mauritania.photosync.olympus.sync.FilesManager.Config
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification

import scala.collection.immutable.Seq
import scala.util.{Failure, Success}

class FilesManagerSpec extends Specification with Mockito with TempDir {

  val ADateTime = LocalDateTime.of(2000, 1, 1, 0, 0, 0)
  val AnyDirectory = new File(".")
  val Separator = "/"
  val OlympFolder = "100OLYMP"

  "The files manager" should {
    "correctly list locally (already) downloaded files" in {
      withTmpDir { localDirectoryOfDownloads =>
        // Simulate downloads local directory and some photos
        TestHelper.touchFile(new File(localDirectoryOfDownloads, OlympFolder), "photo1.jpg")
        TestHelper.touchFile(new File(localDirectoryOfDownloads, OlympFolder), "photo2.jpg")

        // Simulate camera
        val cameraClientMock = mock[CameraClient]

        val fm = new FilesManager(cameraClientMock, Config(localDirectoryOfDownloads))

        fm.listLocalFiles().toSet mustEqual Set(
          FileInfo(OlympFolder, "photo1.jpg", 0L), FileInfo(OlympFolder, "photo2.jpg", 0L)
        )
      }
    }

    "correctly synchronize a remote file (in the camera) that was not yet downloaded locally" in {
      withTmpDir { localDirectoryOfDownloads =>

        // Simulate local download directory and photo1.jpg (but not photo2.jpg)
        val photo1 = TestHelper.touchFile(new File(localDirectoryOfDownloads, OlympFolder), "photo1.jpg")
        val photo2 = new File(new File(localDirectoryOfDownloads, OlympFolder), "photo2.jpg") // not present (not touched)

        photo1.exists() must beTrue
        photo2.exists() must beFalse

        // Simulate camera reporting that photo2.jpg is available
        val cameraClientMock = mock[CameraClient]
        val fiPhoto2 = FileInfo(OlympFolder, "photo2.jpg", 100L)
        val remoteFilesMock = Seq(fiPhoto2)
        cameraClientMock.listFiles().returns(remoteFilesMock)
        cameraClientMock.downloadFile(fiPhoto2, localDirectoryOfDownloads).
          returns(Success(TestHelper.touchFile(new File(localDirectoryOfDownloads, OlympFolder), "photo2.jpg")))

        // The manager should download the file photo2.jpg
        val fm = new FilesManager(cameraClientMock, Config(localDirectoryOfDownloads))
        fm.sync().toArray mustEqual Array(Success(photo2))

        // There should be downloaded photo2.jpg and old photo1.jpg in local directory
        photo1.exists() must beTrue
        photo2.exists() must beTrue
      }
    }

    "correctly synchronize a remote file (in the camera) that had been already downloaded locally" in {

      withTmpDir { localDirectoryOfDownloads =>
        // Simulate downloads local directory and photo1.jpg
        val photo1 = TestHelper.touchFile(new File(localDirectoryOfDownloads, OlympFolder), "photo1.jpg")

        photo1.exists() must beTrue

        // Simulate camera telling that photo1.jpg is available
        val cameraClientMock = mock[CameraClient]
        val fiPhoto1 = FileInfo(OlympFolder, "photo1.jpg", 0L)
        val remoteFilesMock = Seq(fiPhoto1)
        cameraClientMock.listFiles().returns(remoteFilesMock)
        cameraClientMock.downloadFile(fiPhoto1, localDirectoryOfDownloads).
          returns(Success(TestHelper.touchFile(new File(localDirectoryOfDownloads, OlympFolder), "photo1.jpg")))

        // The manager should skip downloading file photo1.jpg
        val fm = new FilesManager(cameraClientMock, Config(localDirectoryOfDownloads))
        val syncResult = fm.sync()
        syncResult.size mustEqual 1
        syncResult.head must beAFailedTry.withThrowable[AlreadyDownloadedException]
      }
    }

    "correctly handle a failure when synchronizing a file" in {
      // Simulate downloads local directory (no photo1.jpg)
      withTmpDir { localDirectoryOfDownloads =>
        // Simulate camera telling that photo1.jpg is available
        val cameraClientMock = mock[CameraClient]
        val fiPhoto1 = FileInfo(OlympFolder, "photo1.jpg", 100L)
        val remoteFilesMock = Seq(fiPhoto1)
        cameraClientMock.listFiles().returns(remoteFilesMock)
        cameraClientMock.downloadFile(fiPhoto1, localDirectoryOfDownloads).
          returns(Failure(new RuntimeException()))

        // The manager should download the file photo2.jpg
        val fm = new FilesManager(cameraClientMock, Config(localDirectoryOfDownloads))
        val syncResult = fm.sync()
        syncResult.size mustEqual 1
        syncResult.head must beFailedTry.withThrowable[RuntimeException]

      }
    }

    "correctly list what are the remote files" in {

      // Simulate camera telling that photo1.jpg and photo2.jpg are available
      val cameraClientMock = mock[CameraClient]
      val remoteFilesMock = Seq(FileInfo(OlympFolder, "photo1.jpg", 100L), FileInfo(OlympFolder, "photo2.jpg", 100L))
      cameraClientMock.listFiles().returns(remoteFilesMock)

      // The manager should list both files
      val fm = new FilesManager(cameraClientMock, Config(new File("output")))
      fm.listRemoteFiles() mustEqual remoteFilesMock

    }

  }

  def simulateAlreadyDownloadedLocalFile: (File, String, File) = {
    // Simulate already downloaded local file
    val localFileSimulatingDownloaded = TestHelper.createTmpFile("photo.", 100L)
    val localFilenameSimulatingDownloaded = localFileSimulatingDownloaded.getName
    val localDirectoryOfDownloads = localFileSimulatingDownloaded.getParentFile
    (localFileSimulatingDownloaded, localFilenameSimulatingDownloaded, localDirectoryOfDownloads)
  }

}

