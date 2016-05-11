name := "google-api-scala"
organization := "tech.christopherdavenport"
version := "0.01-SNAPSHOT"

mainClass in Compile := Some("CommandLine")

scalaVersion := "2.11.8"

libraryDependencies ++= List(
  "com.typesafe" % "config" % "1.3.0",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test",
  "com.google.api-client" % "google-api-client" % "1.22.0",
  "com.google.apis" % "google-api-services-admin-directory" % "directory_v1-rev67-1.22.0",
  "com.google.apis" % "google-api-services-calendar" % "v3-rev180-1.22.0",
  "com.google.apis" % "google-api-services-gmail" % "v1-rev41-1.22.0",
  "com.google.apis" % "google-api-services-drive" % "v3-rev23-1.22.0"
)

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")
resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/maven-releases/"
fork in run := true
