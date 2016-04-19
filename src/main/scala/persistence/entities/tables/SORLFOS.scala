package persistence.entities.tables

import persistence.entities.constructs.PidmTable
import com.typesafe.slick.driver.oracle.OracleDriver.api._
import java.sql.Timestamp

import persistence.entities.representations.SORLFOS_R

/**
  * Created by davenpcm on 4/11/2016.
  */
class SORLFOS (tag: Tag) extends PidmTable[SORLFOS_R](tag, "SORLFOS")("SORLFOS_PIDM") {
  // Sorlfos pidm is pidm
  def LCUR_SEQNO = column[Int]("SORLFOS_LCUR_SEQNO")
  def SEQNO= column[Int]("SORLFOS_SEQNO")
  def LFST_CODE = column[String]("SORLFOS_LFST_CODE")
  def TERM_CODE = column[String]("SORLFOS_TERM_CODE")
  def PRIORITY_NO = column[Int]("SORLFOS_PRIORITY_NO")
  def CSTS_CODE = column[String]("SORLFOS_CSTS_CODE")
  def CACT_CODE = column[String]("SORLFOS_CACT_CODE")
  def DATA_ORIGIN = column[String]("SORLFOS_DATA_ORIGIN")
  def USER_ID = column[String]("SORLFOS_USER_ID")
  def ACTIVITY_DATE = column[Timestamp]("SORLFOS_ACTIVITY_DATE")
  def MAJR_CODE = column[String]("SORLFOS_MAJR_CODE")
  def TERM_CODE_CTLG = column[String]("SORLFOS_TERM_CODE_CTLG")

  def * = (
    pidm,
    LCUR_SEQNO,
    SEQNO,
    LFST_CODE,
    TERM_CODE,
    PRIORITY_NO,
    CSTS_CODE,
    CACT_CODE,
    DATA_ORIGIN,
    USER_ID,
    ACTIVITY_DATE,
    MAJR_CODE,
    TERM_CODE_CTLG
    ) <> (SORLFOS_R.tupled, SORLFOS_R.unapply)
}
