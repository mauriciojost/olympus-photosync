import com.typesafe.sbt.SbtNativePackager._
import NativePackagerKeys._

name := "photosync"

version := "1.0"

mainClass in Compile := Some("org.mauritania.photosync.main.Starter")

packageArchetype.java_server

maintainer in Windows := "Mauricio Jost <mauricio.jost@gmail.com>"

packageSummary in Windows := "Custom startscript parameters"

packageDescription := "Custom startscript parameters"

//libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0"

//libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.12"

libraryDependencies += "com.github.scopt" %% "scopt" % "3.3.0"
