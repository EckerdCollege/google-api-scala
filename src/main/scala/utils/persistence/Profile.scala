package utils.persistence

import slick.driver.JdbcProfile

/**
  * Created by chris on 4/9/16.
  */
trait Profile {
  val profile: JdbcProfile
}
