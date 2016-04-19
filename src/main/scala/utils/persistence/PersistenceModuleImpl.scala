package utils.persistence

import persistence.dao.implementation.PidmDalImpl
import persistence.entities.representations._
import persistence.entities.tables._
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile
import slick.lifted.TableQuery
import utils.configuration.Configuration


/**
  * Created by chris on 4/9/16.
  */
trait PersistenceModuleImpl extends PersistenceModule with DbModule {
  this: Configuration =>

  //Incase We Want To Use Another Driver
  val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig("oracle")

  override implicit val profile: JdbcProfile = dbConfig.driver
  override implicit val db: JdbcProfile#Backend#Database = dbConfig.db

  override val spridenDal =
    new PidmDalImpl[SPRIDEN, SPRIDEN_R](TableQuery[SPRIDEN])

  override val sorlcurDal =
    new PidmDalImpl[SORLCUR, SORLCUR_R ](TableQuery[SORLCUR])

  override val sorlfosDal =
    new PidmDalImpl[SORLFOS, SORLFOS_R](TableQuery[SORLFOS])

  override val gobtpacDal =
    new PidmDalImpl[GOBTPAC, GOBTPAC_R](TableQuery[GOBTPAC])

  override val gobumapDal =
    new PidmDalImpl[GOBUMAP, GOBUMAP_R](TableQuery[GOBUMAP])

  override val goremalDal =
    new PidmDalImpl[GOREMAL, GOREMAL_R](TableQuery[GOREMAL])

  val self = this
}
