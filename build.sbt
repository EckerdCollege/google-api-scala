name := "Banner-Jobs"

mainClass in Compile := Some("GoogleUpdateGobumap")

scalaVersion := "2.11.8"

libraryDependencies ++= List(
  "com.typesafe.slick" %% "slick" % "3.1.0",
  "org.slf4j" % "slf4j-nop" % "1.7.10",
  "com.h2database" % "h2" % "1.4.187",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "com.typesafe.slick" %% "slick-extensions" % "3.1.0",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.1.0",
  "com.google.api-client" % "google-api-client" % "1.21.0",
  "com.google.apis" % "google-api-services-admin-directory" % "directory_v1-rev65-1.21.0",
  "com.google.api-client" % "google-api-client-extensions" % "1.6.0-beta"
)


unmanagedBase := baseDirectory.value / ".lib"
unmanagedResourceDirectories in Compile += baseDirectory.value / "src" / "resources"

resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/maven-releases/"
fork in run := true
