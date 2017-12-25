package org.mauritania.photosync.olympus.client

import java.net.URL

import org.mauritania.photosync.olympus.sync.FileInfo
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import java.io.File
import org.mauritania.photosync.TestHelper
import scala.collection.immutable.Seq

class CameraClientSpec extends Specification with Mockito {

  "The camera server client" should {

    "correctly list remote files when empty from OMD E-M10" in {
      val cc = new CameraClient(
        generateClientCameraConfig(
          "00-root-em10-nofolder.html",
          specialMappingUrlTranslator("00-root-em10-nofolder.html", "0000-em10-no-files.html")
        )
      )
      cc.listFiles() mustEqual Seq.empty[FileInfo]
    }


    "correctly list remote files when many remote files from OMD E-M10" in {
      val cc = new CameraClient(
        generateClientCameraConfig(
          "01-root-em10-onefolder.html",
          specialMappingUrlTranslator("01-root-em10-onefolder.html", "0001-em10-many-files.html")
        )
      )
      cc.listFiles().size mustEqual 135
    }


    "correctly list remote files and download when having one remote file from OMD E-M10" in {
      val cc = new CameraClient(
        generateClientCameraConfig(
          "01-root-em10-onefolder.html",
          specialMappingUrlTranslator("01-root-em10-onefolder.html", "0002-em10-downloadable-file.html")
        )
      )

      // wlansd[0]="/DCIM/100OLYMP/,OR.ORF,15441739,0,18229,43541";
      cc.listFiles() mustEqual Seq(FileInfo("100OLYMP", "OR.ORF", 15441739L, 18229))


      val outputDirectory = TestHelper.createTmpDir("output")
      cc.downloadFile("100OLYMP", "OR.ORF", outputDirectory)

      val downloadedFileToCheck = new File(new File(outputDirectory, "100OLYMP"), "OR.ORF")

      downloadedFileToCheck.exists mustEqual true

      downloadedFileToCheck.deleteOnExit()

      done

    }

  }

  def generateClientCameraConfig(rootHtmlName: String, mapping: URL => URL): CameraClientConfig = {
    CameraClientConfig(
      serverProtocol = "file",
      serverName = "localhost",
      serverBaseUrl = "./src/test/resources/org/mauritania/photosync/" + rootHtmlName,
      serverPort = 0,
      serverPingTimeout = 0,
      fileRegex = """wlan.*=.*,(.*),(\d+),(\d+),(\d+),(\d+).*""",
      urlTranslator = Some(mapping)
    )

  }

  def specialMappingUrlTranslator(rootHtmlName: String, folderHtmlName: String)(url: URL): URL = {
    // Tricky translations to mock up responses of the camera
    def transformRelativeUrl(file: String) = {
      file
        .replace(rootHtmlName + "/100OLYMP", "100OLYMP/" + folderHtmlName)
        .replace(folderHtmlName + "/", "photosample/")
    }
    val relativeUrl = url.getFile
    val newRelativeUrl = transformRelativeUrl(relativeUrl)
    new URL(url.getProtocol, url.getHost, url.getPort, newRelativeUrl)
  }

}
