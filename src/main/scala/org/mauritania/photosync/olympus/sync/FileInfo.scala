package org.mauritania.photosync.olympus.sync

case class FileInfo(
  name: String,
  size: Long
) {
  override def toString(): String = {
    "FileInfo(name=" + name + ",size=" + size + ")"
  }
}

