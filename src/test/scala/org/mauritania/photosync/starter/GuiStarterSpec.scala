package org.mauritania.photosync.starter

import java.io.File
import java.net.InetSocketAddress

import com.sun.net.httpserver.HttpServer
import org.mauritania.photosync.olympus.sync.TempDir
import org.mauritania.photosync.starter.gui.GuiStarter
import org.specs2.mutable.Specification

import scalafx.event.Event
import scalafx.scene.input.{MouseButton, MouseEvent, PickResult}


class GuiStarterSpec extends Specification with TempDir {

  val DefaultPickResult = new PickResult(GuiStarter.stage, 0, 0)
  val MouseClick = new MouseEvent(
    MouseEvent.MouseClicked, 0, 0, 0, 0, MouseButton.Primary, 1, true, true, true, true, true, true, true, true, true, true, DefaultPickResult)
  val WaitMs = 2000

  "The GUI starter" should {

    "works correctly under normal conditions" in {
      withTmpDir { tmp =>
        val expectedDownloadedOrfFile = new File(tmp, new File("100OLYMP", "OR.ORF").getPath)
        val expectedDownloadedAviFile = new File(tmp, new File("100OLYMP", "VI.AVI").getPath)

        val server = HttpServer.create(new InetSocketAddress(8085), 0)
        server.createContext("/", new HttpCameraMock())
        server.setExecutor(null)
        server.start()

        val r = new Runnable() {
          override def run() = {
            Starter.main(
              Array(
                "--gui",
                "--server-name", "localhost",
                "--server-port", "8085",
                "--output-directory", tmp.getAbsolutePath
              )
            )

          }
        }
        new Thread(r).start()

        Thread.sleep(WaitMs) // Let thread initialize GUI

        Event.fireEvent(GuiStarter.SyncButton, MouseClick)

        Thread.sleep(WaitMs)

        server.stop(0)

        expectedDownloadedOrfFile.exists() must beTrue
        expectedDownloadedAviFile.exists() must beTrue
      }
    }

  }

}

