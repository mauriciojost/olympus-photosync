package org.mauritania.photosync.starter.gui

case class GuiConfig(
  /**
    * Whether to show or not the file name in the list.
    */
  showFilename: Boolean,

  /**
    * The size (that affects width and height) of the thumbnails.
    */
  thumbnailSize: Int
)
