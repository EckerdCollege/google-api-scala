package persistence.entities.representations

import java.sql.Timestamp
/**
  * Created by davenpcm on 5/9/16.
  */
case class GWBALIAS_R(
                     typePkCk: String,
                     keyPk: String,
                     alias: String,
                     termCode: String,
                     createGroupCk: String,
                     createDate: Timestamp,
                     activityDate: Timestamp,
                     userId: String
                     )
