package org.mauritania.photosync.olympus.sync

import java.time.LocalDate

case class FileInfo(
  folder: String,
  name: String,
  size: Long,
  date: Int = FileInfo.DefaultDate
) {

  import FileInfo._

  def getFileId: String = {
    folder + "/" + name
  }

  private def maskAndShift(i: Int, mask: Int, shift: Int): Int = (i & mask) >>> shift

  def getHumanDate(): LocalDate = {
    val days = maskAndShift(date, MaskDays, 0)
    val months = maskAndShift(date, MaskMont, 5)
    val years = maskAndShift(date, MaskYear, 9) + 1980
    LocalDate.of(years, months, days)
  }

}

object FileInfo {

  val MaskDays = Integer.parseInt("0000000000011111", 2)
  val MaskMont = Integer.parseInt("0000000111100000", 2)
  val MaskYear = Integer.parseInt("1111111000000000", 2)

  val MaxMachineDayticks = 61343
  val MinMachineDayticks = 10273

  val DefaultDate = MinMachineDayticks

  val MaxDate = LocalDate.of(2099, 12, 31)
  val MinDate = LocalDate.of(2000, 1, 1)

}
