package org.mauritania.photosync.olympus.client

import java.time.{LocalDate, LocalTime}

import org.specs2.mutable.Specification


class FileInfoSpec extends Specification {

  val DefaultFileInfo = FileInfo(
    folder = "FOLDER",
    name = "NAME",
    size = 0L
  )

  "The files info" should {

    "correctly retrieve the date from a machine date" in {
      DefaultFileInfo.copy(date = FileInfo.MinMachineDayticks).humanDate mustEqual FileInfo.MinDate
      DefaultFileInfo.copy(date = FileInfo.MaxMachineDayticks).humanDate mustEqual FileInfo.MaxDate

      DefaultFileInfo.copy(date = 36157).humanDate mustEqual LocalDate.of(2050, 9, 29)
      DefaultFileInfo.copy(date = 36156).humanDate mustEqual LocalDate.of(2050, 9, 28)
      DefaultFileInfo.copy(date = 23073).humanDate mustEqual LocalDate.of(2025, 1, 1)

      DefaultFileInfo.copy(date = 10273).humanDate mustEqual LocalDate.of(2000, 1, 1)
      DefaultFileInfo.copy(date = 10305).humanDate mustEqual LocalDate.of(2000, 2, 1)
      DefaultFileInfo.copy(date = 10337).humanDate mustEqual LocalDate.of(2000, 3, 1)
      DefaultFileInfo.copy(date = 10849).humanDate mustEqual LocalDate.of(2001, 3, 1)
      DefaultFileInfo.copy(date = 11361).humanDate mustEqual LocalDate.of(2002, 3, 1)
    }

    "correctly retrieve the time from a machine time" in {
      DefaultFileInfo.copy(time = 0).humanTime mustEqual LocalTime.of(0, 0, 0)
      DefaultFileInfo.copy(time = 60 * 60 * 24 - 1).humanTime mustEqual LocalTime.of(23, 59, 59)
    }

  }
}
