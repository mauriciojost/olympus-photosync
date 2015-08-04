name := """photosync"""

organization := """org.mauritania"""

version := """0.1-SNAPSHOT"""

scalaVersion := "2.11.6"

fork in run := true

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0"

libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.12"

libraryDependencies += "com.github.scopt" %% "scopt" % "3.3.0"
