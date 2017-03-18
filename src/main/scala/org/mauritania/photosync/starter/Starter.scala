package org.mauritania.photosync.starter

import java.io.File

import org.mauritania.photosync.olympus.PhotosyncConfig
import org.mauritania.photosync.olympus.client.CameraClient
import org.mauritania.photosync.olympus.sync.FilesManager
import org.mauritania.photosync.starter.ArgumentsParserBuilder._
import org.slf4j.LoggerFactory

object Starter {
  val logger = LoggerFactory.getLogger(this.getClass)

  def main(args: Array[String]): Unit = {

    logger.debug("DEBUG")
    logger.info("INFO")
    logger.warn("WARN")
    logger.error("ERROR")


    val fileConfiguration = loadConfigFile()

    logger.info("Loading file configuration ({})...", fileConfiguration)

    buildParser().parse(args, fileConfiguration) match {
      case Some(config) => startSynchronization(config)
      case None =>  throw new IllegalArgumentException("Bad command line arguments!")
    }

  }

  def startSynchronization(config: PhotosyncConfig): Unit = {

    logger.info("Using configuration ({})...", config)
    val cameraClient = new CameraClient(config.client, identity)
    val manager = new FilesManager(cameraClient, new File(config.outputDirectory))

    logger.info("Synchronizing media from camera -> PC...")
    manager.sync()

    logger.info("Synchronized!")

  }

}
