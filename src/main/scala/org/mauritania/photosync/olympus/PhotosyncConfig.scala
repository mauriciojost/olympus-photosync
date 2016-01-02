package org.mauritania.photosync.olympus

import org.mauritania.photosync.olympus.client.CameraClientConfig

case class PhotosyncConfig(

  /**
    * Configuration for the client.
    */
  client: CameraClientConfig,

  /**
    * Local output directory, where photos will be downloaded.
    */
  outputDirectory: String

)
