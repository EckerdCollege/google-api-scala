package scripts
import java.io.FileOutputStream

import com.google.api.services.admin.directory.model.User
import com.google.common.io.BaseEncoding
import persistence.entities.representations.Image

/**
  * Created by davenpcm on 4/27/16.
  */
object GooglePhotos extends App {

  /**
    * This function takes a user object and creates an image at the location specified or returns None if there was no
    * image to be written.
    * @param outputFolder This is the output folder for the
    * @param user This is a user object from google.
    * @return A tuple mapping the id to the option image created by the process.
    */
  def getGoogleImage(outputFolder: String, user: User): (String, Option[Image]) = {
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
    def convertUserToImage(user: User): Option[Image] = {
      val userPhoto = GoogleAdmin.GetUserPhoto(user.getId)
      userPhoto match {
        case Some(photo) =>
          val extension = mimeTypeToExtension(photo.getMimeType)
          val byteArray = convertToImage(getBase64CssImage(photo.getPhotoData))
          val parsedFolder = parseOutputFolder(outputFolder)
          val image = Image(s"$parsedFolder$id$extension", byteArray)
          Some(image)
        case None => None
      }

    }

    /**
      * Writes an image to disk at the location specified
      * @param image An Option of An Image Object
      * @return It should be the same Image Object Passed In. Performs SideEffect Writing to Disk As It Moves
      *         Through This Section.
      */
    def createFile(image: Option[Image]): Option[Image] = image match {
      case None => None
      case Some(i) =>
        val file = new FileOutputStream(i.filename)
        file.write(i.data)
        file.close()
        Some(i)
    }

    val image = convertUserToImage(user)
    val file = createFile(image)
    (id, file)
  }

  /**
    * Returns All Google Photos
    * @param outputFolder This is the folder to write to
    * @return A Sequence for all google users of the user id and the option of an image created.
    */
  def getAllGoogleImages(outputFolder: String): Seq[(String, Option[Image])] = {

    val parImages = GoogleAdmin.listAllUsers()
      .par
      .map(getGoogleImage(outputFolder, _))

    parImages.seq
  }

  val images = getAllGoogleImages("/home/davenpcm/Pictures/Student")
  images.foreach(println)

}
