package utils.persistence

import persistence.dao.api.PidmDal
import persistence.entities.representations._
import persistence.entities.tables._

/**
  * Created by chris on 4/9/16.
  * This is where api for all persistence goes
  */
trait PersistenceModule {
  val spridenDal: PidmDal[SPRIDEN, SPRIDEN_R]
  val sorlcurDal: PidmDal[SORLCUR, SORLCUR_R]
  val sorlfosDal: PidmDal[SORLFOS, SORLFOS_R]
  val gobtpacDal: PidmDal[GOBTPAC, GOBTPAC_R]
  val gobumapDal: PidmDal[GOBUMAP, GOBUMAP_R]
  val goremalDal: PidmDal[GOREMAL, GOREMAL_R]
}
