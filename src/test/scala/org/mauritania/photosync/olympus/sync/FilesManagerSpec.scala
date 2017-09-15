package org.mauritania.photosync.olympus.sync

import java.io.File

import org.mauritania.photosync.TestHelper
import org.mauritania.photosync.olympus.client.CameraClient
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification

import scala.util.{Failure, Success}

class FilesManagerSpec extends Specification with Mockito {

  val AnyDirectory = new File(".")

  "The files manager" should {

    "correctly tell if a file was correctly downloaded" in {

      // Simulate camera telling that such file exists and has the same length as the local file
      val cameraClientMock = mock[CameraClient]
      val localFiles = Map("photo.jpg" -> 10L)
      val remoteFiles = Map("photo.jpg" -> 10L)

      // The manager should tell the file's already synchronized/downloaded
      val fm = new FilesManager(cameraClientMock, AnyDirectory)
      fm.isDownloaded("photo.jpg", localFiles, remoteFiles) mustEqual true

    }

    "correctly tell if a file was incorrectly downloaded" in {

      // Simulate camera telling that such file exists but it has different length than the local file
      val cameraClientMock = mock[CameraClient]
      val localFiles = Map("photo.jpg" -> 0L)
      val remoteFiles = Map("photo.jpg" -> 10L)

      // The manager should tell the file's is bad and should be re-downloaded
      val fm = new FilesManager(cameraClientMock, AnyDirectory)
      fm.isDownloaded("photo.jpg", localFiles, remoteFiles) mustEqual false

    }

    "correctly tell if a file was not downloaded" in {

      // Simulate camera telling there is one file to be downloaded
      val cameraClientMock = mock[CameraClient]
      val localFiles = Map.empty[String, Long]
      val remoteFiles = Map("photo.jpg" -> 10L)

      // The manager should tell the file has not been donwloaded yet
      val fm = new FilesManager(cameraClientMock, AnyDirectory)
      fm.isDownloaded("photo.jpg", localFiles, remoteFiles) mustEqual false

    }

    "correctly list locally (already) downloaded files" in {

      // Simulate downloads local directory and some photos
      val localDirectoryOfDownloads = TestHelper.createTmpDir("output")
      TestHelper.touchFile(localDirectoryOfDownloads, "photo1.jpg")
      TestHelper.touchFile(localDirectoryOfDownloads, "photo2.jpg")

      // Simulate camera
      val cameraClientMock = mock[CameraClient]

      // The manager should tell the file's is bad and should be re-downloaded
      val fm = new FilesManager(cameraClientMock, localDirectoryOfDownloads)

      fm.listLocalFiles() mustEqual Seq(
        FileInfo("photo1.jpg", 0L), FileInfo("photo2.jpg", 0L)
      )

    }

    "correctly synchronize a remote file (in the camera) that was not yet downloaded locally" in {

      // Simulate downloads local directory and photo1.jpg
      val localDirectoryOfDownloads = TestHelper.createTmpDir("output")
      val photo1 = TestHelper.touchFile(localDirectoryOfDownloads, "photo1.jpg")
      val photo2 = new File(localDirectoryOfDownloads, "photo2.jpg")

      photo1.exists() mustEqual true
      photo2.exists() mustEqual false

      // Simulate camera telling that photo2.jpg is available
      val cameraClientMock = mock[CameraClient]
      val remoteFilesMock = Seq(FileInfo("photo2.jpg", 100L))
      cameraClientMock.listFiles().returns(remoteFilesMock)
      cameraClientMock.downloadFile("photo2.jpg", localDirectoryOfDownloads).
        returns(Success(TestHelper.touchFile(localDirectoryOfDownloads, "photo2.jpg")))

      // The manager should download the file photo2.jpg
      val fm = new FilesManager(cameraClientMock, localDirectoryOfDownloads)
      fm.sync() mustEqual Seq(photo2)

      // There should be downloaded photo2.jpg and old photo1.jpg in local directory
      photo1.exists() mustEqual true
      photo2.exists() mustEqual true
    }

    "correctly synchronize a remote file (in the camera) that had been already downloaded locally" in {

      // TODO use already existent commons or guava createTmpDir
      // TODO erase directory when done

      // Simulate downloads local directory and photo1.jpg
      val localDirectoryOfDownloads = TestHelper.createTmpDir("output")
      val photo1 = TestHelper.touchFile(localDirectoryOfDownloads, "photo1.jpg")

      photo1.exists() mustEqual true

      // Simulate camera telling that photo1.jpg is available
      val cameraClientMock = mock[CameraClient]
      val remoteFilesMock = Seq(FileInfo("photo1.jpg", 0L))
      cameraClientMock.listFiles().returns(remoteFilesMock)
      cameraClientMock.downloadFile("photo1.jpg", localDirectoryOfDownloads).
        returns(Success(TestHelper.touchFile(localDirectoryOfDownloads, "photo1.jpg")))

      // The manager should skip downloading file photo1.jpg
      val fm = new FilesManager(cameraClientMock, localDirectoryOfDownloads)
      fm.sync().size mustEqual 0
    }

    "correctly handle a failure when synchronizing a file" in {

      // Simulate downloads local directory (no photo1.jpg)
      val localDirectoryOfDownloads = TestHelper.createTmpDir("output")

      // Simulate camera telling that photo1.jpg is available
      val cameraClientMock = mock[CameraClient]
      val remoteFilesMock = Seq(FileInfo("photo1.jpg", 100L))
      cameraClientMock.listFiles().returns(remoteFilesMock)
      cameraClientMock.downloadFile("photo1.jpg", localDirectoryOfDownloads).
        returns(Failure(new RuntimeException()))

      // The manager should download the file photo2.jpg
      val fm = new FilesManager(cameraClientMock, localDirectoryOfDownloads)
      fm.sync() mustEqual Seq.empty[FileInfo]
    }

    "correctly list what are the remote files" in {

      // Simulate camera telling that photo1.jpg and photo2.jpg are available
      val cameraClientMock = mock[CameraClient]
      val remoteFilesMock = Seq(FileInfo("photo1.jpg", 100L), FileInfo("photo2.jpg", 100L))
      cameraClientMock.listFiles().returns(remoteFilesMock)

      // The manager should list both files
      val fm = new FilesManager(cameraClientMock, new File("output"))
      fm.listRemoteFiles() mustEqual (remoteFilesMock)

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

