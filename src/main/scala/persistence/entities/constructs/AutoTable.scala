package persistence.entities.constructs
import com.typesafe.slick.driver.oracle.OracleDriver.api._
/**
  * Created by davenpcm on 4/28/16.
  */
abstract class AutoTable[T](tag: Tag, name: String) extends Table[T] (tag, name) {
  def autoIndicator= column[String]("AUTO_INDICATOR")
  def processIndicator= column[Option[String]]("PROCESS_INDICATOR")
}
