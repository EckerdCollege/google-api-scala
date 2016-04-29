package scripts.GroupManagement

import utils.configuration.ConfigurationModuleImpl
import utils.persistence.PersistenceModuleImpl

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by davenpcm on 4/29/16.
  */
object UpdateFromDB extends App{
  val modules = new ConfigurationModuleImpl with PersistenceModuleImpl
  implicit val db = modules.db
  import modules.dbConfig.driver.api._

  val data = sql"""SELECT
                    --     SSBSECT_CRN as CRN,
                    --     SSBSECT_SEQ_NUMB as SEQNO,
                    --     SSBSECT_SUBJ_CODE as SUBJECT,
                    --     SSBSECT_CRSE_NUMB as COURSE_NUMBER,
                    --     substr(SSBSECT_SEQ_NUMB, -1),
                    --     decode(substr(SFRSTCR_TERM_CODE, -2, 1), 1, 'fa', 2, 'sp', 3, 'su') as TERM_ALIAS,
                        lower(SSBSECT_SUBJ_CODE) || SSBSECT_CRSE_NUMB || '-' || substr(SSBSECT_SEQ_NUMB, -1) || '-' || decode(substr(SFRSTCR_TERM_CODE, -2, 1), 1, 'fa', 2, 'sp', 3, 'su') as alias,
                        nvl(SSBSECT_CRSE_TITLE, x.SCBCRSE_TITLE) as COURSE_TITLE,
                        student.USERNAME as STUDENT_ACCOUNT,
                        professor.USERNAME as PROFESSOR_ACCOUNT
                    FROM
                    SFRSTCR
                    INNER JOIN
                        SSBSECT
                            INNER JOIN SCBCRSE x
                                ON SCBCRSE_CRSE_NUMB = SSBSECT_CRSE_NUMB
                                AND SCBCRSE_SUBJ_CODE = SSBSECT_SUBJ_CODE
                            ON SSBSECT_TERM_CODE = SFRSTCR_TERM_CODE
                            AND SSBSECT_CRN = SFRSTCR_CRN
                            AND SSBSECT_ENRL > 0
                    LEFT JOIN SIRASGN
                            ON SIRASGN_TERM_CODE = SFRSTCR_TERM_CODE
                            AND SIRASGN_CRN = SFRSTCR_CRN
                            AND SIRASGN_PRIMARY_IND = 'Y'
                            AND SIRASGN_PIDM is not NULL
                    INNER JOIN STVRSTS
                            ON STVRSTS_CODE = SFRSTCR_RSTS_CODE
                            AND STVRSTS_INCL_ASSESS = 'Y'
                    INNER JOIN IDENT_MASTER student
                        ON SFRSTCR_PIDM = student.PIDM
                    INNER JOIN IDENT_MASTER professor
                        ON SIRASGN_PIDM = professor.PIDM
                    INNER JOIN GTVSDAX
                        ON SFRSTCR_TERM_CODE = GTVSDAX_EXTERNAL_CODE
                        AND GTVSDAX_INTERNAL_CODE_GROUP in ('ALIAS_UP', 'ALIAS_UP_XCRS', 'ALIAS_UR')
                    WHERE
                    (SELECT MAX(SCBCRSE_EFF_TERM)
                        FROM SCBCRSE y
                        WHERE y.SCBCRSE_CRSE_NUMB = x.SCBCRSE_CRSE_NUMB
                        AND y.SCBCRSE_SUBJ_CODE = x.SCBCRSE_SUBJ_CODE
                    ) = x.SCBCRSE_EFF_TERM
                    ORDER BY alias asc
    """.as[(String, String,String, String)]

  val result = Await.result(db.run(data), Duration.Inf)
  result.foreach(println(_))
}
