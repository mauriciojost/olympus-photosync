package org.mauritania.photosync.starter

import java.io.File
import java.net.InetSocketAddress

import com.sun.net.httpserver.HttpServer
import org.mauritania.photosync.olympus.sync.TempDir
import org.specs2.mutable.Specification

import scala.io.Source


class StarterSpec extends Specification with TempDir {

  "The starter" should {

    "throw exception in case of bad arguments" in {
      Starter.main(Array("--bad-argument")) must throwA[IllegalArgumentException]
    }

    "works correctly under normal conditions" in {
      withTmpDir { tmp =>
        val expectedDownloadedFile = new File(tmp, new File("100OLYMP", "OR.ORF").getPath)
        val notExpectedDownloadedFile = new File(tmp, new File("100OLYMP", "VI.AVI").getPath)

        val server = HttpServer.create(new InetSocketAddress(8085), 0)
        server.createContext("/", new HttpCameraMock())
        server.setExecutor(null)
        server.start()

        Starter.main(
          Array(
            "--server-name", "localhost",
            "--server-port", "8085",
            "--file-patterns", "*.ORF",
            "--output-directory", tmp.getAbsolutePath
          )
        )

        server.stop(0)

        expectedDownloadedFile.exists() must beTrue
        notExpectedDownloadedFile.exists() must beFalse
      }
    }

    "works correctly with CGI-only camera (newer cameras like TG-7)" in {
      withTmpDir { tmp =>
        val expectedFile1 = new File(tmp, new File("100OMSYS", "P5180001.JPG").getPath)
        val expectedFile2 = new File(tmp, new File("100OMSYS", "P5230002.JPG").getPath)

        val server = HttpServer.create(new InetSocketAddress(8086), 0)
        server.createContext("/", new HttpCameraCgiMock())
        server.setExecutor(null)
        server.start()

        Starter.main(
          Array(
            "--commandline",
            "--server-name", "localhost",
            "--server-port", "8086",
            "--output-directory", tmp.getAbsolutePath
          )
        )

        server.stop(0)

        expectedFile1.exists() must beTrue
        expectedFile2.exists() must beTrue
      }
    }

  }

  "creates correctly the init config file" in {
    withTmpDir { tmp =>
      val expectedDownloadedFile = new File("application.conf")
      val template = new File("src/main/resources/application.conf")

      expectedDownloadedFile.deleteOnExit()

      Starter.main(Array("--init-config"))

      val actualContent = Source.fromFile(expectedDownloadedFile).getLines().toList
      val expectedContent = Source.fromFile(template).getLines().toList

      expectedDownloadedFile.exists() must beTrue
      actualContent mustEqual expectedContent

    }
  }

}

