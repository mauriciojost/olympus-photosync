package org.mauritania.photosync.starter

import com.typesafe.config.ConfigFactory
import org.mauritania.photosync.Constants
import org.mauritania.photosync.olympus.PhotosyncConfig
import org.mauritania.photosync.olympus.client.CameraClientConfig

object ArgumentsParserBuilder {

  def loadConfigFile(): PhotosyncConfig = {
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

    help("help") text ("show this message")

    opt[String]('n', "server-name").valueName("<server-name>").
      action { (propx, c) => c.copy(client = c.client.copy(serverName = propx)) }.
      text("hostname or IP address of the camera server, default is 'oishare'")

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
      text("regular expression used to detect files from camera server response")

    opt[String]('o', "output-directory").valueName("<path>").
      action { (propx, c) => c.copy(outputDirectory = propx) }.
      text("local directory where media will be stored, default is 'output'")

  }

}
