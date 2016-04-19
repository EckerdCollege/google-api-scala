package persistence.entities.constructs

import com.typesafe.slick.driver.oracle.OracleDriver.api._

/**
  * Created by chris on 4/9/16.
  */
abstract class PidmTable[T](tag: Tag, name: String)(pidmColumnName: String) extends Table[T] (tag, name) {
  def pidm = column[Int](pidmColumnName, O.PrimaryKey)
}
