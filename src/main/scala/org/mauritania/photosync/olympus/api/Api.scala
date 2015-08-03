package org.mauritania.photosync.olympus.api

import java.io.{File, FileOutputStream}
import java.net.{URL, InetAddress}
import java.nio.channels.Channels
import com.typesafe.scalalogging.LazyLogging

import scala.io.Source
import scala.util.matching.Regex

class Api (
          // TODO document these variables
  val serverProtocol : String = "http",

  val serverName : String = "oishare",

  val serverPort : Int = 80,

  val serverRelativeUrl : String = "/DCIM/100OLYMP/",

  val serverPingTimeout : Int = 2000,

  // Regex used to identify files from the server's response
  // A valid line:   wlansd[17]="/DCIM/100OLYMP,P7290009.JPG,278023,0,18173,42481";
  val fileRegex : Regex = """.*=.*,(\w+\.\w+),(\d+),.*""".r

  ) extends LazyLogging {

  var latestFileIdsAndSize : List[(String, Long)] = List()
  var latestServerIpAddress : InetAddress = InetAddress.getByName(serverName)


  def getCameraIp() : InetAddress = {
    // TODO fix this mess
    latestFileIdsAndSize = listFileIdsAndSize()
    latestServerIpAddress = InetAddress.getByName(serverName)
    latestServerIpAddress
  }

  def isReachable() : Boolean = {
    logger.debug("isReachable")
    getCameraIp().isReachable(serverPingTimeout)
  }

  // TODO parametrize destination folder
  def listFileIdsAndSize() : List[(String, Long)] = {

    logger.debug("listFileIdsAndSize")

    val html = Source.fromURL(
      new URL(serverProtocol, latestServerIpAddress.getHostAddress, serverPort, serverRelativeUrl))

    val htmlLines = html.getLines()
    val fileIdsAndSize = htmlLines.flatMap(
      line =>
        line match {
          case fileRegex(fileId, fileSizeBytes) => Some((fileId, fileSizeBytes.toLong))
          case _ => None
        }
    ).toList

    latestFileIdsAndSize = fileIdsAndSize

    fileIdsAndSize

  }

  def downloadFile(fileIdToDownload: String, localDestinationFilename: String) {

    logger.debug("downloadFile")

    val urlSourceFile = 
      new URL(
        serverProtocol, 
        serverName, 
        serverPort, 
        serverRelativeUrl.concat(fileIdToDownload))

    val rbc = Channels.newChannel(urlSourceFile.openStream());
    val fos = new FileOutputStream(localDestinationFilename);
    fos.getChannel().transferFrom(rbc, 0, Long.MaxValue);

  }
  
  def fileHasBeenAlreadyDownloaded(fileToDownload: String) : Boolean = {
    val locals = getLocalFilesAndSizes()
    val localSize : Long = locals.toMap.get(fileToDownload).getOrElse(0)
    val remotes = latestFileIdsAndSize
    val remoteSize : Long = latestFileIdsAndSize.toMap.get(fileToDownload).getOrElse(0)

    logger.info("Local file size: $localSize (real size is $remoteSize)")
    localSize == remoteSize
  }

  // TODO add tests
  private def getLocalFilesAndSizes() : List[(String, Long)] = {
    val files = new File(".").listFiles()
    val filesAndSizes = files.map(file => (file.getName, file.length())).toList
    filesAndSizes
  }

}

