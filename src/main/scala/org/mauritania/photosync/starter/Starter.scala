package org.mauritania.photosync.starter

import java.io.File

import org.mauritania.photosync.Constants
import org.mauritania.photosync.olympus.PhotosyncConfig
import org.mauritania.photosync.olympus.client.CameraClient
import org.mauritania.photosync.olympus.sync.FilesManager
import org.mauritania.photosync.starter.ArgumentsParserBuilder._
import org.slf4j.LoggerFactory

object Starter {
  val logger = LoggerFactory.getLogger(this.getClass)

  def main(args: Array[String]): Unit = {
    try {
      startApp(args)
    } catch {
      case e: Exception =>
        logger.error("Application failed", e)
        throw e
    }
  }

  def startApp(args: Array[String]): Unit = {

    logger.info(s"Version: ${Constants.Version}")

    val fileConfiguration = loadConfigFile

    logger.info(s"Loading file configuration: $fileConfiguration")

    buildParser.parse(args, fileConfiguration) match {
      case Some(config) if !config.gui => startSynchronization(config)
      case Some(config) if config.gui => GuiStarter.main(args)
      case None =>  throw new IllegalArgumentException("Bad command line arguments!")
    }

  }

  def startSynchronization(config: PhotosyncConfig): Unit = {

    logger.info(s"Using configuration ($config)...")
    val cameraClient = new CameraClient(config.client)
    val managerConfig = FilesManager.Config(
      outputDir = new File(config.outputDirectory),
      mediaFilter = config.mediaFilter
    )
    val manager = new FilesManager(cameraClient, managerConfig)

    logger.info("Synchronizing media from camera -> PC...")
    manager.sync()

    logger.info("Synchronized!")

  }

}
