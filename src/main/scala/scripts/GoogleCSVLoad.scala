//package scripts
//
//import java.sql.Timestamp
//
//import persistence.entities.representations.{GOBUMAP_R, GOREMAL_R, GoogleIdentity}
//import persistence.entities.tables.GOREMAL
//import slick.lifted.TableQuery
//import utils.configuration.ConfigurationModuleImpl
//import utils.persistence.PersistenceModuleImpl
//
//import scala.concurrent.duration.Duration
//import scala.concurrent.{Await, Future}
//import scala.concurrent.ExecutionContext.Implicits.global
//import scala.util.{Failure, Success, Try}
//
///**
//  * Created by davenpcm on 4/15/2016.
//  */
//object  GoogleCSVLoad extends App {
//
//  val modules = new ConfigurationModuleImpl with PersistenceModuleImpl
//  import modules.dbConfig.driver.api._
//
//  val fileName = "C:/Users/davenpcm/Downloads/gam-id-email.csv"
//
//  /**
//    * CSV Parse For Creating The Google Identity
//    *
//    * @param path This is a path to find the csv that will be parsed
//    * @return A Set of Google Identities That Can Continue through the process
//    */
//  def ReturnGoogleIdentities(path: String): Seq[GoogleIdentity] = {
//
//    def identsCreate(path: String): Seq[GoogleIdentity] =  {
//      val bufferedSource = io.Source.fromFile(path)
//      var Idents: Seq[GoogleIdentity] = Seq[GoogleIdentity]()
//      for (line <- bufferedSource.getLines) {
//        val cols = line.split(",").map(_.trim)
//        Idents = Idents :+ GoogleIdentity(cols(0), cols(1))
//      }
//      bufferedSource.close()
//      Idents
//    }
//
//    val idents = identsCreate(path)
//
//    idents.drop(1)
//  }
//
//  /**
//    * This Function Takes A GoogleIdentity and Performs Database IO As A SideEffect, Or Nothing As Necessary
//    * Welcome to the void...
//    *
//    * @param googleIdentity A Google Identity
//    */
//  def GOBUMAPInsertUpdate(googleIdentity: GoogleIdentity): Future[Int] = {
//
//    /**
//      * Function checks GOREMAL for the email associated with the Google Identity and if it is the highest email type
//      * associated with
//      *
//      * @param googleIdentity A Google Identity
//      * @return Option of a PIDM
//      */
//    def GOREMALPIDMFIND(googleIdentity: GoogleIdentity): Future[Option[Int]] = {
//
//      /**
//        * This function finds a matching record in GOREMAL to the googleidentities email and where it is in a set
//        * type of codes
//        *
//        * @param googleIdentity Take a Google Idendtity
//        * @param emaltype This is a filter for valid EMAL Codes
//        * @return An Option of A GOREMAL_R
//        */
//      def goremalrecmatch(googleIdentity: GoogleIdentity, emaltype: List[String]): Future[Option[GOREMAL_R]] = {
//        val goremalpidm = modules.goremalDal.findByFilter(v =>
//          v.email === googleIdentity.primaryEmail
//        ).map( rec => rec.filter(a => emaltype.contains(a.emal_code))
//        ).map( rec => rec.headOption)
//
//        goremalpidm
//      }
//
//      /**
//        *
//        * This takes a record and sees if it is the best record. Of the record types the priority is alphabetical which
//        * allows use to sortwith less than.
//        *
//        * @param gor Option of a GOREMAL Record
//        * @param emaltype A List to filter for valid EMAL Codes
//        * @return
//        */
//      def bestrecfind(gor: Future[Option[GOREMAL_R]], emaltype: List[String]): Future[Option[GOREMAL_R]] = {
//
//        gor.flatMap {
//          case None => Future(None)
//          case Some(rec) =>
//
//            val futureIsBestEmail = modules.goremalDal.findByFilter(
//              record =>
//                record.pidm === rec.pidm
//            ).map(rec =>
//              rec.filter(a => emaltype.contains(a.emal_code))
//            ).map(
//              gor => gor.sortWith(_.emal_code < _.emal_code)
//            ).map(
//              recs => recs.headOption
//            )
//
//            futureIsBestEmail
//
//        }
//
//      }
//
//      /**
//        * This function filters the goremal record to efficiently run through whether the record returned anything,
//        * and if it returned see if it is the best record. If it is the best record, we use that otherwise we
//        * return None
//        *
//        * @param goremalrec Option of a Goremal record
//        * @param emaltype emaltypes to filter results
//        * @return Option of a PIDM. Returns PIDM if the Record Exists and it is the Best Record
//        */
//      def selectreturntype(goremalrec: Future[Option[GOREMAL_R]], emaltype: List[String]): Future[Option[Int]] = goremalrec.flatMap{
//        case None => Future(None)
//        case Some(s) =>
//          val bestrec = bestrecfind(goremalrec, emaltype)
//
//          if (bestrec == goremalrec){
//            Future(Some(s.pidm))
//          }
//          else {
//            Future(None)
//          }
//
//      }
//
//
//      val emaltypes = List("CA","CAS", "ECA", "ZCA", "ZCAS", "ZCH")
//      val goremalrec = goremalrecmatch(googleIdentity, emaltypes)
//
//      selectreturntype(goremalrec, emaltypes)
//
//    }
//
//    /**
//      * Simple Insert Function Wrapper Creating A Record and Then Inserting It into GOBUMAP
//      *
//      * @param googleIdentity A google identity
//      * @param pidm A pidm -> It is required to do this operation so it is not an Option
//      * @return Int The PIDM => Database IO As a Side Effect
//      */
//    def Insert(googleIdentity: GoogleIdentity, pidm: Int): Future[Int] =  {
//      val date = new java.util.Date()
//      val timestamp = new Timestamp(date.getTime)
//
//      val gobumapR = GOBUMAP_R(pidm , googleIdentity.id, timestamp, timestamp, "davenpcm")
////      println("gobumapR", gobumapR)
//      val future = modules.gobumapDal.insert(gobumapR)
//      future
//    }
//
//    /**
//      * Simple Update Function. Creates a TimeStamp Before Updating A GOBUMAP RECORD
//      *
//      * @param googleIdentity A google identity
//      * @param pidm A Pidm -> If it has gotten to this point it will exist
//      * @return Int 0 If Failed => We Are Doing Database IO As a Side Effect
//      */
//    def Update(googleIdentity: GoogleIdentity, pidm: Int): Future[Int] = {
//      val date = new java.util.Date()
//      val timestamp = new Timestamp(date.getTime)
//
//      val gobumapR = GOBUMAP_R(pidm, googleIdentity.id, timestamp, timestamp, "davenpcm")
//
//      val future = modules.gobumapDal.update(gobumapR)
//
//      future
//    }
//
//    /**
//      * This Checks A Option of a Pidm and the GoogleIdentity Record against GOBUMAP To Return What The Appropriate
//      * Action to Take Moving Forward IS
//      *
//      * @param googleIdentity A Google Identity
//      * @param possiblePidm An Option of a Pidm - What Was Found Through GOREMALPIDMFIND
//      * @return This returns an Option of a Boolean. If No Pidm was found already, or the record already exists will
//      *         return none. If the pidm exists in the table but the udc_id doesnt match then we update return
//      *         some(false) and if it is not present at all we return some(true)
//      */
//    def CheckIfNotExistsGOBUMAP(googleIdentity: GoogleIdentity, possiblePidm : Option[Int]):
//    Future[Option[Boolean]] = Future {
//      possiblePidm match {
//
//        case None => None
//        case Some(s) =>
//          //      Check if the same record already exists
//          val futuresameidentexists = modules.gobumapDal.findByFilter(gobu =>
//            gobu.pidm === s &&
//              gobu.UDC_ID === googleIdentity.id
//          ).map(_.isEmpty)
//
//          futuresameidentexists map {
//            case false => None
//            case true =>
//
//              val pidmgobumaprecs = modules.gobumapDal.findByFilter(gobu =>
//                gobu.pidm === s
//              ).map(_.isEmpty)
//              // If pidm does not exist return
//
//              pidmgobumaprecs.map(f => Some(f))
//          }
//      }
//    }
//
//    val pidm = GOREMALPIDMFIND(googleIdentity)
//
//    pidm.flatMap{ pid =>
//
//      val exists = CheckIfNotExistsGOBUMAP(googleIdentity, pid)
//
//      exists.flatMap {
//        case None => Future(0)
//        case Some(b) =>
//          b match {
//          case false => Update(googleIdentity, pid.get)
//          case true => Insert(googleIdentity, pid.get)
//        }
//      }
//    }
//
//  }
//
//  def futureToFutureTry[T](f: Future[T]): Future[Try[T]] =
//    f.map(Success(_)).recover({case e => Failure(e)})
//
//  val idents = ReturnGoogleIdentities(fileName)
//
//  val futures = idents.map(GOBUMAPInsertUpdate(_))
//  val listoffuturetrys = futures.map(futureToFutureTry(_))
//  val futureListofTrys = Future.sequence(listoffuturetrys)
//
//
//  val futuresResult = Await.result(futureListofTrys, Duration.Inf)
//  futuresResult.foreach(println(_))
//
//}
