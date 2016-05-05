//package google.services.drive.models
//
//import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
//import com.google.api.client.http.javanet.NetHttpTransport
//import com.google.api.client.json.jackson2.JacksonFactory
//
///**
//  * Created by davenpcm on 5/4/16.
//  */
//case class Drive(googleCredential: GoogleCredential, applicationName: String) {
//
//  implicit def toGoogleDrive: com.google.api.services.drive.Drive = {
//      val httpTransport = new NetHttpTransport
//      val jsonFactory = new JacksonFactory
//      val drive = new com.google.api.services.drive.Drive.Builder(httpTransport, jsonFactory, googleCredential)
//        .setApplicationName(applicationName)
//        .setHttpRequestInitializer(googleCredential)
//        .build()
//      drive
//  }
//
//}
