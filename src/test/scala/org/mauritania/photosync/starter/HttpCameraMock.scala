package org.mauritania.photosync.starter

import com.sun.net.httpserver.{HttpExchange, HttpHandler}

class HttpCameraMock extends HttpHandler {

  val rootResponse = """
...
</style>
<script type="text/javascript">
wlansd = new Array();
wlansd[0]="/DCIM/,100OLYMP,0,0,0,0";
...
"""
  val folderResponse = """
...
</style>
<script type="text/javascript">
wlansd = new Array();
wlansd[0]="/DCIM/100OLYMP/,OR.ORF,15441739,0,18229,43541";
wlansd[0]="/DCIM/100OLYMP/,VI.AVI,95441739,0,18229,43541";
...
                     """
  val fileContentResponse = "=== PHOTO SAMPLE ==="

  def handle(t: HttpExchange) {
    t.getRequestURI.toString match {
      case "/DCIM" => sendRootResponse(t, rootResponse)
      case "/DCIM/100OLYMP" => sendRootResponse(t, folderResponse)
      case "/DCIM/100OLYMP/OR.ORF" => sendRootResponse(t, fileContentResponse)
      case uri => sendRootResponse(t, s"Unexpected url: $uri")
    }
  }

  private def sendRootResponse(t: HttpExchange, response: String) {
    t.sendResponseHeaders(200, response.length())
    val os = t.getResponseBody
    os.write(response.getBytes)
    os.close()
  }

}
