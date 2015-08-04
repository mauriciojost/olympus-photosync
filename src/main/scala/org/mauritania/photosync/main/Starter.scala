package org.mauritania.photosync.main

import java.io.File

import com.typesafe.scalalogging.Logger
import org.mauritania.photosync.olympus.api.Api
import org.slf4j.LoggerFactory

case class Config(foo: Int = -1, out: File = new File("."), xyz: Boolean = false,
                  libName: String = "", maxCount: Int = -1, verbose: Boolean = false, debug: Boolean = false,
                  mode: String = "", files: Seq[File] = Seq(), keepalive: Boolean = false,
                  jars: Seq[File] = Seq(), kwargs: Map[String,String] = Map())

object Starter {

  val logger = Logger(LoggerFactory.getLogger(this.getClass))

  // TODO add parameters
  // TODO add README.md
  // TODO add tasks to generate binaries
  // TODO try to use mockups? (to learn)

  def main(args: Array[String]): Unit = {

    val parser = new scopt.OptionParser[Config]("scopt") {
      head("scopt", "3.x")
      opt[Int]('f', "foo") action { (x, c) =>
        c.copy(foo = x) } text("foo is an integer property")
      opt[File]('o', "out") required() valueName("<file>") action { (x, c) =>
        c.copy(out = x) } text("out is a required file property")
      opt[(String, Int)]("max") action { case ((k, v), c) =>
        c.copy(libName = k, maxCount = v) } validate { x =>
        if (x._2 > 0) success else failure("Value <max> must be >0")
      } keyValueName("<libname>", "<max>") text("maximum count for <libname>")
      opt[Seq[File]]('j', "jars") valueName("<jar1>,<jar2>...") action { (x,c) =>
        c.copy(jars = x) } text("jars to include")
      opt[Map[String,String]]("kwargs") valueName("k1=v1,k2=v2...") action { (x, c) =>
        c.copy(kwargs = x) } text("other arguments")
      opt[Unit]("verbose") action { (_, c) =>
        c.copy(verbose = true) } text("verbose is a flag")
      opt[Unit]("debug") hidden() action { (_, c) =>
        c.copy(debug = true) } text("this option is hidden in the usage text")
      note("some notes.\n")
      help("help") text("prints this usage text")
      arg[File]("<file>...") unbounded() optional() action { (x, c) =>
        c.copy(files = c.files :+ x) } text("optional unbounded args")
      cmd("update") action { (_, c) =>
        c.copy(mode = "update") } text("update is a command.") children(
        opt[Unit]("not-keepalive") abbr("nk") action { (_, c) =>
          c.copy(keepalive = false) } text("disable keepalive"),
        opt[Boolean]("xyz") action { (x, c) =>
          c.copy(xyz = x) } text("xyz is a boolean property"),
        checkConfig { c =>
          if (c.keepalive && c.xyz) failure("xyz cannot keep alive") else success }
        )
    }
    // parser.parse returns Option[C]
    parser.parse(args, Config()) match {
      case Some(config) =>
      // do stuff

      case None =>
      // arguments are bad, error message will have been displayed
    }


    logger.info("Starting...")

    val cameraApi = new Api()

    if (!cameraApi.isReachable()) {
      logger.error("Cannot find camera API: are you connected to its WIFI service?")
      System.exit(-1)
    }

    val files = cameraApi.listFileIdsAndSize()

    files.foreach {
      fileIdAndSize => {

        val downloaded = cameraApi.fileHasBeenAlreadyDownloaded(fileIdAndSize._1)
        logger.debug(s"File $fileIdAndSize._1 with size $fileIdAndSize._2 downloaded $downloaded")
        if (!downloaded) {
          cameraApi.downloadFile(fileIdAndSize._1, fileIdAndSize._1)
        }
      }
    }

    logger.debug("Done.")

  }

}
