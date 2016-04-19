package persistence.entities.tables
import com.typesafe.slick.driver.oracle.OracleDriver.api._
import persistence.entities.constructs.PidmTable
import persistence.entities.representations.GOBTPAC_R
/**
  * Created by davenpcm on 4/11/2016.
  */
class GOBTPAC (tag: Tag) extends PidmTable[GOBTPAC_R](tag, "GOBTPAC")("GOBTPAC_PIDM") {
  def EXTERNAL_USER = column[Option[String]]("GOBTPAC_EXTERNAL_USER")

  def * = (
    pidm,
    EXTERNAL_USER
    ) <> (GOBTPAC_R.tupled, GOBTPAC_R.unapply)
}
