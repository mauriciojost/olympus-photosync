package org.mauritania.photosync.starter.gui

import java.io.File
import java.time.LocalDate
import java.util.concurrent.Executors

import org.mauritania.photosync.Constants
import org.mauritania.photosync.olympus.client.CameraClient
import org.mauritania.photosync.olympus.sync.{FilesManager, SyncPlanItem}
import org.mauritania.photosync.starter.{ArgumentsParserBuilder, PhotosyncConfig}
import org.mauritania.photosync.starter.gui.CustomCell.CellType
import org.slf4j.LoggerFactory
import rx._
import rx.async.Timer

import scala.collection.immutable.Seq
import scalafx.Includes._
import scalafx.application.JFXApp.PrimaryStage
import scalafx.application.{JFXApp, Platform}
import scalafx.collections.ObservableBuffer
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control._
import scalafx.scene.input.{KeyCode, KeyEvent}
import scalafx.scene.layout.{HBox, Priority, VBox}
import scalafx.scene.text.Text
import rx.async.Platform._

import scala.concurrent.duration._
import scala.util.Try


object GuiStarter extends JFXApp {

  private val logger = LoggerFactory.getLogger(this.getClass)

  val NoneText = ""
  val SeqSeparator = ','
  val StatusTextIdle = "Idle"
  val DisconnectedText = "Disconnected (!!!)"
  val ConnectedText = "Connected"
  val ConnectivityCheckPeriodMs = 1000
  val StatusSyncdFinished = "Sync finished"
  val StatusStyle = "-fx-font: normal italic 10pt sans-serif"
  val DefaultSpacing = 20
  val ThreadPoolSize = 4
  val ThreadPool = Executors.newFixedThreadPool(ThreadPoolSize);
  val GuiAsync = new GuiAsync(ThreadPool)

  val baseConfigVar = Var[Option[PhotosyncConfig]](None)
  val fileGlobVar = Var(NoneText)
  val fromDateVar = Var(NoneText)
  val untilDateVar = Var(NoneText)

  val managerRx = Rx {
    val newMan = baseConfigVar().map { config =>
      filesManager(
        resolvedConfig(
          config,
          fileGlobVar(),
          fromDateVar(),
          untilDateVar()
        )
      )
    }
    logger.debug(s"Manager recalculated: ${newMan}")
    newMan
  }

  val syncableFilesRx = Rx {
    val managerValue = managerRx()
    StatusText.text = "Refreshing..."
    managerValue.map(refreshSyncPlan(_))
  }

  Timer(ConnectivityCheckPeriodMs millis).trigger {
    Try {
      val managerValue = managerRx.now
      if (managerValue.filter(_.isRemoteConnected).isDefined) {
        ConnectivityText.text = ConnectedText
      } else {
        ConnectivityText.text = DisconnectedText
      }
    }
  }

  val FileGlobText = new TextField {
    text = NoneText
    style = StatusStyle
    promptText = "Glob: *.*"
    vgrow = Priority.Never
    onKeyPressed = (event: KeyEvent) => {
      if (event.code == KeyCode.Enter) {
        updateControls
      }
    }
  }

  val FromDateText = new TextField {
    text = NoneText
    style = StatusStyle
    promptText = "From: 2000-01-01"
    vgrow = Priority.Never
    onKeyPressed = (event: KeyEvent) => {
      if (event.code == KeyCode.Enter) {
        updateControls
      }
    }
  }

  val UntilDateText = new TextField {
    text = NoneText
    style = StatusStyle
    promptText = "Until: 2000-01-01"
    vgrow = Priority.Never
    onKeyPressed = (event: KeyEvent) => {
      if (event.code == KeyCode.Enter) {
        updateControls
      }
    }
  }

  val Separator = new Separator {
    vgrow = Priority.Never
  }

  val StatusText = new Text {
    text = StatusTextIdle
    style = StatusStyle
    alignmentInParent = Pos.BottomCenter
    vgrow = Priority.Never
  }

  val ConnectivityText = new Text {
    text = DisconnectedText
    style = StatusStyle
    alignmentInParent = Pos.BottomCenter
    vgrow = Priority.Never
  }

  val SyncPlanList = new ListView[CellType] {
    vgrow = Priority.Always
  }

  val RefreshButton = new Button("Refresh") {
    onMouseClicked = handle {
      Platform.runLater(runnable(updateControls))
    }
  }

  val SyncButton = new Button("Sync") {
    onMouseClicked = handle {
      managerRx.now.map(syncSyncableFiles)
    }
  }

  val CloseButton = new Button("Quit") {
    onMouseClicked = handle {
      Platform.runLater(runnable{
        stopApp()
      })
    }
  }

  delayedInit {
    val args = parameters.raw.toArray
    val fileConfiguration = ArgumentsParserBuilder.loadConfigFile
    val parsedArgs = ArgumentsParserBuilder.Parser.parse(args, fileConfiguration)
    parsedArgs match {
      case conf @ Some(c) => {
        SyncPlanList.setCellFactory(CustomCell.customCellFactory(c.guiConfig.thumbnailSize, c.guiConfig.showFilename))
        baseConfigVar() = conf
      }
      case invalid => throw new IllegalArgumentException(s"Bad command line arguments: $invalid")
    }
  }

  def updateControls(): Unit = {
    untilDateVar.update(UntilDateText.text())
    fromDateVar.update(FromDateText.text())
    fileGlobVar.update(FileGlobText.text())
  }

  private def filesManager(config: PhotosyncConfig): FilesManager = {
    val cameraClient = new CameraClient(config.client)
    val managerConfig = FilesManager.Config(
      outputDir = new File(config.outputDirectory),
      mediaFilter = config.mediaFilter
    )
    new FilesManager(cameraClient, managerConfig)
  }

  stage = new PrimaryStage {
    title = "Olympus Photosync v" + Constants.Version
    scene = new Scene {
      root = new VBox {
        alignment = Pos.Center
        spacing = DefaultSpacing
        padding = Insets(DefaultSpacing, DefaultSpacing, DefaultSpacing, DefaultSpacing)
        children = Seq(
          new VBox {
            alignment = Pos.Center
            spacing = DefaultSpacing
            vgrow = Priority.Always
            children = Seq(
              new HBox {
                alignment = Pos.Center
                spacing = DefaultSpacing
                children = Seq(RefreshButton, SyncButton, CloseButton)
                vgrow = Priority.Never
              },
              FileGlobText,
              FromDateText,
              UntilDateText,
              SyncPlanList,
              Separator,
              StatusText,
              ConnectivityText
            )
          }
        )
      }
    }
  }

  def refreshSyncPlan(manager: FilesManager) = {
    def syncPlan(): Seq[SyncPlanItem] = manager.syncPlan()

    def updateUi(files: Seq[SyncPlanItem]) = {
      SyncPlanList.items = ObservableBuffer[CellType](files)
      StatusText.text = StatusTextIdle
    }

    GuiAsync.asyncThenSync(syncPlan, updateUi)
  }

  def syncSyncableFiles(manager: FilesManager) = {
    def startSynchronization(): String = {
      val syncPlan = manager.syncPlan()
      logger.debug(s"Synchronizing ${syncPlan.map(_.fileInfo.name)}")
      syncPlan.foreach { syncPlanItem =>
        syncFile(manager, syncPlanItem)
      }
      val outputDirectory = baseConfigVar.now.map(_.outputDirectory).mkString
      StatusSyncdFinished + s"(output at: $outputDirectory)"
    }

    def updateUi(msg: String) = {
      StatusText.text = msg
    }

    GuiAsync.asyncThenSync(startSynchronization, updateUi)
  }

  def syncFile(manager: FilesManager, syncPlanItem: SyncPlanItem) = {
    updateStatus(s"Synchronizing ${syncPlanItem.fileInfo.name} (${syncPlanItem.index.percentageAsStr})")
    manager.syncFile(syncPlanItem)
  }

  override def stopApp(): Unit = {
    ThreadPool.shutdownNow()
    stage.close()
  }

  private def resolvedConfig(
    baseConfig: PhotosyncConfig,
    glob: String,
    from: String,
    until: String
  ) = {
    val oldMediaFilter = baseConfig.mediaFilter
    val newMediaFilter = oldMediaFilter.copy(
      fileNameConditions = parseSeq(glob),
      fromDateCondition = parseLocalDate(from),
      untilDateCondition = parseLocalDate(until)
    )
    baseConfig.copy(mediaFilter = newMediaFilter)
  }

  private def parseLocalDate(d: String) = Some(d).filterNot(_ == NoneText).map(LocalDate.parse)

  private def parseSeq(s: String) = Some(s).filterNot(_ == NoneText).map(_.split(SeqSeparator).toSeq)

  private def updateStatus(msg: String) = Platform.runLater(runnable(StatusText.text = msg))

  private def runnable(func: => Unit): Runnable = new Runnable {
    override def run = func
  }

}

