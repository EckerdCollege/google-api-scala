package google.services.admin.directory

import com.google.api.services.admin.directory.model.UserPhoto
import com.google.api.services.admin.directory.{Directory, DirectoryScopes}

import scala.util.{Failure, Success, Try}

/**
  * Created by davenpcm on 5/3/16.
  */
object photos {

  def get(userKey: String,
          service: Directory
         ): Either[Throwable, UserPhoto] = {

    val returnType = Try( service.users().photos().get(userKey).execute() )

    returnType match {
      case Success(value) => Right(value)
      case Failure(exception) => Left(exception)
    }
  }

}
