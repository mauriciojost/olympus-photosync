package org.mauritania.photosync.main

import org.mauritania.photosync.olympus.api.Api._

object Starter {

  def main(args: Array[String]) : Unit = {

    println("Starting...")

    val cameraIp = getCameraIp()
    val isCameraReachable = isReachable()
    val fileIds = listFileIds()

    println(cameraIp)
    println(isCameraReachable)
    println(fileIds)

    println("Done.")

  }

}
