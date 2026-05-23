package org.mauritania.photosync.starter

import com.sun.net.httpserver.{HttpExchange, HttpHandler}
import org.slf4j.LoggerFactory

import scala.reflect.io.Streamable

/**
  * Simulates a newer Olympus/OM System camera (e.g. TG-7) that only supports
  * the CGI endpoint and returns 404 for direct /DCIM access.
  */
class HttpCameraCgiMock extends HttpHandler {

  val logger = LoggerFactory.getLogger(this.getClass)

  val CgiRootResponse =
    """VER_100
      |/DCIM,100OMSYS,0,16,23730,17529""".stripMargin

  val CgiFolderResponse =
    """VER_100
      |/DCIM/100OMSYS,P5180001.JPG,2643976,0,23730,17529
      |/DCIM/100OMSYS,P5230002.JPG,2562502,0,23735,30721""".stripMargin

  val FileContentResponse = "=== PHOTO SAMPLE ==="
  val fileThumbnailResponse = Streamable.bytes(this.getClass.getResource("/org/mauritania/photosync/100OLYMP/thumbnails/OR.ORF").openStream())
  val HttpOk = 200
  val HttpNotFound = 404

  def handle(t: HttpExchange) = {
    val uri = t.getRequestURI.toString
    logger.debug(s"Requested: $uri")
    uri match {
      case "/DCIM" => sendResponse(t, HttpNotFound, "Not Found".getBytes, true)
      case "/get_imglist.cgi?DIR=/DCIM" => sendResponse(t, HttpOk, CgiRootResponse.getBytes, true)
      case "/get_imglist.cgi?DIR=/DCIM/100OMSYS" => sendResponse(t, HttpOk, CgiFolderResponse.getBytes, true)
      case "/DCIM/100OMSYS/P5180001.JPG" => sendResponse(t, HttpOk, FileContentResponse.getBytes, true)
      case "/DCIM/100OMSYS/P5230002.JPG" => sendResponse(t, HttpOk, FileContentResponse.getBytes, true)
      case "/get_thumbnail.cgi?DIR=/DCIM/100OMSYS/P5180001.JPG" => sendResponse(t, HttpOk, fileThumbnailResponse, false)
      case "/get_thumbnail.cgi?DIR=/DCIM/100OMSYS/P5230002.JPG" => sendResponse(t, HttpOk, fileThumbnailResponse, false)
      case uri => sendResponse(t, HttpNotFound, s"Not Found: $uri".getBytes, true)
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
