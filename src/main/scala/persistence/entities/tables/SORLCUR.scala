package persistence.entities.tables

import persistence.entities.constructs.PidmTable
import persistence.entities.representations.SORLCUR_R
import com.typesafe.slick.driver.oracle.OracleDriver.api._
import java.sql.Timestamp

/**
  * Created by davenpcm on 4/11/2016.
  */
class SORLCUR(tag: Tag) extends PidmTable[SORLCUR_R](tag, "SORLCUR")("SORLCUR_PIDM") {
  // pidm returns SORLCUR_PIDM column
  def SORLCUR_SEQNO= column[Int]("SORLCUR_SEQNO")
  def SORLCUR_LMOD_CODE= column[String]("SORLCUR_LMOD_CODE")
  def SORLCUR_TERM_CODE= column[String]("SORLCUR_TERM_CODE")
  def SORLCUR_KEY_SEQ_NO= column[Int]("SORLCUR_KEY_SEQNO")
  def SORLCUR_ROLL_IND= column[String]("SORLCUR_ROLL_IND")
  def SORLCUR_CACT_CODE = column[String]("SORLCUR_CACT_CODE")
  def SORLCUR_USER_ID= column[String]("SORLCUR_USER_ID")
  def SORLCUR_DATA_ORIGIN= column[String]("SORLCUR_DATA_ORIGIN")
  def SORLCUR_ACTIVITY_DATE= column[Timestamp]("SORLCUR_ACTIVITY_DATE")
  def SORLCUR_LEVL_CODE= column[String]("SORLCUR_LEVL_CODE")
  def SORLCUR_COLL_CODE= column[String]("SORLCUR_COLL_CODE")
  def SORLCUR_DEGC_CODE= column[String]("SORLCUR_DEGC_CODE")
  def SORLCUR_TERM_CODE_CTLG= column[String]("SORLCUR_TERM_CODE_CTLG")

  def * = (
    pidm,
    SORLCUR_SEQNO,
    SORLCUR_LMOD_CODE,
    SORLCUR_TERM_CODE,
    SORLCUR_KEY_SEQ_NO,
    SORLCUR_ROLL_IND,
    SORLCUR_CACT_CODE,
    SORLCUR_USER_ID,
    SORLCUR_DATA_ORIGIN,
    SORLCUR_ACTIVITY_DATE,
    SORLCUR_LEVL_CODE,
    SORLCUR_COLL_CODE,
    SORLCUR_DEGC_CODE,
    SORLCUR_TERM_CODE_CTLG
    ) <> (SORLCUR_R.tupled, SORLCUR_R.unapply )
}


