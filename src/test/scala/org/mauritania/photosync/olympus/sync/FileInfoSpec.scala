package org.mauritania.photosync.olympus.sync

import java.time.LocalDate

import org.specs2.mutable.Specification


class FileInfoSpec extends Specification {

  val DefaultFileInfo = FileInfo(
    folder = "FOLDER",
    name = "NAME",
    size = 0L,
    date = None
  )

  "The files info" should {

    "correctly retrieve the date from a machine date" in {
      DefaultFileInfo.copy(date = Some(FileInfo.MinMachineDayticks)).getHumanDate() must beSome(FileInfo.MinDate)
      DefaultFileInfo.copy(date = Some(FileInfo.MaxMachineDayticks)).getHumanDate() must beSome(FileInfo.MaxDate)

      DefaultFileInfo.copy(date = Some(36157)).getHumanDate() must beSome(LocalDate.of(2050, 9, 29))
      DefaultFileInfo.copy(date = Some(36156)).getHumanDate() must beSome(LocalDate.of(2050, 9, 28))
      DefaultFileInfo.copy(date = Some(23073)).getHumanDate() must beSome(LocalDate.of(2025, 1, 1))

      DefaultFileInfo.copy(date = Some(10273)).getHumanDate() must beSome(LocalDate.of(2000, 1, 1))
      DefaultFileInfo.copy(date = Some(10305)).getHumanDate() must beSome(LocalDate.of(2000, 2, 1))
      DefaultFileInfo.copy(date = Some(10337)).getHumanDate() must beSome(LocalDate.of(2000, 3, 1))
      DefaultFileInfo.copy(date = Some(10849)).getHumanDate() must beSome(LocalDate.of(2001, 3, 1))
      DefaultFileInfo.copy(date = Some(11361)).getHumanDate() must beSome(LocalDate.of(2002, 3, 1))
    }

  }
}
