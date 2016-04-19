package scripts

import java.sql.Timestamp

import persistence.entities.representations.{GOBUMAP_R, GOREMAL_R, GoogleIdentity}
import utils.configuration.ConfigurationModuleImpl
import utils.persistence.PersistenceModuleImpl

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * Created by davenpcm on 4/15/2016.
  */
object  GoogleCSVLoad2 extends App {

  val modules = new ConfigurationModuleImpl with PersistenceModuleImpl
  import modules.dbConfig.driver.api._

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

  /**
    * This is the Insert or Update Function takes the google identity to do all operations necessary for updating the
    * table.
    * @param googleIdentity GoogleIdentity Taken From The CSV
    * @return This returns a Future of a Tuple of String and Int So we can match the email address to the int mapping
    *         each google identity to the job that was performed within the GOBUMAP InsertUpdate
    */
  def GOBUMAPInsertUpdate(googleIdentity: GoogleIdentity): Future[(String,Int)] = {


    /**
      * This is the future wrangler. The other pieces return futures so we need to handle the futures to our integer
      * output
      *
      * @param googleIdentity A google Identity
      * @return An integer showing as 1 if information was updated, or the PIDM if they were inserted.
      */
    def googleStringer(googleIdentity: GoogleIdentity): Future[Int] = {


      /**
        * Function checks GOREMAL for the email associated with the Google Identity and if it is the highest email type
        * associated with
        *
        * @param googleIdentity A Google Identity
        * @return Option of a PIDM
        */
      def GoremalPidmFind(googleIdentity: GoogleIdentity): Future[Option[Int]] = {

        /**
          * This function finds a matching record in GOREMAL to the googleidentities email and where it is in a set
          * type of codes
          *
          * @param googleIdentity Take a Google Idendtity
          * @param emaltype       This is a filter for valid EMAL Codes
          * @return An Option of A GOREMAL_R
          */
        def goremalrecmatch(googleIdentity: GoogleIdentity, emaltype: List[String]): Future[Option[GOREMAL_R]] = {
          val goremalpidm = modules.goremalDal.findByFilter(v =>
            v.email === googleIdentity.primaryEmail
          ).map(rec => rec.filter(a => emaltype.contains(a.emal_code))
          ).map(rec => rec.headOption)

          goremalpidm
        }

        /**
          *
          * This takes a record and sees if it is the best record. Of the record types the priority is alphabetical
          * which allows use to sortwith less than.
          *
          * @param gor      Option of a GOREMAL Record
          * @param emaltype A List to filter for valid EMAL Codes
          * @return
          */
        def bestrecfind(gor: Option[GOREMAL_R], emaltype: List[String]): Future[Option[GOREMAL_R]] = gor match {
          case None => Future(None)
          case Some(rec) =>

            val futureIsBestEmail = modules.goremalDal.findByFilter(
              record =>
                record.pidm === rec.pidm
            ).map(rec =>
              rec.filter(a => emaltype.contains(a.emal_code))
            ).map(
              gor => gor.sortWith(_.emal_code < _.emal_code)
            ).map(
              recs => recs.headOption
            )
            futureIsBestEmail
        }


        /**
          * This function filters the goremal record to efficiently run through whether the record returned anything,
          * and if it returned see if it is the best record. If it is the best record, we use that otherwise we
          * return None
          *
          * @param goremalrec Option of a Goremal record
          * @param emaltype   emaltypes to filter results
          * @return Option of a PIDM. Returns PIDM if the Record Exists and it is the Best Record
          */
        def selectreturntype(goremalrec: Option[GOREMAL_R], emaltype: List[String]):
        Future[Option[Int]] = goremalrec match {

          case None => Future(None)
          case Some(s) =>

            val bestrec = bestrecfind(goremalrec, emaltype)

            bestrec.map( best =>
              if (best == goremalrec) {
                Some(s.pidm)
              }
              else {
                None
              }
            )
        }


        val emaltypes = List("CA", "CAS", "ECA", "ZCA", "ZCAS", "ZCH")
        val goremalrec = goremalrecmatch(googleIdentity, emaltypes)

        goremalrec.flatMap(value => selectreturntype(value, emaltypes))
      }


      /**
        * Simple Parser for Option of an Int Passed By GOREMALPIDMFIND. Converts Some to their value and Nones to 0
        * @param futurePidm Future(Option(Int)) Which will be the PIDM if valid and None if Not
        * @return Future of an Int. Will be PIDM if should be looked at otherwise will be 0
        */
      def futureUnwrap(futurePidm: Future[Option[Int]]): Future[Int] = {
        futurePidm.map{
          case None => 0
          case Some(x) => x
        }
      }


      /**
        * Take Pidm and Maps a New Future Option Whether or Not it Exists in GOBUMAP Already
        * @param futurePidm This is a Future Int that is the Pidm if valid, or 0 if it came from a none.
        * @return Refer to CheckIfNotExistsGOBUMAP for how this is done
        */
      def futureInttoOptBool(futurePidm: Future[Int]): Future[Option[Boolean]] = {

        /**
          * This Checks A Option of a Pidm and the GoogleIdentity Record against GOBUMAP To Return What The Appropriate
          * Action to Take Moving Forward IS
          *
          * @param googleIdentity A Google Identity
          * @param possiblePidm   An Option of a Pidm - What Was Found Through GOREMALPIDMFIND
          * @return This returns an Option of a Boolean. If No Pidm was found already, or the record already exists will
          *         return none. If the pidm exists in the table but the udc_id doesnt match then we update return
          *         some(false) and if it is not present at all we return some(true)
          */
        def CheckIfNotExistsGOBUMAP(googleIdentity: GoogleIdentity, possiblePidm: Option[Int]):
        Future[Option[Boolean]] = possiblePidm match {

          case None => Future(None)
          case Some(s) =>
            //      Check if the same record already exists
            val futureSameIdentExists = modules.gobumapDal.findByFilter(gobu =>
              gobu.pidm === s &&
                gobu.UDC_ID === googleIdentity.id
            ).map(_.isEmpty)

            futureSameIdentExists flatMap {
              case false => Future(None)
              case true =>

                val pidmgobumaprecs = modules.gobumapDal.findByFilter(gobu =>
                  gobu.pidm === s
                ).map(_.isEmpty)

                pidmgobumaprecs.map(f => Some(f))
            }
        }

        futurePidm.flatMap( opt =>
          CheckIfNotExistsGOBUMAP(googleIdentity, Some(opt))
        )
      }

      /**
        * The annoying coping mechanism of flatmap deficiencies. We do not flatten our option rather we convert it to a
        * tuple that is false if none and true is some.
        * @param futureOptBool The result of CheckIfNotExistsGOBUMAP
        * @return This returns a tuple of two booleans. The first value is true if processing should occur, otherwise
        *         it is false.
        */
      def futureOptBooleanUnwrapper(futureOptBool: Future[Option[Boolean]]): Future[(Boolean, Boolean)] = {
        futureOptBool.map {
          case None => (false, false)
          case Some(x) => (true, x)
        }
      }

      /**
        * This is a combinator of two futures that itself returns a combined future of the two.
        * @param futureBoolTuple Two Boolean Tuple the first is a true false on whether it was a Some or None returned
        *                        from CheckIfNotExists.
        * @param futurePidm This is just the future of the value of the pidm returned from future unwrap. It is 0 if
        *                   the Option is None(Referring to what is returned by GoremalPidmFind
        * @return A tuple that contains all of the values from the previous futures serving as a merging point of
        *         the future threads.
        */
      def FutureBoolTupletoFinalTuple(futureBoolTuple:Future[(Boolean, Boolean)], futurePidm: Future[Int]):
      Future[(Boolean, Boolean, Int)] = {
        for {
          pid <- futurePidm
          boolTuple <- futureBoolTuple
        } yield boolTuple match {
          case (false, x) => (false, x, 0)
          case (true, x) => (true, x, pid)
        }
      }

      /**
        * This produces the final integer output as a singular future of integer. The second case class is to deal with
        * the times that the pidm returns as None from GoremalPidmFind.
        *
        * @param futureTuple Tuple of Boolean, Boolean, Int. The first Bollean is whether it should be processed, the
        *                    second indicates where it should be updated or inserted.
        * @return Update and Insert both natively return Future of Int,(1 for update, PIDM for insert) Will return 0 for
        *         all records that nothing was done with.
        */
      def futureTupleToInt(futureTuple: Future[(Boolean, Boolean, Int)]): Future[Int] = {

        /**
          * Simple Insert Function Wrapper Creating A Record and Then Inserting It into GOBUMAP
          *
          * @param googleIdentity A google identity
          * @param pidm           A pidm -> It is required to do this operation so it is not an Option
          * @return Int The PIDM => Database IO As a Side Effect
          */
        def Insert(googleIdentity: GoogleIdentity, pidm: Int): Future[Int] = {
          val date = new java.util.Date()
          val timestamp = new Timestamp(date.getTime)

          val gobumapR = GOBUMAP_R(pidm, googleIdentity.id, timestamp, timestamp, "davenpcm")
          //      println("gobumapR", gobumapR)
          val future = modules.gobumapDal.insert(gobumapR)
          future
        }

        /**
          * Simple Update Function. Creates a TimeStamp Before Updating A GOBUMAP RECORD
          *
          * @param googleIdentity A google identity
          * @param pidm           A Pidm -> If it has gotten to this point it will exist
          * @return Int 0 If Failed => We Are Doing Database IO As a Side Effect
          */
        def Update(googleIdentity: GoogleIdentity, pidm: Int): Future[Int] = {
          val date = new java.util.Date()
          val timestamp = new Timestamp(date.getTime)

          val gobumapR = GOBUMAP_R(pidm, googleIdentity.id, timestamp, timestamp, "davenpcm")

          val future = modules.gobumapDal.update(gobumapR)

          future
        }

        futureTuple.flatMap{
          case (false, bool, pid) => Future(0)
          case (true, bool, 0) => Future(0)
          case (true, bool, pid) => bool match {
            case false => Update(googleIdentity, pid)
            case true => Insert(googleIdentity, pid)
          }
        }
      }

      val optPidm = GoremalPidmFind(googleIdentity)
      val pidm = futureUnwrap(optPidm)
      val optBool = futureInttoOptBool(pidm)
      val unwrappedOptBool = futureOptBooleanUnwrapper(optBool)
      val finalTuple = FutureBoolTupletoFinalTuple(unwrappedOptBool, pidm)
      val finalInt = futureTupleToInt(finalTuple)

      finalInt

    }

    googleStringer(googleIdentity).map(a => (googleIdentity.primaryEmail, a))
  }

  val idents = ReturnGoogleIdentities(fileName) // The Identities From The Spreadsheet
  val future = Future.sequence(idents.map(GOBUMAPInsertUpdate(_))) // The Actual Database Transactions Grouped

  val ints = Await.result(future, Duration(60, "seconds")) // Waiting for all futures to complete 60s timeout
  ints.filter(a => a._2 != 0 ).foreach(println(_)) // Print Out Any Values Updated Or Inserted

}