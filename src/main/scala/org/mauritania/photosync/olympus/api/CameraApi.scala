package org.mauritania.photosync.olympus.api

import java.io.{File, FileOutputStream}
import java.net.{URL, InetAddress}
import java.nio.channels.Channels

import org.slf4j.LoggerFactory

//import com.typesafe.scalalogging.LazyLogging

import scala.io.{BufferedSource, Source}
import scala.util.matching.Regex

class CameraApi(

           /**
            * Protocol to be used to contact the server.
            * Should not be changed.
            */
           val serverProtocol: String = "http",

           /**
            * Name or IP address of the server.
            * Should not be changed.
            */
           val serverName: String = "oishare",

           /**
            * Port of the http service provided by the server.
            * Should not be changed.
            */
           val serverPort: Int = 80,

           /**
            * Relative URL at which the server.
            * Should not be changed.
            */
           val serverBaseUrl: String = "/DCIM",

           /**
            * Folder to synchronize.
            */
           val serverFolderName: String = "100OLYMP",

           /**
            * Timeout (in ms) to be used when pinging the server.
            * Should not be changed.
            */
           val serverPingTimeout: Int = 2000,

           /*
            * Regex used to identify files from the server's response
            * Sample: wlansd[17]="/DCIM/100OLYMP,P7290009.JPG,278023,0,18173,42481";
            * Should not be changed.
            */
           val fileRegex: Regex = """.*=.*,(\w+\.\w+),(\d+),.*""".r

           ) {

  val logger = LoggerFactory.getLogger(this.getClass)

  def getServerIp(): InetAddress = {
    logger.debug("getServerIp")
    InetAddress.getByName(serverName)
  }

  def isServerReachable(): Boolean = {
    logger.debug("isReachable")
    getServerIp().isReachable(serverPingTimeout)
  }

  def listFiles(): List[(String, Long)] = {
    logger.debug("listFiles")
    generateFilesListFromHtml(Source.fromURL(new URL(serverProtocol, serverName, serverPort, generateRelativeUrl)))
  }


  def downloadFile(fileId: String, localDestination: File) {
    logger.debug("downloadFile")
    val urlSourceFile = new URL( serverProtocol, serverName, serverPort, generateRelativeUrl.concat(fileId))
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

  private def generateRelativeUrl: String = {
    serverBaseUrl + "/" + serverFolderName + "/"
  }

}

