package org.mauritania.photosync.starter

import com.sun.net.httpserver.{HttpExchange, HttpHandler}
import org.slf4j.LoggerFactory

import scala.reflect.io.Streamable

class HttpCameraMock extends HttpHandler {

  val logger = LoggerFactory.getLogger(this.getClass)

  val RootResponse = """
...
</style>
<script type="text/javascript">
wlansd = new Array();
wlansd[0]="/DCIM/,100OLYMP,0,0,0,0";
...
"""

  val FolderResponse = """
...
</style>
<script type="text/javascript">
wlansd = new Array();
wlansd[0]="/DCIM/100OLYMP/,OR.ORF,15441739,0,18229,43541";
wlansd[0]="/DCIM/100OLYMP/,VI.AVI,95441739,0,18229,43541";
...
                     """

  val CgiRootResponse =
    """VER_100
      |/DCIM,100OLYMP,0,16,18229,43541""".stripMargin

  val CgiFolderResponse =
    """VER_100
      |/DCIM/100OLYMP,OR.ORF,15441739,0,18229,43541
      |/DCIM/100OLYMP,VI.AVI,95441739,0,18229,43541""".stripMargin

  val FileContentResponse = "=== PHOTO SAMPLE ==="
  val fileThumbnailResponse = Streamable.bytes(this.getClass.getResource("/org/mauritania/photosync/100OLYMP/thumbnails/OR.ORF").openStream())
  val HttpOk = 200
  val HttpNotFound = 404

  def handle(t: HttpExchange) = {
    val uri = t.getRequestURI.toString
    logger.debug(s"Requested: $uri")
    uri match {
      case "/DCIM" => sendResponse(t, HttpOk, RootResponse.getBytes, true)
      case "/DCIM/100OLYMP" => sendResponse(t, HttpOk, FolderResponse.getBytes, true)
      case "/DCIM/100OLYMP/OR.ORF" => sendResponse(t, HttpOk, FileContentResponse.getBytes, true)
      case "/get_imglist.cgi?DIR=/DCIM" => sendResponse(t, HttpOk, CgiRootResponse.getBytes, true)
      case "/get_imglist.cgi?DIR=/DCIM/100OLYMP" => sendResponse(t, HttpOk, CgiFolderResponse.getBytes, true)
      case "/get_thumbnail.cgi?DIR=/DCIM/100OLYMP/OR.ORF" => sendResponse(t, HttpOk, fileThumbnailResponse, false)
      case "/get_thumbnail.cgi?DIR=/DCIM/100OLYMP/VI.AVI" => sendResponse(t, HttpOk, fileThumbnailResponse, false)
      case uri => sendResponse(t, HttpOk, s"Unexpected url: $uri".getBytes, true)
    }
  }

  private def sendResponse(t: HttpExchange, statusCode: Int, response: Array[Byte], logResponse: Boolean) = {
    if (logResponse) {
      logger.debug(s"Response: ${new String(response)}...")
    }
    t.sendResponseHeaders(statusCode, response.length)
    val os = t.getResponseBody
    os.write(response)
    os.close()
  }

}
