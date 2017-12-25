package org.mauritania.photosync.starter

import java.io.File
import java.util.concurrent.Executors

import org.mauritania.photosync.Constants
import org.mauritania.photosync.olympus.sync.FilesManagerImpl.SyncPlanItem
import org.mauritania.photosync.olympus.sync.{FilesManagerImpl, FilesManagerMock}
import org.mauritania.photosync.olympus.{FilesManager, PhotosyncConfig}
import org.slf4j.LoggerFactory
import rx._

import scala.collection.immutable.Seq
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import scalafx.Includes._
import scalafx.application.JFXApp.PrimaryStage
import scalafx.application.{JFXApp, Platform}
import scalafx.collections.ObservableBuffer
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.{Button, ListView, Separator, TextField}
import scalafx.scene.input.{KeyCode, KeyEvent}
import scalafx.scene.layout.{HBox, VBox}
import scalafx.scene.text.Text


object GuiStarter extends JFXApp {

  private val logger = LoggerFactory.getLogger(this.getClass)

  val StatusTextIdle = "Idle"
  val GlobDefaultText = "*"
  val TitleStyle = "-fx-font: normal bold 15pt sans-serif"
  val StatusStyle = "-fx-font: normal italic 10pt sans-serif"

  val baseConfigVar = Var[Option[PhotosyncConfig]](None)
  val globVar = Var(GlobDefaultText)

  val managerRx = Rx {
    val baseConfigVal = baseConfigVar()
    val globVal = globVar()
    logger.debug(s"Manager recalculated: $baseConfigVal & $globVal")
    baseConfigVal.map(c => filesManager(resolvedConfig(c, globVal)))
  }

  val syncableFilesRx = Rx {
    val managerValue = managerRx()
    managerValue.map(refreshSyncableFiles(_))
  }

  val GlobText = new TextField {
    text = GlobDefaultText
    style = StatusStyle
    onKeyPressed = (event: KeyEvent) => {
      if (event.code == KeyCode.Enter) {
        StatusText.text = "Refreshing..."
        globVar.update(text())
      }
    }
  }

  val Separator = new Separator {
    maxWidth = 200
  }

  val StatusText = new Text {
    text = StatusTextIdle
    style = StatusStyle
  }

  val MediaList = new ListView[String] {}

  val RefreshButton = new Button("Refresh") {
    onMouseClicked = handle {
      StatusText.text = "Refreshing..."
      managerRx.now.map(refreshSyncableFiles)
    }
  }

  val SyncButton = new Button("Sync") {
    onMouseClicked = handle {
      StatusText.text = "Syncing..."
      managerRx.now.map(syncSyncableFiles)
    }
  }

  val CloseButton = new Button("Quit") {
    onMouseClicked = handle {
      StatusText.text = "Closing..."
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
    baseConfigVar() = parsedConfig
  }

  private def filesManager(config: PhotosyncConfig): FilesManager = {
    //val cameraClient = new CameraClient(config.client)
    val managerConfig = FilesManagerImpl.Config(
      outputDir = new File(config.outputDirectory),
      mediaFilter = config.mediaFilter
    )
    new FilesManagerMock(managerConfig)
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
          new VBox {
            alignment = Pos.Center
            spacing = 10
            children = Seq(
              new HBox {
                alignment = Pos.Center
                spacing = 50
                children = Seq(RefreshButton, SyncButton, CloseButton)
              },
              GlobText,
              MediaList,
              Separator,
              StatusText
            )
          }
        )
      }
    }
  }

  def refreshSyncableFiles(manager: FilesManager) = {
    def onAsyncThread(): Seq[String] = manager.syncPlan().map(_.fileInfo.name)

    def onFxSyncThread(files: Seq[String]) = {
      MediaList.items = ObservableBuffer[String](files)
      StatusText.text = StatusTextIdle
    }

    Async.asyncThenSync(onAsyncThread, onFxSyncThread)
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

    Async.asyncThenSync(onAsyncThread, onFxSyncThread)
  }

  def syncFile(manager: FilesManager, syncPlanItem: SyncPlanItem) = {
    Async.updateStatus(s"Synchronizing ${syncPlanItem.fileInfo.name} (${syncPlanItem.index})")
    manager.syncFile(syncPlanItem.fileInfo, syncPlanItem.local, syncPlanItem.remote)
  }


  object Async {

    private val AsyncExecutionContext = new ExecutionContext {
      val ThreadPool = Executors.newFixedThreadPool(4);

      def execute(runnable: Runnable) {
        ThreadPool.submit(runnable)
      }

      def reportFailure(t: Throwable): Unit = {
        logger.error("Error", t)
      }
    }


    private def runnable(func: => Unit): Runnable = new Runnable {
      override def run = func
    }

    /**
      * Execute sync(async()).
      *
      * @param async function returning [[T]] (will asynchronously execute)
      * @param sync  function using [[T]] (will execute synchronously in UI thread)
      * @tparam T type of exchange
      */
    def asyncThenSync[T](async: => T, sync: T => Unit) = {
      val fu = Future(async)(AsyncExecutionContext)
      fu.onComplete {
        case Success(r) => Platform.runLater(runnable(sync(r)))
        case Failure(e) => logger.error(e.getMessage)
      }(AsyncExecutionContext)
    }

    def updateStatus(msg: String) = Platform.runLater(runnable(StatusText.text = msg))

  }

  def resolvedConfig(baseConfig: PhotosyncConfig, glob: String) = {
    baseConfig.copy(
      mediaFilter = baseConfig.mediaFilter.copy(fileNameConditions = Some(Seq(glob)))
    )
  }


}

