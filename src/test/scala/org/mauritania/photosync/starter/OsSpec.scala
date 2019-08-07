package org.mauritania.photosync.starter

import org.specs2.mutable.Specification

class OsSpec extends Specification {

  "The OS utility" should {

    "parse server name" in {
      Os.defaultOsPropertyValue() mustEqual "Linux" // tests only executed under linux

      // as per https://memorynotfound.com/detect-os-name-version-java/
      Os.currentOs("linux") mustEqual Os.Linux
      Os.currentOs("unix") mustEqual Os.Linux
      Os.currentOs("windows") mustEqual Os.Windows
      Os.currentOs("mac os") mustEqual Os.MacOs
      Os.currentOs("another os") mustEqual Os.Unknown

    }

  }

}

