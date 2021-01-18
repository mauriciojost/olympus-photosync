enablePlugins(WindowsPlugin, UniversalPlugin, JavaAppPackaging)

rpmRelease := "1"

rpmVendor := "mauritania"

rpmUrl := Some("https://github.com/mauriciojost/olympus-photosync")

rpmLicense := Some("Apache License Version 2.0")

name := "olympus-photosync"

scalaVersion := "2.12.7"

mainClass in Compile := Some("org.mauritania.photosync.starter.Starter")

maintainer := "Mauricio Jost <mauriciojostx@gmail.com>"

packageSummary := "Synchronize media from Olympus cameras to your PC wirelessly."

coverageMinimum := 92
coverageFailOnMinimum := false

parallelExecution in Test := false

resolvers ++= Seq(
  "Bintray repository" at "https://dl.bintray.com/scalaz-releases/"
)

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api" % "1.7.30",
  "org.slf4j" % "slf4j-log4j12" % "1.7.30",
  "com.typesafe" % "config" % "1.4.1",
  "com.github.scopt" %% "scopt" % "4.0.0",
  "org.scalafx" %% "scalafx" % "8.0.144-R12",
  "com.lihaoyi" %% "scalarx" % "0.3.2",
  "org.openjfx" % "javafx-controls" % "16-ea+6",
  "org.openjfx" % "javafx-fxml" % "16-ea+6"
)

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.3" % "test",
  "org.specs2" %% "specs2-core" % "4.10.6" % "test",
  "org.specs2" %% "specs2-mock" % "4.10.6" % "test",
  "commons-io" % "commons-io" % "2.8.0" % "test"
)


