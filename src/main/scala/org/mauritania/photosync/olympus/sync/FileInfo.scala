package org.mauritania.photosync.olympus.sync

import java.time.LocalDate

case class FileInfo(
  folder: String,
  name: String,
  size: Long,
  date: Option[Int] = None
) {

  import FileInfo._

  def getFileId: String = {
    folder + "/" + name
  }

  private def maskAndShift(i: Int, mask: Int, shift: Int): Int = (i & mask) >>> shift

  def getHumanDate(): Option[LocalDate] = {
    date.map { machineDays =>
      val days = maskAndShift(machineDays, MaskDays, 0)
      val months = maskAndShift(machineDays, MaskMont, 5)
      val years = maskAndShift(machineDays, MaskYear, 9) + 1980
      LocalDate.of(years, months, days)
    }
  }

}

object FileInfo {

  val MaskDays = Integer.parseInt("0000000000011111", 2)
  val MaskMont = Integer.parseInt("0000000111100000", 2)
  val MaskYear = Integer.parseInt("1111111000000000", 2)

  val MaxMachineDayticks = 61343
  val MinMachineDayticks = 10273

  val MaxDate = LocalDate.of(2099, 12, 31)
  val MinDate = LocalDate.of(2000, 1, 1)

}
