package org.mauritania.photosync.olympus.client

import java.net.URL

case class CameraClientConfig(

  /**
   * Protocol to be used to contact the server.
   */
  serverProtocol: String,

  /**
   * Name or IP address of the server.
   */
  serverName: String,

  /**
   * Port of the http service provided by the server.
   */
  serverPort: Int,

  /**
   * Relative URL used to contact the server.
   */
  serverBaseUrl: String,

  /*
   * Regex used to identify files from the server's response
   * Sample: wlansd[17]="/DCIM/100OLYMP,P7290009.JPG,278023,0,18173,42481";
   */
  fileRegex: String,

  /*
   * Flag to preserve the creation date for each file, as provided by the server
   */
  preserveCreationDate: Boolean,

  /**
    * URL translator, used only for testing purposes
    */
  urlTranslator: Option[URL => URL] = None

) {
  def fileUrl(relativeUrl: String) = {
    val r = new URL(serverProtocol, serverName, serverPort, relativeUrl)
    urlTranslator.getOrElse((i: URL) => i)(r)
  }
}

