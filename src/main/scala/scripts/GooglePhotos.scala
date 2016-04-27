package scripts
import java.io.FileOutputStream

import com.google.api.services.admin.directory.model.User
import com.google.common.io.BaseEncoding

/**
  * Created by davenpcm on 4/27/16.
  */
object GooglePhotos extends App {
//  val identifier= "davenpcm@eckerd.edu"
//  val photo = GoogleAdmin.GetUserPhoto(identifier)
//
//  val phototype = photo.getMimeType
//  println(s"Mime Type - ${phototype}")
//  val photodata = photo.getPhotoData
//  println(s"Data - ${photodata}")
//  val id = photo.getId
//  println(s"Id - ${id}")
//
//  val base64css = getBase64CssImage(photodata)
//  println(base64css)
//
//  val converted = convertToImage(base64css)
//  println(converted)
//
//  val mimeExt1 = mimeTypeToExtension(phototype)
//  println(s"MimeExt1 - ${mimeExt1}")
//  val mimeExt2 = mimeTypeToExtension2(phototype)
//  println(s"MimeExt2 - ${mimeExt2}")

  def getBase64CssImage(urlSafeBase64Data: String): String = {
    val urlSafeBase64DataNew = BaseEncoding.base64().encode(BaseEncoding.base64Url().decode(urlSafeBase64Data))
    urlSafeBase64DataNew
  }

  def convertToImage(base64css: String): Array[Byte] = {
    BaseEncoding.base64().decode(base64css)
  }


  def mimeTypeToExtension(mimeType: String):String = mimeType match {
    case a if a == "image/jpeg" => ".jpg"
    case a if a == "image/png" => ".png"
    case a if a == "image/gif" => ".gif"
    case a if a == "image/bmp" => ".bmp"
    case a if a == "image/tiff" => ".tiff"
    case _ => ".unknown"
  }

  def mimeTypeToExtension2(mimeType: String): String = {
    mimeType.replaceFirst("image/", ".")
  }

//  val file = new FileOutputStream(s"/home/davenpcm/Pictures/Student/${id}.jpg")
//  file.write(converted)
//  file.close()
  case class Image(filename: String, data: Array[Byte])

  def convertUserToImage(user: User): Option[Image] = {
    val userPhoto = GoogleAdmin.GetUserPhoto(user.getId)
    userPhoto match {
      case Some(photo) =>
        val extension = mimeTypeToExtension( photo.getMimeType)
        val byteArray = convertToImage( getBase64CssImage( photo.getPhotoData ))
        val image = Image(s"${user.getId}$extension", byteArray)
        Some(image)
      case None => None
    }
  }

  def createFile(image: Option[Image]) : Unit = image match {
    case None =>
    case Some(i) =>
      val folder = "/home/davenpcm/Pictures/Student/"
      val file = new FileOutputStream(folder + i.filename)
      file.write(i.data)
      file.close()
  }
  val UserImages = GoogleAdmin.listAllUsers().par.map(user => convertUserToImage(user))
  UserImages.foreach(createFile(_))
  UserImages.foreach(println(_))





}
