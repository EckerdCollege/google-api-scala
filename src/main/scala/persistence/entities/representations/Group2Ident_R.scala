package persistence.entities.representations

/**
  * Created by davenpcm on 4/28/16.
  */
case class Group2Ident_R(
                          groupId: String,
                          identID: String,
                          autoIndicator: String,
                          memberRole: String,
                          memberType: String,
                          processIndicator: Option[String] = None
                        )
