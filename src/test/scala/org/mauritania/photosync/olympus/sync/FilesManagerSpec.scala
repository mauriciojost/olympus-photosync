package org.mauritania.photosync.olympus.sync

import java.io.File

import org.mauritania.photosync.TestHelper
import org.mauritania.photosync.olympus.client.CameraClient
import org.mauritania.photosync.olympus.sync.FilesManager.Config
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import scala.collection.immutable.Seq

import scala.util.{Failure, Success}

class FilesManagerSpec extends Specification with Mockito with TempDir {

  val AnyDirectory = new File(".")
  val Separator = "/"
  val OlympFolder = "100OLYMP"

  "The files manager" should {

    "correctly tell if a file was correctly downloaded" in {

      // Simulate camera telling that such file exists and has the same length as the local file
      val cameraClientMock = mock[CameraClient]
      val localFiles = Map(FileInfo(OlympFolder, "photo.jpg", 10L).getFileId -> 10L)
      val remoteFiles = Map(FileInfo(OlympFolder, "photo.jpg", 10L).getFileId -> 10L)

      // The manager should tell the file's already synchronized/downloaded
      val fm = new FilesManager(cameraClientMock, Config(AnyDirectory))
      fm.isDownloaded(FileInfo(OlympFolder, "photo.jpg", 10L), localFiles, remoteFiles) mustEqual true

    }

    "correctly tell if a file was incorrectly downloaded" in {

      // Simulate camera telling that such file exists but it has different length than the local file
      val cameraClientMock = mock[CameraClient]
      val localFiles = Map(FileInfo(OlympFolder, "photo.jpg", 0L).getFileId -> 0L)
      val remoteFiles = Map(FileInfo(OlympFolder, "photo.jpg", 10L).getFileId -> 10L)

      // The manager should tell the file's is bad and should be re-downloaded
      val fm = new FilesManager(cameraClientMock, Config(AnyDirectory))
      fm.isDownloaded(FileInfo(OlympFolder, "photo.jpg", 10L), localFiles, remoteFiles) mustEqual false

    }

    "correctly tell if a file was not downloaded" in {

      // Simulate camera telling there is one file to be downloaded
      val cameraClientMock = mock[CameraClient]
      val localFiles = Map.empty[String, Long]
      val remoteFiles = Map(FileInfo(OlympFolder, "photo.jpg", 10L).getFileId -> 10L)

      // The manager should tell the file has not been donwloaded yet
      val fm = new FilesManager(cameraClientMock, Config(AnyDirectory))
      fm.isDownloaded(FileInfo(OlympFolder, "photo.jpg", 10L), localFiles, remoteFiles) mustEqual false

    }

    "correctly list locally (already) downloaded files" in {

      // Simulate downloads local directory and some photos
      val localDirectoryOfDownloads = TestHelper.createTmpDir("output")
      TestHelper.touchFile(new File(localDirectoryOfDownloads, OlympFolder), "photo1.jpg")
      TestHelper.touchFile(new File(localDirectoryOfDownloads, OlympFolder), "photo2.jpg")

      // Simulate camera
      val cameraClientMock = mock[CameraClient]

      // The manager should tell the file's is bad and should be re-downloaded
      val fm = new FilesManager(cameraClientMock, Config(localDirectoryOfDownloads))

      fm.listLocalFiles() mustEqual Seq(
        FileInfo(OlympFolder, "photo1.jpg", 0L), FileInfo(OlympFolder, "photo2.jpg", 0L)
      )

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
        val remoteFilesMock = Seq(FileInfo(OlympFolder, "photo2.jpg", 100L))
        cameraClientMock.listFiles().returns(remoteFilesMock)
        cameraClientMock.downloadFile(OlympFolder, "photo2.jpg", localDirectoryOfDownloads).
          returns(Success(TestHelper.touchFile(new File(localDirectoryOfDownloads, OlympFolder), "photo2.jpg")))

        // The manager should download the file photo2.jpg
        val fm = new FilesManager(cameraClientMock, Config(localDirectoryOfDownloads))
        fm.sync() mustEqual Seq(photo2)

        // There should be downloaded photo2.jpg and old photo1.jpg in local directory
        photo1.exists() must beTrue
        photo2.exists() must beTrue
      }
    }

    "correctly synchronize a remote file (in the camera) that had been already downloaded locally" in {

      // TODO erase directory when done

      // Simulate downloads local directory and photo1.jpg
      val localDirectoryOfDownloads = TestHelper.createTmpDir("output")
      val photo1 = TestHelper.touchFile(new File(localDirectoryOfDownloads, OlympFolder), "photo1.jpg")

      photo1.exists() must beTrue

      // Simulate camera telling that photo1.jpg is available
      val cameraClientMock = mock[CameraClient]
      val remoteFilesMock = Seq(FileInfo(OlympFolder, "photo1.jpg", 0L))
      cameraClientMock.listFiles().returns(remoteFilesMock)
      cameraClientMock.downloadFile(OlympFolder, "photo1.jpg", localDirectoryOfDownloads).
        returns(Success(TestHelper.touchFile(new File(localDirectoryOfDownloads, OlympFolder), "photo1.jpg")))

      // The manager should skip downloading file photo1.jpg
      val fm = new FilesManager(cameraClientMock, Config(localDirectoryOfDownloads))
      fm.sync().size mustEqual 0
    }

    "correctly handle a failure when synchronizing a file" in {

      // Simulate downloads local directory (no photo1.jpg)
      val localDirectoryOfDownloads = TestHelper.createTmpDir("output")

      // Simulate camera telling that photo1.jpg is available
      val cameraClientMock = mock[CameraClient]
      val remoteFilesMock = Seq(FileInfo(OlympFolder, "photo1.jpg", 100L))
      cameraClientMock.listFiles().returns(remoteFilesMock)
      cameraClientMock.downloadFile(OlympFolder, "photo1.jpg", localDirectoryOfDownloads).
        returns(Failure(new RuntimeException()))

      // The manager should download the file photo2.jpg
      val fm = new FilesManager(cameraClientMock, Config(localDirectoryOfDownloads))
      fm.sync() mustEqual Seq.empty[FileInfo]
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

