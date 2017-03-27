package org.mauritania.photosync.starter

import java.io.{InputStream, OutputStream}
import java.net.InetSocketAddress

import com.sun.net.httpserver.{HttpExchange, HttpHandler, HttpServer}
import org.specs2.mutable.Specification


class StarterSpec extends Specification {

  "The starter" should {

    "throw exception in case of bad arguments" in {
      Starter.main(Array("--bad-argument")) must throwA[IllegalArgumentException]
    }

    "works correctly under normal conditions" in {
      val server = HttpServer.create(new InetSocketAddress(8085), 0)
      server.createContext("/", new RootHandler())
      server.setExecutor(null)
      server.start()

      Starter.main(
        Array(
          "--server-name", "localhost",
          "--server-port", "8085",
          "--output-directory", "src/test/resources/org/mauritania/photosync/photosample/"
        )
      )

      server.stop(0)
      done
    }

  }

}

class RootHandler extends HttpHandler {

  val rootResponse = """
...
</style>
<script type="text/javascript">
wlansd = new Array();
wlansd[0]="/DCIM/100OLYMP/,OR.ORF,15441739,0,18229,43541";
...
"""
  val fileContentResponse = "=== PHOTO SAMPLE ==="

  def handle(t: HttpExchange) {
    t.getRequestURI.toString match {
      case "/DCIM/100OLYMP/" => sendRootResponse(t, rootResponse)
      case "/DCIM/100OLYMP/OR.ORF" => sendRootResponse(t, fileContentResponse)
    }
  }

  private def copyStream(in: InputStream, out: OutputStream) {
    Iterator
      .continually(in.read)
      .takeWhile(-1 !=)
      .foreach(out.write)
  }

  private def sendRootResponse(t: HttpExchange, response: String) {
    t.sendResponseHeaders(200, response.length())
    val os = t.getResponseBody
    os.write(response.getBytes)
    os.close()
  }

}
