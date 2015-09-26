package org.mauritania.photosync.olympus.client

import scala.util.matching.Regex

case class CameraClientConfig(

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

)

