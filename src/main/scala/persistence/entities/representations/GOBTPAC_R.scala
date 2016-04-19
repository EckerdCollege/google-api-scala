package persistence.entities.representations

import persistence.entities.constructs.PidmEntity

/**
  * Created by davenpcm on 4/11/2016.
  */
case class GOBTPAC_R(
                    pidm: Int,
                    external_user: Option[String]
                    ) extends PidmEntity
