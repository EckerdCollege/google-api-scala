package persistence.entities.constructs

/**
  * Created by chris on 4/9/16.
  */
trait PidmEntity {
  def pidm: Int
  def isValid : Boolean = true
}
