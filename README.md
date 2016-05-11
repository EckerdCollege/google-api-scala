Google-API-Scala
================

This simple api is a wrapper for the google java libraries. Currently mapping Admin Directory, Drive,
and Calendar however with intention to expand the scope moving forward.

#### Setup
Until I have this published you will need to publish locally first then.

In build.sbt
```sbt
libraryDependencies += "tech.christopherdavenport" %% "google-api-scala" % "0.01-SNAPSHOT"
```

This is what a default configuration looks like that allows you to access each service with the least leg work.

```conf
google = {
    applicationName = "Example Application"
    serviceAccountEmail = "exampleServiceAccountEmail@christopherdavenport.tech"
    credentialFilePath = "/path/to/local/file"
    administratorEmail = "administratorEmail@christopherdavenport.tech"
    domain = "christopherdavenport.tech"
}
```

#### Example

List All Groups In Your Organization
```scala
import tech.christopherdavenport.google.api.services.admin.directory.Directory
object Test extends App{

  implicit val AdminDirectory = Directory()
  val groups = AdminDirectory.groups.list()
  groups.foreach(println)

}
```




