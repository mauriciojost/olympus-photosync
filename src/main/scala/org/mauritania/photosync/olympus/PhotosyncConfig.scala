package org.mauritania.photosync.olympus

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
  mediaFilter: FileInfoFilter.Criteria

)
