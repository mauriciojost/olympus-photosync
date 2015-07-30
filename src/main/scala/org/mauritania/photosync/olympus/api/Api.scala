package org.mauritania.photosync.olympus.api

import java.io.FileOutputStream
import java.net.{URL, InetAddress}
import java.nio.channels.Channels
import scala.io.Source

object Api {

  val defaultServerProtocol = "http"
  val defaultServerName = "oishare"
  val defaultRelativeUrl = "/DCIM/100OLYMP/"
  val defaultServerPort = 80
  val defaultServerPingTimeout = 2000

  // Regex used to identify files from the server's response
  // A valid line:   wlansd[17]="/DCIM/100OLYMP,P7290009.JPG,278023,0,18173,42481";
  val FILE_ID_REGEX = """.*=.*,(\w+\.\w+),(\d+),.*""".r

  // Regex used to identify files from the server's response
  // A valid line:   wlansd[17]="/DCIM/100OLYMP,P7290009.JPG,278023,0,18173,42481";
  val FILE_ID_REGEX = """.*=.*,(\w+\.\w+),(\d+),.*""".r

  var latestIpAddress : String = "192.168.0.10"

  var latestFileIdsAndSize : List[(String, Int)] = List()

  def getCameraIp() : InetAddress = {
    val inetAddress = InetAddress.getByName(defaultServerName)
    latestIpAddress = inetAddress.getHostAddress
    inetAddress
  }

  def isReachable() : Boolean = {
    InetAddress.getByName(defaultServerName).isReachable(defaultServerPingTimeout)
  }

  def listFileIdsAndSize() : List[(String, Int)] = {

    val html = Source.fromURL(
      new URL(defaultServerProtocol, defaultServerName, defaultServerPort, defaultRelativeUrl))
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

  def downloadFile(fileIdToDownload: String, localDestinationFilename: String) {

    val urlSourceFile = 
      new URL(
        defaultServerProtocol, 
        defaultServerName, 
        defaultServerPort, 
        defaultRelativeUrl.concat(fileIdToDownload))

    val rbc = Channels.newChannel(urlSourceFile.openStream());
    val fos = new FileOutputStream(localDestinationFilename);
    fos.getChannel().transferFrom(rbc, 0, Long.MaxValue);

  }
  
  //private def fileHasBeenAlreadyDownloaded(fileToDownload: String) : Boolean = {
    
  //}
  

}

