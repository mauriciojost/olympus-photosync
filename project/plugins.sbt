// The Typesafe repository
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += Resolver.url("bintray-banno-oss-releases", url("https://dl.bintray.com/banno/oss"))(Resolver.ivyStylePatterns)

addSbtPlugin("com.banno" % "sbt-plantuml-plugin" % "1.1.1")

addSbtPlugin("com.typesafe.sbt" %% "sbt-native-packager" % "1.1.0")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.0")

addSbtPlugin("org.scoverage" % "sbt-coveralls" % "1.1.0")

addSbtPlugin("org.planet42" % "laika-sbt" % "0.7.0")
