package persistence.entities.tables
import com.typesafe.slick.driver.oracle.OracleDriver.api._
import persistence.entities.representations.GroupMaster_R
/**
  * Created by davenpcm on 4/28/16.
  */
class GROUP_MASTER(tag: Tag) extends Table[GroupMaster_R](tag, "GROUP_MASTER"){
  def id = column[String]("ID", O.PrimaryKey)
  def autoIndicator = column[String]("AUTO_INDICATOR")
  def name = column[String]("NAME")
  def email = column[String]("EMAIL")
  def count = column[Long]("MEMBER_COUNT")
  def desc = column[Option[String]]("DESCRIPTION")
  def processIndicator = column[Option[String]]("PROCESS_INDICATOR")




  def * = (id, autoIndicator , name, email, count, desc, processIndicator) <>
    (GroupMaster_R.tupled, GroupMaster_R.unapply)
}
