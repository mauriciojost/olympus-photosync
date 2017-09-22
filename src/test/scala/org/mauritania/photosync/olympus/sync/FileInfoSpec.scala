package org.mauritania.photosync.olympus.sync

import java.time.LocalDate

import org.specs2.mutable.Specification


class FileInfoSpec extends Specification {

  val DefaultFileInfo = FileInfo(
    folder = "FOLDER",
    name = "NAME",
    size = 0L
  )

  "The files info" should {

    "correctly retrieve the date from a machine date" in {
      DefaultFileInfo.copy(date = FileInfo.MinMachineDayticks).getHumanDate() mustEqual FileInfo.MinDate
      DefaultFileInfo.copy(date = FileInfo.MaxMachineDayticks).getHumanDate() mustEqual FileInfo.MaxDate

      DefaultFileInfo.copy(date = 36157).getHumanDate() mustEqual LocalDate.of(2050, 9, 29)
      DefaultFileInfo.copy(date = 36156).getHumanDate() mustEqual LocalDate.of(2050, 9, 28)
      DefaultFileInfo.copy(date = 23073).getHumanDate() mustEqual LocalDate.of(2025, 1, 1)

      DefaultFileInfo.copy(date = 10273).getHumanDate() mustEqual LocalDate.of(2000, 1, 1)
      DefaultFileInfo.copy(date = 10305).getHumanDate() mustEqual LocalDate.of(2000, 2, 1)
      DefaultFileInfo.copy(date = 10337).getHumanDate() mustEqual LocalDate.of(2000, 3, 1)
      DefaultFileInfo.copy(date = 10849).getHumanDate() mustEqual LocalDate.of(2001, 3, 1)
      DefaultFileInfo.copy(date = 11361).getHumanDate() mustEqual LocalDate.of(2002, 3, 1)
    }

  }
}
