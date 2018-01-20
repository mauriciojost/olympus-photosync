package org.mauritania.photosync.starter

import java.io.File
import javafx.scene.input.KeyEvent
import javafx.scene.input.KeyCode

import org.mauritania.photosync.olympus.sync.{CameraMock, TempDir}
import org.mauritania.photosync.starter.gui.GuiStarter
import org.specs2.mutable.Specification

import scalafx.event.Event
import scalafx.scene.input.{MouseButton, MouseEvent, PickResult}
import javafx.{event => jfxe}


class GuiStarterSpec extends Specification with TempDir with CameraMock {

  val DefaultPickResult = new PickResult(GuiStarter.stage, 0, 0)
  val MouseClick = new MouseEvent(
    MouseEvent.MouseClicked, 0, 0, 0, 0, MouseButton.Primary, 1, true, true, true, true, true, true, true, true, true, true, DefaultPickResult)

  val EnterPress = new KeyEvent(KeyEvent.KEY_PRESSED, null, null, KeyCode.ENTER, false, false, false, false)


  val HttpHost = "localhost"
  val HttpPort = 8085

  val GuiWaitMs = 8085

  def mockedGuiArgs(tmpDir: File) = Array(
    "--gui",
    "--server-name", HttpHost,
    "--server-port", HttpPort.toString,
    "--output-directory", tmpDir.getAbsolutePath
  )

  "The GUI starter" should {

    "work correctly under normal conditions and close correctly" in {
      withTmpDir { tmp =>
        withCameraMock(HttpPort){
          val expectedDownloadedOrfFile = new File(tmp, new File("100OLYMP", "OR.ORF").getPath)
          val expectedDownloadedAviFile = new File(tmp, new File("100OLYMP", "VI.AVI").getPath)

          val guiThread = launchGuiStarterAsync(mockedGuiArgs(tmp))

          expectedDownloadedOrfFile.exists() must beFalse
          expectedDownloadedAviFile.exists() must beFalse

          fireEvent(GuiStarter.SyncButton, MouseClick)

          expectedDownloadedOrfFile.exists() must beTrue
          expectedDownloadedAviFile.exists() must beTrue

          fireEvent(GuiStarter.CloseButton, MouseClick)

          guiThread.isAlive mustEqual false

        }
      }
    }
    "filter correctly and close correctly" in {
      withTmpDir { tmp =>
        withCameraMock(HttpPort){

          val guiThread = launchGuiStarterAsync(mockedGuiArgs(tmp))

          GuiStarter.FileGlobText.text = "*.AVI" // only 1 file matches (out of the two existent)

          fireEvent(GuiStarter.FileGlobText, EnterPress)

          fireEvent(GuiStarter.CloseButton, MouseClick)

          guiThread.isAlive mustEqual false

        }
      }
    }

  }

  private def fireEvent(target: jfxe.EventTarget, event: jfxe.Event) = {
    Event.fireEvent(target, event)
    Thread.sleep(GuiWaitMs) // Let firing operations take place
  }

  private def launchGuiStarterAsync(args: Array[String]): Thread = {
    val runnable = new Runnable() {
      override def run() = {
        Starter.main(args)
      }
    }
    val thread = new Thread(runnable)
    thread.setDaemon(false)
    thread.start()
    Thread.sleep(GuiWaitMs) // Let thread initialize GUI
    thread
  }
}

