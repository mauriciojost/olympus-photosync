import com.typesafe.sbt.SbtNativePackager._
import NativePackagerKeys._

name := "photosync"

version := "0.2"

mainClass in Compile := Some("org.mauritania.photosync.main.Starter")

packageArchetype.java_server

maintainer in Windows := "Mauricio Jost <mauricio.jost@gmail.com>"

packageSummary in Windows := "Custom startscript parameters"

scalaVersion := "2.9.1"

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api" % "1.7.5",
  "org.slf4j" % "slf4j-log4j12" % "1.7.5",
  "com.github.scopt" %% "scopt" % "3.3.0"
)


