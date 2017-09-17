package org.mauritania.photosync.olympus.client

case class CameraClientConfig(

  /**
   * Protocol to be used to contact the server.
   */
  val serverProtocol: String,

  /**
   * Name or IP address of the server.
   */
  val serverName: String,

  /**
   * Port of the http service provided by the server.
   */
  val serverPort: Int,

  /**
   * Relative URL used to contact the server.
   */
  val serverBaseUrl: String,

  /**
   * Timeout (in ms) to be used when pinging the server.
   */
  val serverPingTimeout: Int,

  /*
   * Regex used to identify files from the server's response
   * Sample: wlansd[17]="/DCIM/100OLYMP,P7290009.JPG,278023,0,18173,42481";
   */
  val fileRegex: String

)

