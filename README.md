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

object Test extends App {

  implicit val AdminDirectory = Directory()
  val groups = AdminDirectory.groups.list()
  groups.foreach(println)

}
```

Creating A Group
```scala
import tech.christopherdavenport.google.api.services.admin.directory.Directory
import tech.christopherdavenport.google.api.services.admin.directory.models.Group

object Test extends App {

  implicit val AdminDirectory = Directory()
  val newGroup = AdminDirectory.groups.create(
    Group("Group Name", "newGroupEmail@christopherdavenport.tech")
  )
  println(newGroup)

}
```

List All Users In Your Organization
```scala
import tech.christopherdavenport.google.api.services.admin.directory.Directory

object Test extends App {

  implicit val AdminDirectory = Directory()
  val users = AdminDirectory.users.list()
  users.foreach(println)

}
```

Creating A User
```scala
import tech.christopherdavenport.google.api.services.admin.directory.Directory
import tech.christopherdavenport.google.api.services.admin.directory.models.{Email, Name, User}

object Test extends App {

  implicit val AdminDirectory = Directory()
  def superSecretPasswordAlgorithm(): Option[String] = Some("PASSWORD")
  val newUser = AdminDirectory.users.create(
    User(
      Name("givenName", "familyName"),
      Email("newUser@christopherdavenport.tech"),
      superSecretPasswordAlgorithm()
    )
  )
  println(newUser)

}
```

What about members of every group in your organization. Caveat - Takes a chunk of time if you have a lot of users.
```scala
import tech.christopherdavenport.google.api.services.admin.directory.Directory

object Test extends App {

  implicit val AdminDirectory = Directory()
  val groupsWithMembers = AdminDirectory.groups.list().map(_.getMembers)
  groupsWithMembers.foreach(println)

}
```



