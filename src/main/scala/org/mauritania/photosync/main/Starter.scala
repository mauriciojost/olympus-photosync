package org.mauritania.photosync.main

import org.mauritania.photosync.olympus.api.Api._

object Starter {

  def main(args: Array[String]) : Unit = {

    println("Starting...")

    println("Camera IP: " + getCameraIp())
    println("Is reachable: " + isReachable())
    val files = listFileIdsAndSize()

    files.foreach(fileId => println("File '" + fileId._1 + "' with size '" + fileId._2 + "'"))

    downloadFile(files.head._1, files.head._1)

    println("Done.")

  }

}
