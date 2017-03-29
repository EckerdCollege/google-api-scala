Google-API-Scala [![Build Status](https://travis-ci.org/EckerdCollege/google-api-scala.svg?branch=0.1.x)](https://travis-ci.org/EckerdCollege/google-api-scala) [![codecov](https://codecov.io/gh/EckerdCollege/google-api-scala/branch/0.1.x/graph/badge.svg)](https://codecov.io/gh/EckerdCollege/google-api-scala) 
================

This API is a wrapper for the google java libraries. Currently mapping Admin Directory, Drive,
and Calendar however with intention to expand the scope moving forward.

This library is continuing to improve error handling and any bugs that occur in the 0.1.0 line however API changes for
the data model will be handled in the 0.2.0 release cycle.

#### Expected 0.2.0 Changes
It has become apparent for the need between construction and returnable data types as even though 
in the object oriented style this wraps, the data is extremely different in construction. 
It is likely people will also only want specific modules so rather than wrapping all the modules together they will 
each be published individually from a core module. So you will only get the Directory if all you want is the directory.
Finally we will be working to transition to a Functional Approach rather than an Object-Oriented Approach. It will
make it easier to interact if as a developer you can seperate Execution and Business Logic so hopefully this will make
it into 0.2.0 as well.

#### Setup

In build.sbt
```sbt
libraryDependencies += "edu.eckerd" %% "google-api-scala" % "0.1.0"
```

This is what a default configuration looks like that allows you to access each service with the least leg work. Now
they can be brought in either explicitly or via environmental variables. Feel free to fill them in to begin and then
later switch back to an Environmental variable approach.

```conf
google = {
  domain = ${?GOOGLE_DOMAIN}
  serviceAccountEmail = ${?GOOGLE_SERVICE_ACCOUNT}
  administratorEmail = ${GOOGLE_ADMINISTRATOR_ACCOUNT}
  credentialFilePath = ${?GOOGLE_CREDENTIAL_FILE_LOCATION}
  applicationName = ${?GOOGLE_APPLICATION_NAME}
}
```

#### Example

List All Groups In Your Organization
```scala
import edu.eckerd.google.api.services.directory.Directory

object Test extends App {
  implicit val AdminDirectory = Directory()
  val groups = AdminDirectory.groups.list()
  groups.foreach(println)
}
```

Creating A Group
```scala
import edu.eckerd.google.api.services.directory.Directory
import edu.eckerd.google.api.services.directory.models.Group

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
import edu.eckerd.google.api.services.directory.Directory

object Test extends App {
  implicit val AdminDirectory = Directory()
  val users = AdminDirectory.users.list()
  users.foreach(println)

}
```

Creating A User
```scala
import edu.eckerd.google.api.services.directory.Directory
import edu.eckerd.google.api.services.directory.models.{Email, Name, User}

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
import edu.eckerd.google.api.services.directory.Directory

object Test extends App {
  implicit val AdminDirectory = Directory()
  val groupsWithMembers = AdminDirectory.groups.list().map(_.getMembers)
  groupsWithMembers.foreach(println)
}
```



