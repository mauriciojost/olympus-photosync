package org.mauritania.photosync.olympus.client

import java.net.URL

import org.specs2.mock.Mockito
import org.specs2.mutable._
import java.io.File
import org.mauritania.photosync.TestHelper._

class CameraClientSpec extends Specification with Mockito {

  "The camera server client" should {

    "correctly list remote files when empty from OMD E-M10" in {
      val cc = new CameraClient(
        generateClientCameraConfig("org/mauritania/photosync/0000-em10-no-files.html"),
        identityUrlTranslator
      )
      cc.listFiles() mustEqual (Nil)
    }


    "correctly list remote files when many remote files from OMD E-M10" in {
      val cc = new CameraClient(
        generateClientCameraConfig("org/mauritania/photosync/0001-em10-many-files.html"),
        identityUrlTranslator
      )
      cc.listFiles().size mustEqual 135
    }


    "correctly list remote files and download when having one remote file from OMD E-M10" in {
      val cc = new CameraClient(
        generateClientCameraConfig("org/mauritania/photosync/0002-em10-downloadable-file.html"),
        specialMappingUrlTranslator // trick to test that a file is downloadable
      )
      cc.listFiles() mustEqual List(("OR.ORF", 15441739L))

      val outputDirectory = createTmpDir("output")
      cc.downloadFile("OR.ORF", outputDirectory)

      val downloadedFileToCheck = new File(outputDirectory, "OR.ORF")

      downloadedFileToCheck.exists mustEqual true

      downloadedFileToCheck.deleteOnExit()

      done

    }

  }

  def generateClientCameraConfig(file: String): CameraClientConfig = {
    CameraClientConfig(
      serverProtocol = "file",
      serverName = "localhost",
      serverBaseUrl = "./src/test/resources/",
      serverFolderName = file,
      serverPort = 0,
      serverPingTimeout = 0,
      fileRegex = """.*=.*,(\w+\.\w+),(\d+),.*""".r
    )

  }

  def specialMappingUrlTranslator(url: URL): URL = {
    def transformFileUrl(file: String) = file.replace("0002-em10-downloadable-file.html", "photosample")

    url match {
      case url if url.getFile.endsWith("ORF") => {
        new URL(url.getProtocol, url.getHost, url.getPort, transformFileUrl(url.getFile))
      }
      case url => url
    }
  }

  def identityUrlTranslator(url: URL): URL = url

}
