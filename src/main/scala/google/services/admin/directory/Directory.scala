package google.services.admin.directory
import google.services.Service
import scala.language.implicitConversions
/**
  * Created by davenpcm on 5/5/16.
  */
case class Directory(service: Service) {
  val groups = new groups(this)
  val members = new members(this)
  val photos = new photos(this)
  val users = new users(this)
}

