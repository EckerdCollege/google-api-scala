package persistence.entities.tables
import com.typesafe.slick.driver.oracle.OracleDriver.api._
import persistence.entities.constructs.AutoTable
import persistence.entities.representations.GroupMaster_R
import slick.ast.ColumnOption.PrimaryKey
/**
  * Created by davenpcm on 4/28/16.
  */
class GROUP_MASTER(tag: Tag) extends AutoTable[GroupMaster_R](tag, "GROUP_MASTER"){
  def id = column[String]("ID", O.PrimaryKey)
  def name = column[String]("NAME")
  def email = column[String]("EMAIL")
  def count = column[Long]("MEMBER_COUNT")
  def desc = column[Option[String]]("DESCRIPTION")
  def autoType = column[Option[String]]("AUTO_TYPE")
  def autoKey = column[Option[String]]("AUTO_KEY")
  def autoTermCode = column[Option[String]]("AUTO_TERM_CODE")

  def pk = index("GROUP_MASTER_PK", (id, autoType, autoKey), unique = true)

  def * = (id, autoIndicator , name, email, count, desc, processIndicator, autoType, autoKey, autoTermCode) <>
    (GroupMaster_R.tupled, GroupMaster_R.unapply)
}
