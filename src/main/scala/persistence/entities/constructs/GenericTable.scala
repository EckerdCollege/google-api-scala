package persistence.entities.constructs

import com.typesafe.slick.driver.oracle.OracleDriver.api._
import slick.model.PrimaryKey
/**
  * Created by davenpcm on 5/9/16.
  */
abstract class GenericTable[T](tag: Tag, name: String) extends Table[T] (tag, name) {

  def pk: PrimaryKey
}
