package org.mauritania.photosync.olympus.sync

import java.time.LocalDate

import org.specs2.mutable.Specification


class FileInfoFilterSpec extends Specification {

  val Date2k0101 = LocalDate.of(2000, 1, 1)
  val Date2k0101Ticks = FileInfo.MinMachineDayticks

  val Date2k0102 = LocalDate.of(2000, 1, 2)
  val Date2k0102Ticks = FileInfo.MinMachineDayticks + 1

  val Date2k0103 = LocalDate.of(2000, 1, 3)
  val Date2k0103Ticks = FileInfo.MinMachineDayticks + 2

  val Bypass = FileInfoFilter.Criteria.Bypass
  val From2k0102 = FileInfoFilter.Criteria.Bypass.copy(fromDate = Some(Date2k0102))
  val Until2k0102 = FileInfoFilter.Criteria.Bypass.copy(untilDate = Some(Date2k0102))

  val FileInfo2k0101 = FileInfo(folder = "FOLDER", name = "NAME", size = 0L, Date2k0101Ticks)
  val FileInfo2k0102 = FileInfo(folder = "FOLDER", name = "NAME", size = 0L, Date2k0101Ticks + 1)
  val FileInfo2k0103 = FileInfo(folder = "FOLDER", name = "NAME", size = 0L, Date2k0101Ticks + 2)

  "The files filter" should {

    "correctly keep all files in bypass mode" in {
      FileInfoFilter.isFileEligible(FileInfo2k0101, Bypass) must beTrue
      FileInfoFilter.isFileEligible(FileInfo2k0102, Bypass) must beTrue
      FileInfoFilter.isFileEligible(FileInfo2k0103, Bypass) must beTrue
    }

    "correctly filter all files before and equal 'from' criteria" in {
      FileInfoFilter.isFileEligible(FileInfo2k0101, From2k0102) must beFalse
      FileInfoFilter.isFileEligible(FileInfo2k0102, From2k0102) must beFalse
      FileInfoFilter.isFileEligible(FileInfo2k0103, From2k0102) must beTrue
    }

    "correctly filter all files after and equal to 'until' criteria" in {
      FileInfoFilter.isFileEligible(FileInfo2k0101, Until2k0102) must beTrue
      FileInfoFilter.isFileEligible(FileInfo2k0102, Until2k0102) must beFalse
      FileInfoFilter.isFileEligible(FileInfo2k0103, Until2k0102) must beFalse
    }

  }
}
