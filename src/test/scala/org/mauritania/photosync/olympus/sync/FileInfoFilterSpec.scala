package org.mauritania.photosync.olympus.sync

import java.time.LocalDate

import org.specs2.mutable.Specification


class FileInfoFilterSpec extends Specification {

  "The files filter" should {

    val Date2k0101Ticks = FileInfo.MinMachineDayticks
    val Date2k0102 = LocalDate.of(2000, 1, 2)

    val BypassCriteria = FileInfoFilter.Criteria.Bypass
    val From2k0102Criteria = FileInfoFilter.Criteria.Bypass.copy(fromDateCondition = Some(Date2k0102))
    val Until2k0102Criteria = FileInfoFilter.Criteria.Bypass.copy(untilDateCondition = Some(Date2k0102))

    val FileInfo2k0101 = FileInfo(folder = "FOLDER", name = "NAME", size = 0L, Date2k0101Ticks)
    val FileInfo2k0102 = FileInfo(folder = "FOLDER", name = "NAME", size = 0L, Date2k0101Ticks + 1)
    val FileInfo2k0103 = FileInfo(folder = "FOLDER", name = "NAME", size = 0L, Date2k0101Ticks + 2)

    "correctly keep all files in bypass mode" in {
      FileInfoFilter.isFileEligible(FileInfo2k0101, BypassCriteria) must beTrue
      FileInfoFilter.isFileEligible(FileInfo2k0102, BypassCriteria) must beTrue
      FileInfoFilter.isFileEligible(FileInfo2k0103, BypassCriteria) must beTrue
    }

    "correctly keep all files before and equal 'from' criteria" in {
      FileInfoFilter.isFileEligible(FileInfo2k0101, From2k0102Criteria) must beFalse
      FileInfoFilter.isFileEligible(FileInfo2k0102, From2k0102Criteria) must beTrue
      FileInfoFilter.isFileEligible(FileInfo2k0103, From2k0102Criteria) must beTrue
    }

    "correctly keep all files after and equal to 'until' criteria" in {
      FileInfoFilter.isFileEligible(FileInfo2k0101, Until2k0102Criteria) must beTrue
      FileInfoFilter.isFileEligible(FileInfo2k0102, Until2k0102Criteria) must beTrue
      FileInfoFilter.isFileEligible(FileInfo2k0103, Until2k0102Criteria) must beFalse
    }

    def fileWithName(n: String) = FileInfo(folder = "FOLDER", name = n, size = 0L, Date2k0101Ticks)
    def filter(n: String*) = FileInfoFilter.Criteria.Bypass.copy(fileNameConditions = Some(n.toSeq))

    "correctly filter all files after with extension 'avi'" in {
      FileInfoFilter.isFileEligible(fileWithName("xx.avo"), filter("*.avi")) must beFalse
      FileInfoFilter.isFileEligible(fileWithName("xx.AVI"), filter("*.avi")) must beFalse
      FileInfoFilter.isFileEligible(fileWithName("xx.avi"), filter("*.avi")) must beTrue
    }

    "correctly filter all files after containing 'XX'" in {
      FileInfoFilter.isFileEligible(fileWithName("xx.avo"), filter("*XX*")) must beFalse
      FileInfoFilter.isFileEligible(fileWithName("xxxxxx.AVI"), filter("*XX*")) must beFalse
      FileInfoFilter.isFileEligible(fileWithName("xxXXxx.avi"), filter("*XX*")) must beTrue
    }

    "correctly filter all files with combined filters (one or the other)" in {
      FileInfoFilter.isFileEligible(fileWithName("xx.avo"), filter("*this*", "*.avi")) must beFalse
      FileInfoFilter.isFileEligible(fileWithName("xx.avi"), filter("*this*", "*.avi")) must beTrue
      FileInfoFilter.isFileEligible(fileWithName("xxthispp.avo"), filter("*this*", "*.avi")) must beTrue
    }

  }
}
