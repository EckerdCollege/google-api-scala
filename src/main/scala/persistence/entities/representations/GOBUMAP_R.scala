package persistence.entities.representations
import java.sql.Timestamp

import persistence.entities.constructs.PidmEntity
/**
  * Created by davenpcm on 4/15/2016.
  */
case class GOBUMAP_R(
                    pidm: Int,
                    UDC_ID: String,
                    CreateDate: Timestamp,
                    ActivityDate: Timestamp,
                    UserId: String
                    ) extends PidmEntity
