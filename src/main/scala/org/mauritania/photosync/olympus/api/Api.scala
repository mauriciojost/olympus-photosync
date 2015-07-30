package org.mauritania.photosync.olympus.api

import java.io.{File, FileOutputStream}
import java.net.{URL, InetAddress}
import java.nio.channels.Channels
import java.nio.file.{Files, Paths}
import scala.io.Source
import scala.util.matching.Regex

class Api (

  val serverProtocol : String = "http",

  val serverName : String = "oishare",

  val serverPort : Int = 80,

  val serverRelativeUrl : String = "/DCIM/100OLYMP/",

  val serverPingTimeout : Int = 2000,

  // Regex used to identify files from the server's response
  // A valid line:   wlansd[17]="/DCIM/100OLYMP,P7290009.JPG,278023,0,18173,42481";
  val fileRegex : Regex = """.*=.*,(\w+\.\w+),(\d+),.*""".r

  ) {

  var latestFileIdsAndSize : List[(String, Long)] = List()
  var latestServerIpAddress : InetAddress = InetAddress.getByName(serverName)


  def getCameraIp() : InetAddress = {
    latestFileIdsAndSize = listFileIdsAndSize()
    latestServerIpAddress = InetAddress.getByName(serverName)
    latestServerIpAddress
  }

  def isReachable() : Boolean = {
    getCameraIp().isReachable(serverPingTimeout)
  }

  def listFileIdsAndSize() : List[(String, Long)] = {

    val html = Source.fromURL(
      new URL(serverProtocol, latestServerIpAddress.getHostAddress, serverPort, serverRelativeUrl))
    //val html = Source.fromURL("file:///tmp/olympus-files-report.txt")

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

    println("Local file size: " + localSize + " (real size is " + remoteSize + ")")
    localSize == remoteSize
  }

  private def getLocalFilesAndSizes() : List[(String, Long)] = {
    val files = new File(".").listFiles()
    val filesAndSizes = files.map(file => (file.getName, file.length())).toList
    println("Local files: " + filesAndSizes.length)
    filesAndSizes
  }

}

