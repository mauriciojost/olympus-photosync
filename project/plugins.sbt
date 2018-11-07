// The Typesafe repository
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += Resolver.url("bintray-banno-oss-releases", url("https://dl.bintray.com/banno/oss"))(Resolver.ivyStylePatterns)

addSbtPlugin("com.typesafe.sbt" %% "sbt-native-packager" % "1.3.11")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.6.0-M5")

addSbtPlugin("org.scoverage" % "sbt-coveralls" % "1.2.7")
