package org.mauritania.photosync.main

import com.typesafe.scalalogging.Logger
import org.mauritania.photosync.olympus.api.Api
import org.slf4j.LoggerFactory

object Starter {

  val logger = Logger(LoggerFactory.getLogger(this.getClass))

  // TODO add parameters
  // TODO add README.md
  // TODO add tasks to generate binaries
  // TODO try to use mockups? (to learn)

  def main(args: Array[String]): Unit = {

    logger.info("Starting...")

    val cameraApi = new Api()

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

  }

}
