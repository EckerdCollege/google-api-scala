package persistence.entities.representations

import java.sql.Timestamp

import persistence.entities.constructs.PidmEntity

/**
  * Created by chris on 4/9/16.
  */
case class SPRIDEN_R(
                  pidm: Int,
                  id: String,
                  firstName: String,
                  lastName: String,
                  mInitial: Option[String],
                  changeIndicator: Option[String],
                  activityDate: Timestamp
                  ) extends PidmEntity
