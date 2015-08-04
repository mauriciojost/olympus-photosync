package org.mauritania.photosync.olympus.api

import java.io.{File, FileOutputStream}
import java.net.{URL, InetAddress}
import java.nio.channels.Channels
//import com.typesafe.scalalogging.LazyLogging

import scala.io.{BufferedSource, Source}
import scala.util.matching.Regex

class CameraApi(
           /**
            * Protocol to be used to contact the server.
            */
           val serverProtocol: String = "http",

           /**
            * Name or IP address of the server.
            */
           val serverName: String = "oishare",

           /**
            * Port of the http service provided by the server.
            */
           val serverPort: Int = 80,

           /**
            * Relative URL at which the server replies with a list of files.
            */
           val serverRelativeUrl: String = "/DCIM/100OLYMP/",

           /**
            * Timeout (in ms) to be used when pinging the server.
            */
           val serverPingTimeout: Int = 2000,

           /*
            * Regex used to identify files from the server's response
            * Sample: wlansd[17]="/DCIM/100OLYMP,P7290009.JPG,278023,0,18173,42481";
            */
           val fileRegex: Regex = """.*=.*,(\w+\.\w+),(\d+),.*""".r

           ) /*extends LazyLogging*/ {

  def getServerIp(): InetAddress = {
    //logger.debug("getServerIp")
    InetAddress.getByName(serverName)
  }

  def isServerReachable(): Boolean = {
    //logger.debug("isReachable")
    getServerIp().isReachable(serverPingTimeout)
  }

  def listFiles(): List[(String, Long)] = {
    //logger.debug("listFiles")
    generateFilesListFromHtml(Source.fromURL(new URL(serverProtocol, serverName, serverPort, serverRelativeUrl)))
  }

  def downloadFile(fileId: String, localDestination: File) {
    //logger.debug("downloadFile")
    val urlSourceFile = new URL( serverProtocol, serverName, serverPort, serverRelativeUrl.concat(fileId))
    val rbc = Channels.newChannel(urlSourceFile.openStream());
    val fos = new FileOutputStream(new File(localDestination, fileId));
    fos.getChannel().transferFrom(rbc, 0, Long.MaxValue);
  }

  private def generateFilesListFromHtml(html: BufferedSource): List[(String, Long)] = {
    val htmlLines = html.getLines()
    val fileIdsAndSize = htmlLines.flatMap(
      htmlLineToBeParsed =>
        htmlLineToBeParsed match {
          case fileRegex(fileId, fileSizeBytes) => Some((fileId, fileSizeBytes.toLong))
          case _ => None
        }
    ).toList

    fileIdsAndSize
  }

}

