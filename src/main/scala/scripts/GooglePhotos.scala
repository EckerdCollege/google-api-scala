package scripts
import java.io.FileOutputStream

import google.services.admin.directory.Directory
import com.google.api.services.admin.directory.model.User
import com.google.common.io.BaseEncoding
import persistence.entities.representations.Image
import scala.util.{Success, Failure}

/**
  * Created by davenpcm on 4/27/16.
  */
object GooglePhotos {

  /**
    * This function takes a user object and creates an image at the location specified or returns None if there was no
    * image to be written.
    * @param outputFolder This is the output folder for the
    * @param user This is a user object from google.
    * @return A tuple mapping the id to the option image created by the process.
    */
  def getGoogleImage(outputFolder: String, user: User, service: Directory): (User, Option[Image]) = {


    val id = user.getId
    /**
      * Simple Parse to Make Sure the Folder String is Properly Ended with a Slash
      * @param outputFolder The initial output folder
      * @return The parsed output folder with a trailing slash
      */
    def parseOutputFolder(outputFolder: String): String = {
      if (outputFolder.endsWith("/")) outputFolder else outputFolder + "/"
    }

    /**
      * Transforms a urlsafe string into the appropriate CSS image string.
      * @param urlSafeBase64Data The original string
      * @return The final string that is appropriate CSS to generate an image
      */
    def getBase64CssImage(urlSafeBase64Data: String): String = {
      val urlSafeBase64DataNew = BaseEncoding.base64().encode(BaseEncoding.base64Url().decode(urlSafeBase64Data))
      urlSafeBase64DataNew
    }

    /**
      * Takes base64css string and converts it to a file
      * @param base64css A CSS string
      * @return An array of bytes that is an image file. Needs to be written  to disk.
      */
    def convertToImage(base64css: String): Array[Byte] = {
      BaseEncoding.base64().decode(base64css)
    }


    /**
      * This takes a full mimetype and creates the appropriate file extension for interpretation by other programs
      * @param mimeType Full mimeType is the format image/format such as image/jpeg. Supported types by google when this
      *                 was written or jpeg, png, gif, bmp and tiff.
      * @return Returns an extension such as .jpeg or .png
      */
    def mimeTypeToExtension(mimeType: String): String = {
      mimeType.replaceFirst("image/", ".")
    }

    /**
      * Take a google model user and gets the users image if it exists
      * @param user A google user
      * @return An Option of an Image if it Exists
      */
    def convertUserToImage(user: User, service: Directory): Option[Image] = {
      val userPhoto = service.photos.get(user.getId)
      userPhoto match {
        case Success(photo) =>
          val extension = mimeTypeToExtension(photo.getMimeType)
          val byteArray = convertToImage(getBase64CssImage(photo.getPhotoData))
          val parsedFolder = parseOutputFolder(outputFolder)
          val image = Image(s"$parsedFolder$id$extension", byteArray)
          Some(image)
        case Failure(error) =>  println(error); None
      }
    }

    val image = convertUserToImage(user, service)

    (user, image)
  }

  /**
    * Writes an image to disk at the location specified
    * @param image An Option of An Image Object
    * @return It should be the same Image Object Passed In. Performs SideEffect Writing to Disk As It Moves
    *         Through This Section.
    */
  def create(image: Option[Image]): Unit = image match {
    case None =>
    case Some(i) =>
      val file = new FileOutputStream(i.filename)
      file.write(i.data)
      file.close()
  }

  /**
    * Returns All Google Photos
    * @param outputFolder This is the folder to write to
    * @return A Sequence for all google users of the user id and the option of an image created.
    */
  def getAllGoogleImages( outputFolder: String, service: Directory): Seq[(User, Option[Image])] = {
    import google.language.JavaConverters._
    val users = service.users.list()

    val photos = users
        .par.map(user => getGoogleImage(outputFolder, user.asJava, service))

    photos.foreach(tuple => create(tuple._2))

    photos.seq
  }


}
