package persistence.entities.representations

import persistence.entities.constructs.AutoEntity

/**
  * Created by davenpcm on 4/28/16.
  */
case class GroupMaster_R(id: String,
                         autoIndicator: String,
                         name: String,
                         email: String,
                         count: Long,
                         desc: Option[String],
                         processIndicator: Option[String] = None
                        ) extends AutoEntity
