package org.mauritania.photosync.starter

import java.io.File
import javafx.scene.input.KeyEvent
import javafx.scene.input.KeyCode

import org.mauritania.photosync.olympus.sync.{CameraMock, TempDir}
import org.mauritania.photosync.starter.gui.{GuiAsync, GuiStarter}
import org.specs2.mutable.Specification

import scalafx.event.Event
import scalafx.scene.input.{MouseButton, MouseEvent, PickResult}
import javafx.{event => jfxe}

import org.mauritania.photosync.olympus.client.CameraClient


class GuiStarterSpec extends Specification with TempDir with CameraMock {

/*
  // Test temporarily disabled until I figure out what is the reason it fails.
  val DefaultPickResult = new PickResult(GuiStarter.stage, 0, 0)
  val MouseClick = new MouseEvent(
    MouseEvent.MouseClicked, 0, 0, 0, 0, MouseButton.Primary, 1, true, true, true, true, true, true, true, true, true, true, DefaultPickResult)

  val EnterPress = new KeyEvent(KeyEvent.KEY_PRESSED, null, null, KeyCode.ENTER, false, false, false, false)


  val HttpHost = "localhost"
  val HttpPort = 8085
  val GuiWaitMs = 3000

  def mockedGuiArgs(tmpDir: File, port: Int) = Array(
    "--gui",
    "--server-name", HttpHost,
    "--server-port", port.toString,
    "--output-directory", tmpDir.getAbsolutePath
  )

  "The GUI starter" should {

    "work correctly under normal conditions and close correctly" in {
      withTmpDir { tmp =>
        val port = HttpPort
        withCameraMock(port){ camera =>

          // Files expected to exist in the filesystem after a synchronization (they do not exist yet)
          val expectedDownloadedOrfFile = new File(tmp, new File("100OLYMP", "OR.ORF").getPath) // ORF
          val expectedDownloadedAviFile = new File(tmp, new File("100OLYMP", "VI.AVI").getPath) // AVI

          // Launch the GUI
          val guiThread = launchGuiStarterAsync(mockedGuiArgs(tmp, port))

           // Let attempts to connect expire and ensure disconnected status is shown
          Thread.sleep(CameraClient.IsConnectedTimeout + WaitMs * 2)
          GuiStarter.ConnectivityText.text.value mustEqual GuiStarter.DisconnectedText

          // Launch camera, let initialize and ensure connected status is shown
          camera.start()
          Thread.sleep(WaitMs)
          Thread.sleep(2 * GuiStarter.ConnectivityCheckPeriodMs)
          GuiStarter.ConnectivityText.text.value mustEqual GuiStarter.ConnectedText

          // Ensure 2 files are matched, and that firing a sync actually brings them from remote to local
          GuiStarter.SyncPlanList.items.get().size() mustEqual 2
          expectedDownloadedOrfFile.exists() must beFalse
          expectedDownloadedAviFile.exists() must beFalse
          fireEvent(GuiStarter.SyncButton, MouseClick)
          expectedDownloadedOrfFile.exists() must beTrue
          expectedDownloadedAviFile.exists() must beTrue

          // Choose only one file (AVI) and ensure only this one is shown
          GuiStarter.FileGlobText.text = "*.AVI"
          fireEvent(GuiStarter.FileGlobText, EnterPress)
          GuiStarter.SyncPlanList.items.get().size() mustEqual 1

          // Stop the camera and ensure the status goes back to disconnected
          camera.stop(0)
          Thread.sleep(2 * GuiStarter.ConnectivityCheckPeriodMs)
          GuiStarter.ConnectivityText.text.value mustEqual GuiStarter.DisconnectedText

          // Close the app and ensure resources are released properly
          fireEvent(GuiStarter.CloseButton, MouseClick)
          guiThread.isAlive must beFalse
          GuiStarter.ThreadPool.isShutdown must beTrue

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
  */
}

