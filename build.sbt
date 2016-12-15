name := "google-api-scala"
organization := "edu.eckerd"
version := "0.1.1-SNAPSHOT"

scalaVersion := "2.12.1"

libraryDependencies ++= List(
  "com.typesafe" % "config" % "1.3.1",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "org.typelevel" %% "cats" % "0.8.1",
  "com.google.api-client" % "google-api-client" % "1.22.0",
  "com.google.apis" % "google-api-services-admin-directory" % "directory_v1-rev67-1.22.0",
  "com.google.apis" % "google-api-services-calendar" % "v3-rev180-1.22.0",
//  "com.google.apis" % "google-api-services-gmail" % "v1-rev41-1.22.0",
  "com.google.apis" % "google-api-services-drive" % "v3-rev23-1.22.0"
)

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")
resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/maven-releases/"
fork in run := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := (
  <url>https://github.com/EckerdCollege/google-api-scala</url>
    <licenses>
      <license>
        <name>Apache-style</name>
        <url>http://www.apache.org/licenses/LICENSE-2.0</url>
        <distribution>repo</distribution>
      </license>
    </licenses>
    <scm>
      <url>git@github.com:EckerdCollege/google-api-scala.git</url>
      <connection>scm:git@github.com:EckerdCollege/google-api-scala.git</connection>
    </scm>
    <developers>
      <developer>
        <id>ChristopherDavenport</id>
        <name>Christopher Davenport</name>
        <email>christopherdavenport@outlook.com</email>
      </developer>
    </developers>
  )

credentials in ThisBuild ++= (for {
  username <- Option(System.getenv().get("SONATYPE_USERNAME"))
  password <- Option(System.getenv().get("SONATYPE_PASSWORD"))
} yield Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", username, password)).toSeq