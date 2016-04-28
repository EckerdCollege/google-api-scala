package persistence.entities.tables
import com.typesafe.slick.driver.oracle.OracleDriver.api._
import persistence.entities.representations.SSBSECT_R
/**
  * Created by davenpcm on 4/28/16.
  */
class SSBSECT(tag: Tag)  extends Table[SSBSECT_R](tag, "SSBSECT"){
  def termCode = column[String]("SSBSECT_TERM_CODE")
  def crn = column[String]("SSBSECT_CRN")
  def crseNumber = column[String]("SSBSECT_CRSE_NUMB")
  def seqNumber = column[String]("SSBSECT_SEQ_NUMB")
  def enrolled = column[Int]("SSBSECT_ENRL")
  def title = column[Option[String]]("SSBSECT_CRSE_TITLE")

  def * = (termCode, crn, crseNumber, seqNumber, enrolled, title) <>
    (SSBSECT_R.tupled, SSBSECT_R.unapply)
}
