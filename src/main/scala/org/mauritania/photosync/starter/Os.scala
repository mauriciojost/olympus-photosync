package org.mauritania.photosync.starter

import org.slf4j.LoggerFactory

object Os {

  val logger = LoggerFactory.getLogger(this.getClass)

  val DefaultJavaOsPropertyKey = "os.name"

  sealed trait OsType

  case object Windows extends OsType
  case object Linux extends OsType
  case object MacOs extends OsType
  case object Unknown extends OsType

  def defaultOsPropertyValue(): String = System.getProperty(DefaultJavaOsPropertyKey)

  /**
    * Define the current Operating System
    */
  def currentOs(osPropertyValue: String): OsType = {
    val operSysProp = osPropertyValue.toLowerCase
    val detectedOs = if (operSysProp.contains("win")) {
      Windows
    } else if (operSysProp.contains("nix") || operSysProp.contains("nux") || operSysProp.contains("aix")) {
      Linux
    } else if (operSysProp.contains("mac")) {
      MacOs
    } else {
      Unknown
    }
    logger.debug(s"Detected OS: $detectedOs (property value '$operSysProp')")
    detectedOs
  }

}

