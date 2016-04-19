package persistence.entities.tables

import persistence.entities.constructs.PidmTable
import com.typesafe.slick.driver.oracle.OracleDriver.api._
import persistence.entities.representations.GOREMAL_R

/**
  * Created by davenpcm on 4/15/2016.
  */
class GOREMAL(tag: Tag) extends PidmTable[GOREMAL_R](tag, "GOREMAL")("GOREMAL_PIDM") {
  def emal_code = column[String]("GOREMAL_EMAL_CODE")
  def email = column[String]("GOREMAL_EMAIL_ADDRESS")
  def * = (pidm, emal_code, email) <> (GOREMAL_R.tupled, GOREMAL_R.unapply)
}
