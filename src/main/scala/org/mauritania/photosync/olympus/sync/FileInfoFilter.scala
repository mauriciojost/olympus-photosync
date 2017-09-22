package org.mauritania.photosync.olympus.sync

import java.time.LocalDate

object FileInfoFilter {

  def isFileDiscardable(f: FileInfo, c: Criteria): Boolean = {
    val date = f.getHumanDate()
    val toDiscardBecauseAfter = c.discardAfter.map(date.isAfter(_)).getOrElse(false)
    val toDiscardBecauseBefore = c.discardBefore.map(date.isBefore(_)).getOrElse(false)
    toDiscardBecauseAfter || toDiscardBecauseBefore
  }

  case class Criteria(
    discardAfter: Option[LocalDate],
    discardBefore: Option[LocalDate]
  )

  object Criteria {
    val Bypass = Criteria(None, None)
  }

}
