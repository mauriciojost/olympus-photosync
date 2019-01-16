package org.mauritania.photosync.starter

import org.mauritania.photosync.olympus.client.CameraClientConfig
import org.mauritania.photosync.olympus.sync.FileInfoFilter
import org.specs2.mutable.Specification
import scala.collection.immutable.Seq

class ArgumentsParserBuilderSpec extends Specification {

  val parser = ArgumentsParserBuilder.Parser

  "The command line arguments parser" should {

    "parse server name" in {
      val commandLineArguments = Seq("--server-name", "myhostname")
      val result = parser.parse(commandLineArguments, getDefaultPhotosyncConfig)
      result.get.client.serverName mustEqual "myhostname"
    }

    "parse server port" in {
      val commandLineArguments = Seq("--server-port", "1177")
      val result = parser.parse(commandLineArguments, getDefaultPhotosyncConfig)
      result.get.client.serverPort mustEqual 1177
    }

    "parse server base url" in {
      val commandLineArguments = Seq("--server-base-url", "mybase")
      val result = parser.parse(commandLineArguments, getDefaultPhotosyncConfig)
      result.get.client.serverBaseUrl mustEqual "mybase"
    }

    "parse output directory" in {
      val commandLineArguments = Seq("--output-directory", "myoutput")
      val result = parser.parse(commandLineArguments, getDefaultPhotosyncConfig)
      result.get.outputDirectory mustEqual "myoutput"
    }

    "parse configuration file" in {
      ArgumentsParserBuilder.loadConfigFile mustEqual PhotosyncConfig(
        client = CameraClientConfig(
          serverProtocol = "http",
          serverName = "192.168.0.10",
          serverPort = 80,
          serverBaseUrl = "/DCIM",
          fileRegex = "wlan.*=.*,(.*),(\\d+),(\\d+),(\\d+),(\\d+).*",
          preserveCreationDate = true,
          urlTranslator = None,
          forcedTimezone = None
        ),
        mediaFilter = FileInfoFilter.Criteria.Bypass,
        outputDirectory = "output",
        guiMode = false,
        commandLineMode = false,
        initConfig = false,
        shutDownAfterSync = false
      )

    }

  }

  def getDefaultPhotosyncConfig: PhotosyncConfig = {
    PhotosyncConfig(
      client = CameraClientConfig(
        serverProtocol = "",
        serverName = "",
        serverPort = 0,
        serverBaseUrl = "",
        fileRegex = "",
        preserveCreationDate = false,
        urlTranslator = None,
        forcedTimezone = None
      ),
      mediaFilter = FileInfoFilter.Criteria.Bypass,
      outputDirectory = "",
      guiMode = false,
      commandLineMode = false,
      initConfig = false,
      shutDownAfterSync = false
    )

  }

}

