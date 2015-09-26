package org.mauritania.photosync.olympus

import org.mauritania.photosync.olympus.client.CameraClientConfig

case class PhotosyncConfig(
  client: CameraClientConfig = CameraClientConfig(),
  outputDirectory: String = "output"
)
