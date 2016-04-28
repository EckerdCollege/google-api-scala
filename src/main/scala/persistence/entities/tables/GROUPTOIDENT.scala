package persistence.entities.tables
import com.typesafe.slick.driver.oracle.OracleDriver.api._
import persistence.entities.representations.Group2Ident_R
/**
  * Created by davenpcm on 4/28/16.
  */
class GROUPTOIDENT(tag: Tag) extends Table[Group2Ident_R](tag, "GROUP_TO_IDENT") {
  def groupId = column[String]("GROUP_ID")
  def identID = column[String]("IDENT_ID")
  def autoIndicator = column[String]("AUTO_INDICATOR")
  def memberRole = column[String]("MEMBER_ROLE")
  def memberType = column[String]("MEMBER_TYPE")
  def processIndicator = column[Option[String]]("PROCESS_INDICATOR")

  def * = (groupId, identID, autoIndicator, memberRole, memberType, processIndicator) <>
    (Group2Ident_R.tupled, Group2Ident_R.unapply )
}
