package org.mauritania.photosync.olympus.sync

import java.time.LocalDate
import java.nio.file.{FileSystems, Paths}

object FileInfoFilter {

  val PatternPrefix = "glob:"

  def isFileEligible(f: FileInfo, c: Criteria): Boolean = {
    val fileDate = f.getHumanDate
    val fileName = f.name
    val fromRespected = c.fromDateCondition.forall(d => fileDate.isAfter(d) || fileDate.isEqual(d))
    val untilRespected = c.untilDateCondition.forall(d => fileDate.isBefore(d) || fileDate.isEqual(d))
    val nameRespected = c.fileNameConditions.forall(patterns => fileNameConforms(fileName, patterns))
    fromRespected && untilRespected && nameRespected
  }

  private def fileNameConforms(fileName: String, patterns: Seq[String]): Boolean = {
    def toUpperCase(s: String) = s.toUpperCase
    val file = Paths.get(toUpperCase(fileName))
    val matchers = patterns.map(p => FileSystems.getDefault.getPathMatcher(PatternPrefix + toUpperCase(p)))
    matchers.exists(matcher => matcher.matches(file))
  }

  case class Criteria(
    fromDateCondition: Option[LocalDate],
    untilDateCondition: Option[LocalDate],
    fileNameConditions: Option[Seq[String]]
  )

  object Criteria {
    val Bypass = Criteria(None, None, None)
  }

}
