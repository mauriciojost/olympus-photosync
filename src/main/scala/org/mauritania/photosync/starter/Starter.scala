package org.mauritania.photosync.starter

import java.io.{File, PrintWriter}

import org.mauritania.photosync.Constants
import org.mauritania.photosync.olympus.client.CameraClient
import org.mauritania.photosync.olympus.sync.FilesManager
import org.mauritania.photosync.starter.Os.Windows
import org.mauritania.photosync.starter.gui.GuiStarter
import org.slf4j.LoggerFactory

import scala.util.{Failure, Success, Try}

object Starter {

  val ConfTemplateResource = "/application.conf"
  val InitFileOutput = "application.conf"

  val logger = LoggerFactory.getLogger(this.getClass)

  def main(args: Array[String]): Unit = {
    try {
      startApp(args)
    } catch {
      case e: Exception =>
        logger.error("Application failed", e)
        throw e
    }
  }

  def startApp(args: Array[String]): Unit = {

    logger.info(s"Version: ${Constants.Version}")

    val fileConfiguration = ArgumentsParserBuilder.loadConfigFile

    logger.info(s"Loading file configuration: $fileConfiguration")

    val osIswindows = (Os.currentOs(Os.JavaOsProperty) == Windows)

    ArgumentsParserBuilder.Parser.parse(args, fileConfiguration) match {
      case Some(config) if config.initConfig => initConfig(args)
      case Some(config) if config.guiMode && !config.commandLineMode => GuiStarter.main(args)
      case Some(config) if osIswindows && !config.commandLineMode => GuiStarter.main(args)
      case Some(config) => Starter.startSynchronization(config)
      case None =>  throw new IllegalArgumentException("Bad command line arguments!")
    }

  }

  private def initConfig(args: Array[String]): Unit = {
    val targetFile = new File(InitFileOutput)
    val lines = readResource(ConfTemplateResource)
    val newArg = s"-Dconfig.file=${targetFile.getAbsolutePath}"
    lines match {
      case Success(l) => {
        writeToFile(targetFile, l)
        logger.info(s"File created at: ${targetFile.getAbsolutePath}")
        logger.info(s"Use it as follows:")
        logger.info(s"  - Under Linux/MacOs execute:")
        logger.info(s"      - photosync $newArg")
        logger.info(s"  - Under Windows do as follows:")
        logger.info(s"      - Open bin\\photosync.bat with a text editor")
        logger.info(s"      - Replace the line:")
        logger.info(s"          set _JAVA_PARAMS=")
        logger.info(s"        with:")
        logger.info(s"          set _JAVA_PARAMS=$newArg")
        logger.info(s"      - Save the file")
        logger.info(s"      - Launch it")
      }
      case Failure(f) => logger.error("Unexpected error", f)

    }
  }

  private def writeToFile(f: File, lines: List[String]): Unit = {
    val NewLine = System.getProperty("line.separator")
    val linesWithNewLine = lines.map(_ + NewLine)
    new PrintWriter(f) {
      linesWithNewLine.foreach(write)
      close()
    }
  }

  private def readResource(r: String): Try[List[String]] = {
    val confTemplateStream = getClass.getResourceAsStream(r)
    val templateConfigLines = Try(scala.io.Source.fromInputStream(confTemplateStream).getLines.toList)
    confTemplateStream.close()
    templateConfigLines
  }

  def startSynchronization(config: PhotosyncConfig): Unit = {

    logger.info(s"Using configuration ($config)...")
    val cameraClient = new CameraClient(config.client)
    val managerConfig = FilesManager.Config(
      outputDir = new File(config.outputDirectory),
      mediaFilter = config.mediaFilter
    )
    val manager = new FilesManager(cameraClient, managerConfig)

    logger.info("Synchronizing media from camera -> PC...")
    manager.sync()

    logger.info("Synchronized!")

    if (config.shutDownAfterSync) {
      cameraClient.shutDown()
    }

  }

}
