package org.mauritania.photosync.olympus.sync

import java.time.LocalDate

object FileInfoFilter {

  def isFileEligible(f: FileInfo, c: Criteria): Boolean = {
    val date = f.getHumanDate
    val fromRespected = c.fromDate.forall(date.isAfter(_))
    val untilRespected = c.untilDate.forall(date.isBefore(_))
    fromRespected && untilRespected
  }

  case class Criteria(
    fromDate: Option[LocalDate],
    untilDate: Option[LocalDate]
  )

  object Criteria {
    val Bypass = Criteria(None, None)
  }

}
