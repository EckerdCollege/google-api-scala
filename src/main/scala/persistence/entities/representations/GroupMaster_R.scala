package persistence.entities.representations

/**
  * Created by davenpcm on 4/28/16.
  */
case class GroupMaster_R(id: String,
                         autoIndicator: String,
                         name: String,
                         email: String,
                         count: Long,
                         desc: Option[String]
                        )
