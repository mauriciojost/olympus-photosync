package org.mauritania.photosync.olympus.client

import java.net.URL
import java.time.{LocalDate, LocalDateTime, LocalTime}

case class FileInfo(
  folder: String,
  name: String,
  size: Long,
  date: Int = FileInfo.DefaultDate,
  time: Int = FileInfo.DefaultTime,
  thumbnailUrl: Option[URL] = None // if local, no thumbnail will be available
) {

  import FileInfo._

  def getFileId: String = {
    folder + "/" + name
  }

  val humanDate: LocalDate = {
    // ..yyyyyyyymmmmddddd
    //   65432109876543210
    val days = maskShift(date, 4, 0)
    val months = maskShift(date, 8, 5)
    val years = maskShift(date, 16, 9) + 1980
    LocalDate.of(years, months, days)
  }

  val humanTime: LocalTime = {
    // ...hhhhhhmmmmmmsssss
    //    65432109876543210
    val s = FileInfo.maskShift(time, 4, 0)
    val m = FileInfo.maskShift(time, 10, 5)
    val h = FileInfo.maskShift(time, 16, 11)
    LocalTime.of(h, m, s)
  }

  val humanDateTime: LocalDateTime = humanDate.atTime(humanTime)

}

object FileInfo {

  val MaskDays = Integer.parseInt("0000000000011111", 2)
  val MaskMont = Integer.parseInt("0000000111100000", 2)
  val MaskYear = Integer.parseInt("1111111000000000", 2)

  val MaxMachineDayticks = 61343
  val MinMachineDayticks = 10273

  val DefaultDate = MinMachineDayticks
  val DefaultTime = 0

  val MaxDate = LocalDate.of(2099, 12, 31)
  val MinDate = LocalDate.of(2000, 1, 1)

  def maskShift(i: Int, upperMaskBitPos: Int, lowerMaskBitPos: Int): Int =
    (i % (2 << upperMaskBitPos)) >> lowerMaskBitPos

}
