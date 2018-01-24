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

  val FileContentResponse = "=== PHOTO SAMPLE ==="
  val fileThumbnailResponse = Streamable.bytes(this.getClass.getResource("/org/mauritania/photosync/100OLYMP/thumbnails/OR.ORF").openStream())
  val HttpOk = 200

  def handle(t: HttpExchange) = {
    val uri = t.getRequestURI.toString
    logger.debug(s"Requested: $uri")
    uri match {
      case "/DCIM" => sendRootResponse(t, RootResponse.getBytes)
      case "/DCIM/100OLYMP" => sendRootResponse(t, FolderResponse.getBytes)
      case "/DCIM/100OLYMP/OR.ORF" => sendRootResponse(t, FileContentResponse.getBytes)
      case "/get_thumbnail.cgi?DIR=100OLYMP/OR.ORF" => sendRootResponse(t, fileThumbnailResponse)
      case "/get_thumbnail.cgi?DIR=100OLYMP/VI.AVI" => sendRootResponse(t, fileThumbnailResponse)
      case uri => sendRootResponse(t, s"Unexpected url: $uri".getBytes)
    }
  }

  private def sendRootResponse(t: HttpExchange, response: Array[Byte]) = {
    logger.debug(s"Response: $response")
    t.sendResponseHeaders(HttpOk, response.length)
    val os = t.getResponseBody
    os.write(response)
    os.close()
  }

}
