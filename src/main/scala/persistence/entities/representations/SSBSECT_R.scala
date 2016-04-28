package persistence.entities.representations

/**
  * Created by davenpcm on 4/28/16.
  */
case class SSBSECT_R(
                    termCode: String,
                    crn: String,
                    crseNumber: String,
                    seqNumber: String,
                    enrolled: Int,
                    title: Option[String]
                    )
