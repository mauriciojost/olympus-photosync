package org.mauritania.photosync.starter

import org.mauritania.photosync.olympus.client.CameraClientConfig
import org.mauritania.photosync.olympus.sync.FileInfoFilter

case class PhotosyncConfig(

  /**
    * Configuration for the client.
    */
  client: CameraClientConfig,

  /**
    * Local output directory, where photos will be downloaded.
    */
  outputDirectory: String,

  /**
    * If present, media that was created strictly after or before the provided date will not be downloaded.
    */
  mediaFilter: FileInfoFilter.Criteria,

  /**
    * If true, start the GUI.
    */
  gui: Boolean,


  /**
    * If true, dump the configuration file template for customization.
    */
  initConfig: Boolean,

  /**
    * If true, shut down camera when sync is complete
    */
  shutDownAfterSync: Boolean

)
