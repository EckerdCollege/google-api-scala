package persistence.entities.tables

import persistence.entities.constructs.PidmTable
import com.typesafe.slick.driver.oracle.OracleDriver.api._
import java.sql.Timestamp
import persistence.entities.representations.SPRIDEN_R

/**
  * Created by chris on 4/9/16.
  */
class SPRIDEN(tag: Tag) extends PidmTable[SPRIDEN_R](tag, "SPRIDEN")("SPRIDEN_PIDM") {
  // pidm returns SPRIDEN_PIDM column
  def SPRIDEN_ID = column[String]("SPRIDEN_ID")
  def SPRIDEN_FIRST_NAME = column[String]("SPRIDEN_FIRST_NAME")
  def SPRIDEN_LAST_NAME = column[String]("SPRIDEN_LAST_NAME")
  def SPRIDEN_MI = column[Option[String]]("SPRIDEN_MI")
  def SPRIDEN_CHANGE_IND = column[Option[String]]("SPRIDEN_CHANGE_IND", O.PrimaryKey)
  def SPRIDEN_ACTIVITY_DATE = column[Timestamp]("SPRIDEN_ACTIVITY_DATE")

  def * = (pidm, SPRIDEN_ID, SPRIDEN_FIRST_NAME,
    SPRIDEN_LAST_NAME, SPRIDEN_MI, SPRIDEN_CHANGE_IND, SPRIDEN_ACTIVITY_DATE) <> (SPRIDEN_R.tupled, SPRIDEN_R.unapply)
}
