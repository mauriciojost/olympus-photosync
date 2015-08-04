package org.mauritania.photosync.main

import java.io.File

import com.typesafe.scalalogging.Logger
import org.mauritania.photosync.olympus.api.Api
import org.slf4j.LoggerFactory

case class Config(hostname: String = "oishare",  debug: Boolean = false, mode: String = "", kwargs: Map[String,String] = Map())

object Starter {

  val logger = Logger(LoggerFactory.getLogger(this.getClass))

  // TODO add parameters
  // TODO add README.md
  // TODO add tasks to generate binaries
  // TODO try to use mockups? (to learn)

  def main(args: Array[String]): Unit = {

    val parser = new scopt.OptionParser[Config]("scopt") {
      head("scopt", "3.x")
      opt[String]('h', "hostname") required() valueName("<hostname>") action { (x, c) =>
        c.copy(hostname = x) } text("hostname or IP address of the camera server")
      opt[Map[String,String]]("kwargs") valueName("k1=v1,k2=v2...") action { (x, c) =>
        c.copy(kwargs = x) } text("other arguments")
      opt[Unit]("debug") hidden() action { (_, c) =>
        c.copy(debug = true) } text("this option is hidden in the usage text")
      note("some notes.\n")
      help("help") text("prints this usage text")
    }

    parser.parse(args, Config()) match {
      case Some(config) =>

        logger.info("Starting...")


        val cameraApi = new Api(serverName = config.hostname)

        if (!cameraApi.isReachable()) {
          logger.error("Cannot find camera API: are you connected to its WIFI service?")
          System.exit(-1)
        }

        val files = cameraApi.listFileIdsAndSize()

        files.foreach {
          fileIdAndSize => {

            val downloaded = cameraApi.fileHasBeenAlreadyDownloaded(fileIdAndSize._1)
            logger.debug(s"File $fileIdAndSize._1 with size $fileIdAndSize._2 downloaded $downloaded")
            if (!downloaded) {
              cameraApi.downloadFile(fileIdAndSize._1, fileIdAndSize._1)
            }
          }
        }

        logger.debug("Done.")

      case None =>
      // Bad arguments
    }

  }

}
