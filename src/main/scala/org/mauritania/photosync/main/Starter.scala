package org.mauritania.photosync.main

import org.mauritania.photosync.olympus.api.Api

object Starter {

  def main(args: Array[String]) : Unit = {

    println("Starting...")

    val api = new Api()

    println("Camera IP: " + api.getCameraIp())
    println("Is reachable: " + api.isReachable())
    val files = api.listFileIdsAndSize()

    files.foreach{
      fileIdAndSize => {
        println("File '" + fileIdAndSize._1 + "' with size '" + fileIdAndSize._2 + "'")

        val downloaded = api.fileHasBeenAlreadyDownloaded(fileIdAndSize._1)
        println("Donwloaded: " + downloaded)
        if (!downloaded) {
          api.downloadFile(fileIdAndSize._1, fileIdAndSize._1)
        }
      }
    }



    println("Done.")

  }

}
