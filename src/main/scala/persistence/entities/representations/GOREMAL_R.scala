package persistence.entities.representations

import persistence.entities.constructs.PidmEntity

/**
  * Created by davenpcm on 4/15/2016.
  */
case class GOREMAL_R(
                    pidm: Int,
                    emal_code: String,
                    email_address: String
                    ) extends PidmEntity
