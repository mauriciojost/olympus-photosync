package org.mauritania.photosync.olympus.sync

import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import java.io.File
import org.mauritania.photosync.olympus.client.CameraClient
import org.mauritania.photosync.TestHelper

class FilesManagerSpec extends Specification with Mockito {

  "The files manager" should {

    "correctly tell if a file was correctly downloaded" in {

      val (localFileSimulatingDownloaded: File, localFilenameSimulatingDownloaded: String, localDirectoryOfDownloads: File) =
        simulateAlreadyDownloadedLocalFile

      // Simulate camera telling that such file exists and has the same length as the local file
      val cameraClientMock = mock[CameraClient]
      val remoteFilesMock = List((localFilenameSimulatingDownloaded, localFileSimulatingDownloaded.length))
      cameraClientMock.listFiles().returns(remoteFilesMock)

      // The manager should tell the file's already synchronized/downloaded
      val fm = new FilesManager(cameraClientMock, localDirectoryOfDownloads)
      fm.isDownloaded(localFilenameSimulatingDownloaded, remoteFilesMock) mustEqual true

    }

    "correctly tell if a file was incorrectly downloaded" in {

      val (localFileSimulatingDownloaded: File, localFilenameSimulatingDownloaded: String, localDirectoryOfDownloads: File) =
        simulateAlreadyDownloadedLocalFile

      // Simulate camera telling that such file exists but it has different length than the local file
      val cameraClientMock = mock[CameraClient]
      val remoteFilesMock = List((localFilenameSimulatingDownloaded, localFileSimulatingDownloaded.length - 1))
      cameraClientMock.listFiles().returns(remoteFilesMock)

      // The manager should tell the file's is bad and should be re-downloaded
      val fm = new FilesManager(cameraClientMock, localDirectoryOfDownloads)
      fm.isDownloaded(localFilenameSimulatingDownloaded, remoteFilesMock) mustEqual false

    }

    "correctly tell if a file was not downloaded" in {

      // Simulate empty downloads local directory (no photos syncd)
      val localDirectoryOfDownloads = TestHelper.createTmpDir("output")

      // Simulate camera telling there is one file to be downloaded
      val cameraClientMock = mock[CameraClient]
      val remoteFilesMock = List(("photo.jpg", 100L))
      cameraClientMock.listFiles().returns(remoteFilesMock)

      // The manager should tell the file has not been donwloaded yet
      val fm = new FilesManager(cameraClientMock, localDirectoryOfDownloads)
      fm.isDownloaded("photo.jpg", remoteFilesMock) mustEqual false

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

      fm.listLocalFiles().sortBy(x => x._1) mustEqual List(("photo1.jpg", 0L), ("photo2.jpg", 0L)).sortBy(x => x._1)

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
      val remoteFilesMock = List(("photo2.jpg", 100L))
      cameraClientMock.listFiles().returns(remoteFilesMock)
      cameraClientMock.downloadFile("photo2.jpg", localDirectoryOfDownloads).
        returns(TestHelper.touchFile(localDirectoryOfDownloads, "photo2.jpg"))

      // The manager should download the file photo2.jpg
      val fm = new FilesManager(cameraClientMock, localDirectoryOfDownloads)
      fm.sync() mustEqual (List(photo2))

      // There should be downloaded photo2.jpg and old photo1.jpg in local directory
      photo1.exists() mustEqual true
      photo2.exists() mustEqual true
    }

    "correctly synchronize a remote file (in the camera) that had been already downloaded locally" in {

      // Simulate downloads local directory and photo1.jpg
      // TODO use already existent commons or guava createTmpDir
      // TODO erase directory when done
      val localDirectoryOfDownloads = TestHelper.createTmpDir("output")
      val photo1 = TestHelper.touchFile(localDirectoryOfDownloads, "photo1.jpg")

      photo1.exists() mustEqual true

      // Simulate camera telling that photo1.jpg is available
      val cameraClientMock = mock[CameraClient]
      val remoteFilesMock = List(("photo1.jpg", 0L))
      cameraClientMock.listFiles().returns(remoteFilesMock)
      cameraClientMock.downloadFile("photo1.jpg", localDirectoryOfDownloads).
        returns(TestHelper.touchFile(localDirectoryOfDownloads, "photo1.jpg"))

      // The manager should skip downloading file photo1.jpg
      val fm = new FilesManager(cameraClientMock, localDirectoryOfDownloads)
      fm.sync() mustEqual Nil
    }

    "correctly list what are the remote files" in {

      // Simulate camera telling that photo1.jpg and photo2.jpg are available
      val cameraClientMock = mock[CameraClient]
      val remoteFilesMock = List(("photo1.jpg", 100L), ("photo2.jpg", 100L))
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

