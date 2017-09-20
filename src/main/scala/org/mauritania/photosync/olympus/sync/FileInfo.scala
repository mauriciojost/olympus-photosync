package org.mauritania.photosync.olympus.sync

case class FileInfo(
  folder: String,
  name: String,
  size: Long,
  date: Option[Int] = None,
  time: Option[Int] = None
) {
  override def toString(): String = {
    s"FileInfo(folder=$folder, name=$name, size=$size, date=$date, time=$time)"
  }
  def getFileId: String = {
    folder + "/" + name
  }
}

