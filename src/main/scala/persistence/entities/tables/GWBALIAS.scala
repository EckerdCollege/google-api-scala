package persistence.entities.tables

import java.sql.Timestamp

import com.typesafe.slick.driver.oracle.OracleDriver.api._
import persistence.entities.representations.GWBALIAS_R
/**
  * Created by davenpcm on 5/9/16.
  */
class GWBALIAS(tag: Tag) extends Table[GWBALIAS_R](tag, "GWBALIAS") {
  def typePkCk = column[String]("GWBALIAS_TYPE_PK_CK")
  def keyPk = column[String]("GWBALIAS_KEY_PK")
  def alias = column[String]("GWBALIAS_ALIAS")
  def termCode = column[String]("GWBALIAS_TERM_CODE")
  def createGroupCk = column[String]("GWBALIAS_CREATE_GROUP_CK")
  def createDate =  column[Timestamp]("GWBALIAS_DATE_CREATED")
  def activityDate = column[Timestamp]("GWBALIAS_ACTIVITY_DATE")
  def userId= column[String]("GWBALIAS_USER_ID")

  def * = (
    typePkCk,
    keyPk,
    alias,
    termCode,
    createGroupCk,
    createDate,
    activityDate,
    userId) <> (GWBALIAS_R.tupled, GWBALIAS_R.unapply)

  def pk = primaryKey( "gwbalias_pk", (typePkCk, keyPk) )
}
