package org.mauritania.photosync.starter

import org.specs2.mutable.Specification

class StarterSpec extends Specification {

  "The starter" should {

    "throw exception in case of bad arguments" in {
      Starter.main(Array("--bad-argument")) must throwA[IllegalArgumentException]
    }

  }

}
