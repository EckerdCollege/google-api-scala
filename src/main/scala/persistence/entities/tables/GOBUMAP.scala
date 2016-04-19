package persistence.entities.tables

import persistence.entities.constructs.PidmTable
import persistence.entities.representations.GOBUMAP_R
import com.typesafe.slick.driver.oracle.OracleDriver.api._
import java.sql.Timestamp

/**
  * Created by davenpcm on 4/15/2016.
  */
class GOBUMAP(tag: Tag) extends PidmTable[GOBUMAP_R](tag, "GOBUMAP")("GOBUMAP_PIDM") {
  def UDC_ID = column[String]("GOBUMAP_UDC_ID")
  def CreateDate = column[Timestamp]("GOBUMAP_CREATE_DATE")
  def ActivityDate = column[Timestamp]("GOBUMAP_ACTIVITY_DATE")
  def UserId = column[String]("GOBUMAP_USER_ID")

  def * = (pidm, UDC_ID, CreateDate, ActivityDate, UserId) <> (GOBUMAP_R.tupled, GOBUMAP_R.unapply)
}
