package scripts

import persistence.entities.representations.GoogleIdentity

/**
  * Created by davenpcm on 4/15/2016.
  */

object  GoogleCSVLoad extends App {

  val fileName = "C:/Users/davenpcm/Downloads/gam-id-email.csv"

  /**
    * CSV Parse For Creating The Google Identity
    *
    * @param path This is a path to find the csv that will be parsed
    * @return A Set of Google Identities That Can Continue through the process
    */
  def ReturnGoogleIdentities(path: String): Seq[GoogleIdentity] = {

    def identsCreate(path: String): Seq[GoogleIdentity] =  {
      val bufferedSource = io.Source.fromFile(path)
      var Idents: Seq[GoogleIdentity] = Seq[GoogleIdentity]()
      for (line <- bufferedSource.getLines) {
        val cols = line.split(",").map(_.trim)
        Idents = Idents :+ GoogleIdentity(cols(0), cols(1))
      }
      bufferedSource.close()
      Idents
    }

    val idents = identsCreate(path)

    idents.drop(1)
  }

}
