package org.mauritania.photosync.starter.gui

import java.io.File
import java.time.LocalDate

import org.mauritania.photosync.Constants
import org.mauritania.photosync.olympus.client.CameraClient
import org.mauritania.photosync.olympus.sync.{FilesManagerImpl, FilesManagerMock, SyncPlanItem}
import org.mauritania.photosync.olympus.{FilesManager, PhotosyncConfig}
import org.mauritania.photosync.starter.ArgumentsParserBuilder
import org.mauritania.photosync.starter.gui.CustomCell.CellType
import org.slf4j.LoggerFactory
import rx._

import scala.collection.immutable.Seq
import scalafx.Includes._
import scalafx.application.JFXApp.PrimaryStage
import scalafx.application.{JFXApp, Platform}
import scalafx.collections.ObservableBuffer
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control._
import scalafx.scene.input.{KeyCode, KeyEvent}
import scalafx.scene.layout.{HBox, VBox}
import scalafx.scene.text.Text


object GuiStarter extends JFXApp {

  private val logger = LoggerFactory.getLogger(this.getClass)

  val NoneText = ""
  val SeqSeparator = ','
  val SloganText = "Synchronize your photos!!!"
  val StatusTextIdle = "Idle"
  val TitleStyle = "-fx-font: normal bold 15pt sans-serif"
  val StatusStyle = "-fx-font: normal italic 10pt sans-serif"

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

  val FileGlobText = new TextField {
    text = NoneText
    style = StatusStyle
    promptText = "Glob: *.*"
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
    onKeyPressed = (event: KeyEvent) => {
      if (event.code == KeyCode.Enter) {
        updateControls
      }
    }
  }

  val Separator = new Separator {
    maxWidth = 200
  }

  val TitleText = new Text {
    text = SloganText
    style = TitleStyle
  }

  val StatusText = new Text {
    text = StatusTextIdle
    style = StatusStyle
  }

  val SyncPlanList = new ListView[CellType] {}

  val RefreshButton = new Button("Refresh") {
    onMouseClicked = handle {
      updateControls
    }
  }

  val SyncButton = new Button("Sync") {
    onMouseClicked = handle {
      managerRx.now.map(syncSyncableFiles)
    }
  }

  val CloseButton = new Button("Quit") {
    onMouseClicked = handle {
      stage.close()
    }
  }

  delayedInit {
    val args = parameters.raw.toArray
    val fileConfiguration = ArgumentsParserBuilder.loadConfigFile
    val parsedArgs = ArgumentsParserBuilder.Parser.parse(args, fileConfiguration)
    val parsedConfig = parsedArgs match {
      case conf @ Some(c) => conf
      case invalid => throw new IllegalArgumentException(s"Bad command line arguments: $invalid")
    }
    SyncPlanList.setCellFactory(CustomCell.CustomCellCallback)
    baseConfigVar() = parsedConfig
  }

  def updateControls(): Unit = {
    untilDateVar.update(UntilDateText.text())
    fromDateVar.update(FromDateText.text())
    fileGlobVar.update(FileGlobText.text())
  }

  private def filesManager(config: PhotosyncConfig): FilesManager = {
    val cameraClient = new CameraClient(config.client)
    val managerConfig = FilesManagerImpl.Config(
      outputDir = new File(config.outputDirectory),
      mediaFilter = config.mediaFilter
    )
    new FilesManagerImpl(cameraClient, managerConfig)
    //new FilesManagerMock(managerConfig)
  }

  override def stopApp(): Unit = System.exit(0)

  stage = new PrimaryStage {
    title = "Olympus Photosync v" + Constants.Version
    scene = new Scene {
      resizable = false
      content = new VBox {
        alignment = Pos.Center
        padding = Insets(30, 200, 30, 200)
        spacing = 30
        children = Seq(
          TitleText,
          new VBox {
            alignment = Pos.Center
            spacing = 10
            children = Seq(
              new HBox {
                alignment = Pos.Center
                fillHeight = true
                spacing = 20
                children = Seq(RefreshButton, SyncButton, CloseButton)
              },
              FileGlobText,
              FromDateText,
              UntilDateText,
              SyncPlanList,
              Separator,
              StatusText
            )
          }
        )
      }
    }
  }

  def refreshSyncPlan(manager: FilesManager) = {
    def onAsyncThread(): Seq[SyncPlanItem] = manager.syncPlan()

    def onFxSyncThread(files: Seq[SyncPlanItem]) = {
      SyncPlanList.items = ObservableBuffer[CellType](files)
      StatusText.text = StatusTextIdle
    }

    GuiAsync.asyncThenSync(onAsyncThread, onFxSyncThread)
  }

  def syncSyncableFiles(manager: FilesManager) = {
    def onAsyncThread(): String = {
      val syncPlan = manager.syncPlan()
      logger.debug(s"Synchronizing ${syncPlan.map(_.fileInfo.name)}")
      syncPlan.foreach { syncPlanItem =>
        syncFile(manager, syncPlanItem)
      }
      StatusTextIdle
    }

    def onFxSyncThread(msg: String) = {
      StatusText.text = msg
    }

    GuiAsync.asyncThenSync(onAsyncThread, onFxSyncThread)
  }

  def syncFile(manager: FilesManager, syncPlanItem: SyncPlanItem) = {
    updateStatus(s"Synchronizing ${syncPlanItem.fileInfo.name} (${syncPlanItem.index})")
    manager.syncFile(syncPlanItem)
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

