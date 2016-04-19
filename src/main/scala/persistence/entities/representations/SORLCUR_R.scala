package persistence.entities.representations

import java.sql.Timestamp

import persistence.entities.constructs.PidmEntity

/**
  * Created by davenpcm on 4/11/2016.
  */
case class SORLCUR_R(pidm: Int,
                     SORLCUR_SEQNO: Int,
                     SORLCUR_LMOD_CODE: String,
                     SORLCUR_TERM_CODE: String,
                     SORLCUR_KEY_SEQ_NO: Int,
                     SORLCUR_ROLL_IND: String,
                     SORLCUR_CACT_CODE: String,
                     SORLCUR_USER_ID: String,
                     SORLCUR_DATA_ORIGIN: String,
                     SORLCUR_ACTIVITY_DATE: Timestamp,
                     SORLCUR_LEVL_CODE: String,
                     SORLCUR_COLL_CODE: String,
                     SORLCUR_DEGC_CODE: String,
                     SORLCUR_TERM_CODE_CTLG: String
                     ) extends PidmEntity
