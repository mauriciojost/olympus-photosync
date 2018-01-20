package org.mauritania.photosync.olympus.sync

import java.net.InetSocketAddress

import com.sun.net.httpserver.HttpServer
import org.mauritania.photosync.starter.HttpCameraMock

trait CameraMock {

  val WaitMs = 1000

  def withCameraMock[T](port: Int)(f: => T): T = {
    val cameraMockServer = httpCameraMock(port)
    cameraMockServer.start()
    Thread.sleep(WaitMs) // Let the GUI initialize
    try {
      f
    } finally {
      Thread.sleep(WaitMs)
      cameraMockServer.stop(0)
    }
  }

  private def httpCameraMock(port: Int): HttpServer = {
    val server = HttpServer.create(new InetSocketAddress(port), 0)
    server.createContext("/", new HttpCameraMock())
    server.setExecutor(null)
    server
  }


}
