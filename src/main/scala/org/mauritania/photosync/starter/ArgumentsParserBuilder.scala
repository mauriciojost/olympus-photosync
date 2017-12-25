package org.mauritania.photosync.starter

import java.time.LocalDate

import com.typesafe.config.ConfigFactory
import org.mauritania.photosync.Constants
import org.mauritania.photosync.olympus.PhotosyncConfig
import org.mauritania.photosync.olympus.client.CameraClientConfig
import org.mauritania.photosync.olympus.sync.FileInfoFilter

import scala.util.Try

object ArgumentsParserBuilder {

  val SeqSeparator = ','

  def loadConfigFile: PhotosyncConfig = {
    val configFile = ConfigFactory.load()
    PhotosyncConfig(
      client = CameraClientConfig(
        serverProtocol = configFile.getString("server.protocol"),
        serverName = configFile.getString("server.name"),
        serverPort = configFile.getInt("server.port"),
        serverBaseUrl = configFile.getString("server.base.url"),
        serverPingTimeout = configFile.getInt("server.ping.timeout"),
        fileRegex = configFile.getString("file.regex")
      ),
      mediaFilter = FileInfoFilter.Criteria(
        fromDateCondition = Try(configFile.getString("output.fromdate")).toOption.map(LocalDate.parse(_)),
        untilDateCondition = Try(configFile.getString("output.untildate")).toOption.map(LocalDate.parse(_)),
        fileNameConditions = Try(configFile.getString("output.patterns")).toOption.map(_.split(SeqSeparator))
      ),
      outputDirectory = configFile.getString("output.directory")
    )
  }

  def buildParser() = new scopt.OptionParser[PhotosyncConfig]("photosync") {

    head("photosync", Constants.Version)

    note(
      """
        |The official website of the project can be found here:
        |   https://github.com/mauriciojost/olympus-photosync
        |
        |The documentation for the current version can be found here:
        |   https://github.com/mauriciojost/olympus-photosync/tree/vVERSION
        |
        |Feel free to contribute!
        |""".stripMargin.replace("VERSION", Constants.Version))

    help("help") text "show this message"

    opt[String]('n', "server-name").valueName("<server-name>").
      action { (propx, c) => c.copy(client = c.client.copy(serverName = propx)) }.
      text("hostname or IP address of the camera server, default is '192.168.0.10'")

    opt[String]('y', "server-protocol").valueName("<server-protocol>").
      action { (propx, c) => c.copy(client = c.client.copy(serverProtocol = propx)) }.
      text("protocol used to connect to the camera server, default is 'http'")

    opt[String]('p', "server-port").valueName("<server-port>").
      action { (propx, c) => c.copy(client = c.client.copy(serverPort = propx.toInt)) }.
      text("port number of camera server, default is '80'")

    opt[String]('b', "server-base-url").valueName("<server-base-url>").
      action { (propx, c) => c.copy(client = c.client.copy(serverBaseUrl = propx)) }.
      text("base url under which the camera server exposes media, default is '/DCIM'")

    opt[String]('t', "server-ping-timeout").valueName("<server-ping-timeout>").
      action { (propx, c) => c.copy(client = c.client.copy(serverPingTimeout = propx.toInt)) }.
      text("timeout to be used when trying to connect to the camera server, default is '2000'")

    opt[String]('r', "file-regex").valueName("<file-regex>").
      action { (propx, c) => c.copy(client = c.client.copy(fileRegex = propx)) }.
      text("internal, regular expression used to detect files from camera server response")

    opt[String]('o', "output-directory").valueName("<path>").
      action { (propx, c) => c.copy(outputDirectory = propx) }.
      text("local directory where media will be stored, default is 'output'")

    opt[String]('B', "until").valueName("<DD-MM-YYYY>").
      action { (propx, c) => c.copy(mediaFilter = c.mediaFilter.copy(untilDateCondition = Some(LocalDate.parse(propx)))) }.
      text("synchronize only files created strictly before or on a given date")

    opt[String]('A', "from").valueName("<DD-MM-YYYY>").
      action { (propx, c) => c.copy(mediaFilter = c.mediaFilter.copy(fromDateCondition = Some(LocalDate.parse(propx)))) }.
      text("synchronize only files created after or on a given date")

    opt[String]('P', "file-patterns").valueName(s"<pattern1>$SeqSeparator<pattern2>$SeqSeparator...").
      action { (propx, c) => c.copy(mediaFilter = c.mediaFilter.copy(fileNameConditions = Some(propx.split(SeqSeparator)))) }.
      text("synchronize only files that match one of the provided glob patterns, for instance *.avi for AVI files (matching is case sensitive, so '*.AVI' is not equivalent to '*.avi')")

  }

}
