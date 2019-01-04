package org.mauritania.photosync.olympus.client

import java.net.URL

import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import java.io.File
import java.time.{LocalDateTime, LocalTime}

import org.mauritania.photosync.TestHelper

import scala.collection.immutable.Seq

class CameraClientSpec extends Specification with Mockito {

  val ADateTime = LocalDateTime.of(2015, 9, 21, 12, 5, 41)
  val ServerBaseUrl = "./src/test/resources/org/mauritania/photosync/"

  val DefaultCameraClientConfig = CameraClientConfig(
    serverProtocol = "file",
    serverName = "localhost",
    serverBaseUrl = "/DCIM",
    serverPort = 0,
    fileRegex = """wlan.*=.*,(.*),(\d+),(\d+),(\d+),(\d+).*""",
    preserveCreationDate = true,
    urlTranslator = None
  )

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
      val remoteFiles = cc.listFiles()
      remoteFiles.size mustEqual 1
      remoteFiles.head.folder mustEqual "100OLYMP"
      remoteFiles.head.name mustEqual "OR.ORF"
      remoteFiles.head.size mustEqual 15441739L
      remoteFiles.head.date mustEqual 18229
      remoteFiles.head.time mustEqual 43541
      remoteFiles.head.humanTime mustEqual LocalTime.ofSecondOfDay(43541)
      remoteFiles.head.thumbnailUrl must beSome

      val outputDirectory = TestHelper.createTmpDir("output")
      cc.downloadFile("100OLYMP", "OR.ORF", outputDirectory, ADateTime)

      val downloadedFileToCheck = new File(new File(outputDirectory, "100OLYMP"), "OR.ORF")

      downloadedFileToCheck.exists mustEqual true

      downloadedFileToCheck.deleteOnExit()

      done

    }

    "correctly download remote file preserving the creation date" in {
      val cc = new CameraClient(
        generateClientCameraConfig(
          "01-root-em10-onefolder.html",
          specialMappingUrlTranslator("01-root-em10-onefolder.html", "0002-em10-downloadable-file.html")
        )
      )

      // wlansd[0]="/DCIM/100OLYMP/,OR.ORF,15441739,0,18229,43541";
      val remoteFiles = cc.listFiles()
      remoteFiles.size mustEqual 1
      remoteFiles.head.date mustEqual 18229
      remoteFiles.head.humanDateTime mustEqual ADateTime

      val outputDirectory = TestHelper.createTmpDir("output")
      cc.downloadFile("100OLYMP", "OR.ORF", outputDirectory, ADateTime)

      val downloadedFileToCheck = new File(new File(outputDirectory, "100OLYMP"), "OR.ORF")

      downloadedFileToCheck.exists mustEqual true
      downloadedFileToCheck.lastModified() mustEqual ADateTime.toEpochSecond(CameraClient.LocalZoneOffset) * 1000

      downloadedFileToCheck.deleteOnExit()

      done

    }

    "correctly retrieve thumbnail URL" in {
      val cc = new CameraClient(DefaultCameraClientConfig)

      val thumbnailUrl = cc.thumbnailFileUrl("100OLYMP", "OR.ORF")

      thumbnailUrl mustEqual new URL("file", "localhost", 0, "/get_thumbnail.cgi?DIR=/DCIM/100OLYMP/OR.ORF")

    }

  }

  def generateClientCameraConfig(rootHtmlName: String, mapping: URL => URL): CameraClientConfig = {
    DefaultCameraClientConfig.copy(
      serverBaseUrl = ServerBaseUrl + rootHtmlName,
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
