package org.mauritania.photosync.starter

import org.mauritania.photosync.olympus.PhotosyncConfig

object ArgumentsParserBuilder {

  def buildParser() = new scopt.OptionParser[PhotosyncConfig]("photosync") {

    head("photosync", "0.2")

    note("If you find any bugs, send me an e-mail to mauriciojostx@gmail.com .\n")

    help("help") text ("Please also read README.md file.")

    opt[String]('n', "server-name").valueName("<server-name>").
      action { (propx, c) => c.copy(client = c.client.copy(serverName = propx)) }.
      text ("hostname or IP address of the camera server, default is 'oishare'")

    opt[String]('y', "server-protocol").valueName("<server-protocol>").
      action { (propx, c) => c.copy(client = c.client.copy(serverProtocol = propx)) }.
      text ("protocol used to connect to the camera server, default is 'http'")

    opt[String]('p', "server-port").valueName("<server-port>").
      action { (propx, c) => c.copy(client = c.client.copy(serverPort = propx.toInt)) }.
      text ("port number of camera server, default is '80'")

    opt[String]('b', "server-base-url").valueName("<server-base-url>").
      action { (propx, c) => c.copy(client = c.client.copy(serverBaseUrl = propx)) }.
      text ("base url under which the camera server exposes media, default is '/DCIM'")

    opt[String]('d', "server-folder-name").valueName("<server-folder-name>").
      action { (propx, c) => c.copy(client = c.client.copy(serverFolderName = propx)) }.
      text ("directory under which the camera server exposes media, default is '100OLYMP'")

    opt[String]('t', "server-ping-timeout").valueName("<server-ping-timeout>").
      action { (propx, c) => c.copy(client = c.client.copy(serverPingTimeout = propx.toInt)) }.
      text ("timeout to be used when trying to connect to the camera server, default is '2000'")

    opt[String]('r', "file-regex").valueName("<file-regex>").
      action { (propx, c) => c.copy(client = c.client.copy(fileRegex = propx.r)) }.
      text ("regular expression used to detect files from camera server response")

    opt[String]('o', "output-directory").valueName("<path>").
      action { (propx, c) => c.copy(outputDirectory = propx) }.
      text ("local directory where media will be stored, default is 'output'")

  }


}
