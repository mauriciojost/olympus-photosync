package org.mauritania.photosync.olympus.api

import java.net.{URL, InetAddress}
import scala.io.Source

object Api {

  val DEFAULT_CAMERA_NETWORK_PROTOCOL = "http"
  val DEFAULT_CAMERA_NETWORK_NAME = "oishare"
  val DEFAULT_CAMERA_NETWORK_URL = "/DCIM/100OLYMP/"
  val DEFAULT_CAMERA_NETWORK_PORT = 80
  val DEFAULT_CAMERA_PING_TIMEOUT = 3000

  //                  wlansd[17]="/DCIM/100OLYMP,P7290009.JPG,278023,0,18173,42481";
  val FILE_ID_REGEX = """.*=.*,(\w+\.\w+),(\d+),.*""".r

  def getCameraIp() : InetAddress = {
    InetAddress.getByName(DEFAULT_CAMERA_NETWORK_NAME)
  }

  def isReachable() : Boolean = {
    InetAddress.getByName(DEFAULT_CAMERA_NETWORK_NAME).isReachable(DEFAULT_CAMERA_PING_TIMEOUT)
  }

  def listFileIdsAndSize() : List[(String, Int)] = {

    val html = Source.fromURL(
      new URL(DEFAULT_CAMERA_NETWORK_PROTOCOL, DEFAULT_CAMERA_NETWORK_NAME, DEFAULT_CAMERA_NETWORK_PORT, DEFAULT_CAMERA_NETWORK_URL))
    //val html = Source.fromURL("file:///tmp/olympus-files-report.txt")

    val htmlLines = html.getLines()
    htmlLines.flatMap(
      line =>
        line match {
          case FILE_ID_REGEX(fileId, fileSizeBytes) => Some((fileId, fileSizeBytes.toInt))
          case _ => None
        }
    ).toList

  }

  def downloadFile(fileId: String) : Iterable[String] = {
    List()
  }

}
