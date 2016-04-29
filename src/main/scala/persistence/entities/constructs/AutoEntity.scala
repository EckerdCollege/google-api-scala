package persistence.entities.constructs

/**
  * Created by davenpcm on 4/28/16.
  */
trait AutoEntity {
  def autoIndicator: String
  def processIndicator: Option[String]
}
