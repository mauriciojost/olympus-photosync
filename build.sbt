enablePlugins(WindowsPlugin, UniversalPlugin, JavaAppPackaging)

name := "photosync"

version := "0.7-SNAPSHOT"

scalaVersion := "2.11.8"

mainClass in Compile := Some("org.mauritania.photosync.starter.Starter")

maintainer := "Mauricio Jost <mauriciojostx@gmail.com>"

packageSummary := "Custom startscript parameters"

resolvers ++= Seq(
  "Bintray repository" at "https://dl.bintray.com/scalaz-releases/"
)

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api" % "1.7.5",
  "org.slf4j" % "slf4j-log4j12" % "1.7.5",
  "com.typesafe" % "config" % "1.2.1",
  "com.github.scopt" %% "scopt" % "3.3.0")

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "org.specs2" %% "specs2-core" % "3.8.9" % "test",
  "org.specs2" %% "specs2-mock" % "3.8.9" % "test"
)


