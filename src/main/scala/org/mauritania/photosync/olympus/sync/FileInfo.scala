package org.mauritania.photosync.olympus.sync

case class FileInfo(
  folder: String,
  name: String,
  size: Long
) {
  override def toString(): String = {
    s"FileInfo(folder=$folder, name=$name, size=$size)"
  }
  def getFileId: String = {
    folder + "/" + name
  }
}

