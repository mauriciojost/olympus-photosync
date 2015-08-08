import com.typesafe.sbt.SbtNativePackager._
import NativePackagerKeys._

name := "photosync"

version := "0.1"

mainClass in Compile := Some("org.mauritania.photosync.main.Starter")

packageArchetype.java_server

maintainer in Windows := "Mauricio Jost <mauricio.jost@gmail.com>"

packageSummary in Windows := "Custom startscript parameters"

scalaVersion := "2.9.1"

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api" % "1.6.4",
  "org.slf4j" % "slf4j-simple" % "1.6.4",
  "com.github.scopt" %% "scopt" % "3.3.0"
)
