package org.mauritania.photosync.olympus.client

import java.io.File
import java.net.URL
import java.time._

import org.mauritania.photosync.TestHelper
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification

import scala.collection.immutable.Seq

class CameraClientSpec extends Specification with Mockito {

  val ParisZone = ZoneId.of("Europe/Paris")
  val ADateTime = ZonedDateTime.of(2015, 9, 21, 21, 16, 21, 0, ParisZone)
  val ServerBaseUrl = "./src/test/resources/org/mauritania/photosync/"

  val DefaultCameraClientConfig = CameraClientConfig(
    serverProtocol = "file",
    serverName = "localhost",
    serverBaseUrl = "/DCIM",
    serverPort = 0,
    fileRegex = """wlan.*=.*,(.*),(\d+),(\d+),(\d+),(\d+).*""",
    preserveCreationDate = true,
    urlTranslator = None,
    forcedTimezone = Some(
      // the app figures out the zone of the PC, which must be the same as the one in the camera
      ParisZone
    )
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
      remoteFiles.head.thumbnailUrl must beSome

      val outputDirectory = TestHelper.createTmpDir("output")

      val downloaded = cc.downloadFile(remoteFiles.head, outputDirectory)
      downloaded must aSuccessfulTry[File]

      val downloadedFileToCheck = new File(new File(outputDirectory, "100OLYMP"), "OR.ORF")

      downloadedFileToCheck.exists mustEqual true

      downloadedFileToCheck.deleteOnExit()

      done

    }

    "correctly list remote files and tell their date/time" in {
      val cc = new CameraClient(
        generateClientCameraConfig(
          "01-root-em10-onefolder.html",
          specialMappingUrlTranslator("01-root-em10-onefolder.html", "0003-em10-downloadable-file-with-dates.html")
        )
      )

      // Picture taken at 21h59 on Jan14 2019, in Nice, France
      // wlansd[0]="/DCIM/100OLYMP/,OR.ORF,15441739,0,18229,43541";
      val remoteFiles = cc.listFiles()
      remoteFiles.size mustEqual 1
      remoteFiles.head.folder mustEqual "100OLYMP"
      remoteFiles.head.name mustEqual "P1144737.ORF"
      remoteFiles.head.date mustEqual 20014
      remoteFiles.head.time mustEqual 44917
      remoteFiles.head.thumbnailUrl must beSome

      val outputDirectory = TestHelper.createTmpDir("output")

      val downloaded = cc.downloadFile(remoteFiles.head, outputDirectory)
      downloaded must aSuccessfulTry[File]

      val downloadedFileToCheck = new File(new File(outputDirectory, "100OLYMP"), "P1144737.ORF")

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
      remoteFiles.head.time mustEqual 43541
      remoteFiles.head.humanDateTime mustEqual ADateTime.toLocalDateTime

      val outputDirectory = TestHelper.createTmpDir("output")

      val downloaded = cc.downloadFile(remoteFiles.head, outputDirectory)
      downloaded must aSuccessfulTry[File]

      val downloadedFileToCheck = new File(new File(outputDirectory, "100OLYMP"), "OR.ORF")

      downloadedFileToCheck.exists mustEqual true
      downloadedFileToCheck.lastModified() mustEqual ADateTime.toEpochSecond * 1000

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
