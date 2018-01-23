package org.mauritania.photosync.starter

import com.sun.net.httpserver.{HttpExchange, HttpHandler}
import org.slf4j.LoggerFactory

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
  //val fileThumbnailResponse = this.getClass.getResource("/org/mauritania/photosync/100OLYMP/thumbnails/OR.ORF").getFile.getBytes().mkString
  val HttpOk = 200

  def handle(t: HttpExchange) {
    val uri = t.getRequestURI.toString
    logger.debug(s"Requested: $uri")
    uri match {
      case "/DCIM" => sendRootResponse(t, RootResponse)
      case "/DCIM/100OLYMP" => sendRootResponse(t, FolderResponse)
      case "/DCIM/100OLYMP/OR.ORF" => sendRootResponse(t, FileContentResponse)
      //case "/get_thumbnail.cgi?DIR=100OLYMP/OR.ORF" => sendRootResponse(t, fileThumbnailResponse)
      case uri => sendRootResponse(t, s"Unexpected url: $uri")
    }
  }

  private def sendRootResponse(t: HttpExchange, response: String) {
    logger.debug(s"Response: $response")
    t.sendResponseHeaders(HttpOk, response.length())
    val os = t.getResponseBody
    os.write(response.getBytes)
    os.close()
  }

}
