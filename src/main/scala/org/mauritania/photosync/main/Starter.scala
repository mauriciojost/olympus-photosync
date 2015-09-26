package org.mauritania.photosync.main

import org.mauritania.photosync.olympus.api.CameraApi
import org.mauritania.photosync.olympus.sync.FilesManager
import org.slf4j.LoggerFactory

case class Config(hostname: String = "oishare", debug: Boolean = false, mode: String = "", kwargs: Map[String, String] = Map())

object Starter {

  type FileInfo = (String, Long)

  val logger = LoggerFactory.getLogger(this.getClass)

  // TODO add parameters
  // TODO add README.md
  // TODO add tasks to generate binaries
  // TODO try to use mockups? (to learn)

  def main(args: Array[String]): Unit = {

    val parser = new scopt.OptionParser[Config]("photosync") {
      head("photosync", "0.1")
      opt[String]('h', "hostname") valueName ("<hostname>") action { (x, c) =>
        c.copy(hostname = x)
      } text ("hostname or IP address of the camera server")
      note("some notes.\n")
      help("help") text ("Please read README.md file.")
    }

    parser.parse(args, Config()) match {
      case Some(config) =>

        logger.info("Starting...")

        val cameraApi = new CameraApi(
          serverName = config.hostname
        )

        val manager = new FilesManager(cameraApi)

        logger.info("Synchronizing...")

        manager.sync()

        logger.debug("Done.")

      case None =>
      // Bad arguments
    }

  }

}
