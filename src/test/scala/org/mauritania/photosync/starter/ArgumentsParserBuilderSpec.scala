package org.mauritania.photosync.starter

import org.mauritania.photosync.olympus.PhotosyncConfig
import org.specs2.mutable._

class ArgumentsParserBuilderSpec extends Specification {

  val parser = ArgumentsParserBuilder.buildParser()

  "The command line arguments parser" should {

    "parse server name" in {
      val commandLineArguments = Seq("--server-name", "myhostname")
      val result = parser.parse(commandLineArguments, PhotosyncConfig())
      result.get.client.serverName mustEqual("myhostname")
    }

    "parse server port" in {
      val commandLineArguments = Seq("--server-port", "1177")
      val result = parser.parse(commandLineArguments, PhotosyncConfig())
      result.get.client.serverPort mustEqual(1177)
    }

    "parse server base url" in {
      val commandLineArguments = Seq("--server-base-url", "mybase")
      val result = parser.parse(commandLineArguments, PhotosyncConfig())
      result.get.client.serverBaseUrl mustEqual("mybase")
    }

    "parse server folder name" in {
      val commandLineArguments = Seq("--server-folder-name", "myfolder")
      val result = parser.parse(commandLineArguments, PhotosyncConfig())
      result.get.client.serverFolderName mustEqual("myfolder")
    }

    "parse output directory" in {
      val commandLineArguments = Seq("--output-directory", "myoutput")
      val result = parser.parse(commandLineArguments, PhotosyncConfig())
      result.get.outputDirectory mustEqual("myoutput")
    }

    // TODO write missing tests

  }

}

