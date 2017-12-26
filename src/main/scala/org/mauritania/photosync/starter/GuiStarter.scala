package org.mauritania.photosync.starter

import java.io.File

import org.mauritania.photosync.Constants
import org.mauritania.photosync.olympus.client.CameraClient
import org.mauritania.photosync.olympus.sync.FilesManager
import org.mauritania.photosync.olympus.sync.FilesManager.SyncPlanItem

import scalafx.Includes._
import scalafx.application.{JFXApp, Platform}
import scalafx.application.JFXApp.PrimaryStage
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Separator}
import scalafx.scene.layout.{HBox, VBox}
import scalafx.scene.text.Text

object GuiStarter extends JFXApp {

  var manager: FilesManager = null

  val StatusTextIdle = "Idle"
  val TitleStyle = "-fx-font: normal bold 15pt sans-serif"
  val StatusStyle = "-fx-font: normal italic 10pt sans-serif"

  val Separator = new Separator {
    maxWidth = 200
  }

  val StatusText = new Text {
    text = StatusTextIdle
    style = StatusStyle
  }

  val SyncButton = new Button("Sync") {
    onMouseClicked = handle { startTask }
  }

  val CloseButton = new Button("Close") {
    onMouseClicked = handle {
      StatusText.text = "Closing..."
      stage.close()
    }
  }

  delayedInit {
    val args = parameters.raw.toArray
    val fileConfiguration = ArgumentsParserBuilder.loadConfigFile
    ArgumentsParserBuilder.Parser.parse(args, fileConfiguration) match {
      case Some(config) if config.gui => {
        val cameraClient = new CameraClient(config.client)
        val managerConfig = FilesManager.Config(
          outputDir = new File(config.outputDirectory),
          mediaFilter = config.mediaFilter
        )
        this.manager = new FilesManager(cameraClient, managerConfig)
      }
      case None => throw new IllegalArgumentException("Bad command line arguments!")
    }
  }

  override def stopApp(): Unit = {
    System.exit(0)
  }

  stage = new PrimaryStage {
    title = "Olympus Photosync v" + Constants.Version

    scene = new Scene {
      resizable = false
      content = new VBox {
        alignment = Pos.Center
        padding = Insets(30, 200, 30, 200)
        spacing = 30
        children = Seq(
          new VBox {
            alignment = Pos.Center
            spacing = 10
            children = Seq(
              new HBox {
                alignment = Pos.Center
                spacing = 50
                children = Seq(SyncButton, CloseButton)
              },
              Separator,
              StatusText
            )
          }
        )
      }
    }

  }

  def startTask = {
    val backgroundThread = new Thread {
      setDaemon(true)
      override def run = {
        runTask
      }
    }
    backgroundThread.start()
  }

  def runTask = {
    StatusText.text = "Querying..."
    val plan = manager.syncPlan()
    plan.foreach { case SyncPlanItem(file, index, locals, remotes) =>
      StatusText.text = s"Syncing: ${file.name} (${index} / ${plan.size}) ..."
      manager.syncFile(file, locals, remotes)
    }
    StatusText.text = StatusTextIdle
  }

}

