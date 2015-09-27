package org.mauritania.photosync.olympus.sync

import org.specs2.mock.Mockito
import org.specs2.mutable._
import java.io.File
import org.mauritania.photosync.olympus.client.CameraClient
import java.nio.file.{Paths, Files}
import java.nio.charset.StandardCharsets

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
      val localDirectoryOfDownloads = createTmpDir("output")

      // Simulate camera telling there is one file to be downloaded
      val cameraClientMock = mock[CameraClient]
      val remoteFilesMock = List(("photo.jpg", 100L))
      cameraClientMock.listFiles().returns(remoteFilesMock)

      // The manager should tell the file has not been donwloaded yet
      val fm = new FilesManager(cameraClientMock, localDirectoryOfDownloads)
      fm.isDownloaded("photo.jpg", remoteFilesMock) mustEqual false

    }

    "correctly list locally downloaded files" in {

      // Simulate downloads local directory and some photos
      val localDirectoryOfDownloads = createTmpDir("output")
      touchFile(localDirectoryOfDownloads, "photo1.jpg")
      touchFile(localDirectoryOfDownloads, "photo2.jpg")

      // Simulate camera
      val cameraClientMock = mock[CameraClient]

      // The manager should tell the file's is bad and should be re-downloaded
      val fm = new FilesManager(cameraClientMock, localDirectoryOfDownloads)

      fm.listLocalFiles().sortBy(x => x._1) mustEqual List(("photo1.jpg", 0L), ("photo2.jpg", 0L)).sortBy(x => x._1)

    }

    "correctly synchronize a file in the camera not present locally" in {

      // Simulate downloads local directory and photo1.jpg
      val localDirectoryOfDownloads = createTmpDir("output")
      val photo1 = touchFile(localDirectoryOfDownloads, "photo1.jpg")
      val photo2 = new File(localDirectoryOfDownloads, "photo2.jpg")

      photo1.exists() mustEqual true
      photo2.exists() mustEqual false

      // Simulate camera telling that photo2.jpg is available
      val cameraClientMock = mock[CameraClient]
      val remoteFilesMock = List(("photo2.jpg", 100L))
      cameraClientMock.listFiles().returns(remoteFilesMock)
      cameraClientMock.downloadFile("photo2.jpg", localDirectoryOfDownloads).returns(touchFile(localDirectoryOfDownloads, "photo2.jpg"))

      // The manager should download the file photo2.jpg
      val fm = new FilesManager(cameraClientMock, localDirectoryOfDownloads)
      fm.sync() mustEqual (List(photo2))

      // There should be downloaded photo2.jpg and old photo1.jpg in local directory
      photo1.exists() mustEqual true
      photo2.exists() mustEqual true
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
    // list what are the remote files from an Olympus OMD E-M10

  }

  def simulateAlreadyDownloadedLocalFile: (File, String, File) = {
    // Simulate already downloaded local file
    val localFileSimulatingDownloaded = createTmpFile("photo.", 100L)
    val localFilenameSimulatingDownloaded = localFileSimulatingDownloaded.getName
    val localDirectoryOfDownloads = localFileSimulatingDownloaded.getParentFile
    (localFileSimulatingDownloaded, localFilenameSimulatingDownloaded, localDirectoryOfDownloads)
  }

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

