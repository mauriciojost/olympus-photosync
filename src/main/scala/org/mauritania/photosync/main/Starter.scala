package org.mauritania.photosync.main

import org.mauritania.photosync.main.ArgumentsParserBuilder._
import org.mauritania.photosync.olympus.PhotosyncConfig
import org.mauritania.photosync.olympus.client.CameraClient
import org.mauritania.photosync.olympus.sync.FilesManager
import org.slf4j.LoggerFactory

object Starter {
  val logger = LoggerFactory.getLogger(this.getClass)

  def main(args: Array[String]): Unit = {

    logger.debug("DEBUG")
    logger.info("INFO")
    logger.warn("WARN")
    logger.error("ERROR")

    buildParser().parse(args, PhotosyncConfig()) match {
      case Some(config) => startSynchronization(config)
      case None =>  throw new IllegalArgumentException("Bad arguments!")
    }

  }

  def startSynchronization(config: PhotosyncConfig): Unit = {

    logger.info("Starting synchronization camera -> PC...")
    val cameraClient = new CameraClient(config.client)
    val manager = new FilesManager(cameraClient, config.outputDirectory)

    logger.info("Synchronizing media from camera -> PC...")
    manager.sync()

    logger.info("Synchronized!")

  }

}
