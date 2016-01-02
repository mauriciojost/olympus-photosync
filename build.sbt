import com.typesafe.sbt.SbtNativePackager._
import NativePackagerKeys._

name := "photosync"

version := "0.5"


mainClass in Compile := Some("org.mauritania.photosync.starter.Starter")


packageArchetype.java_server


maintainer in Windows := "Mauricio Jost <mauriciojostx@gmail.com>"


packageSummary in Windows := "Custom startscript parameters"


scalaVersion := "2.9.1"


libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api" % "1.7.5",
  "org.slf4j" % "slf4j-log4j12" % "1.7.5",
  "com.typesafe" % "config" % "1.2.1",
  "com.github.scopt" %% "scopt" % "3.3.0")

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "1.6.1" % "test",
  "org.specs2"    %% "specs2"    % "1.5" % "test"
)
