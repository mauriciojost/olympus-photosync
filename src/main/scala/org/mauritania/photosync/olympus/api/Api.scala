package org.mauritania.photosync.olympus.api

import java.net.InetAddress

object Api {

  val DEFAULT_CAMERA_NETWORK_NAME = "oishare"
  val DEFAULT_CAMERA_PING_TIMEOUT = 3000

  def getCameraIp() : InetAddress = {
    InetAddress.getByName(DEFAULT_CAMERA_NETWORK_NAME)
  }

  def isReachable() : Boolean = {
    InetAddress.getByName(DEFAULT_CAMERA_NETWORK_NAME).isReachable(DEFAULT_CAMERA_PING_TIMEOUT)
  }

  def listFileIds() : Iterable[String] = {
    List()
  }

  def downloadFile(fileId: String) : Iterable[String] = {
    List()
  }

}
