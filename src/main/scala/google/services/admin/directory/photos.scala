package google.services.admin.directory

import com.google.api.services.admin.directory.model.UserPhoto
import google.language.JavaConverters._

import scala.util.Try

/**
  * Created by davenpcm on 5/3/16.
  */
class photos(directory: Directory) {
  private val service = directory.asJava

  def get(userKey: String): Try[UserPhoto] = {

    val returnType: Try[UserPhoto] = Try( service.users().photos().get(userKey).execute() )

    returnType
  }

}
