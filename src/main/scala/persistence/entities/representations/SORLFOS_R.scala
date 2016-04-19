package persistence.entities.representations

import java.sql.Timestamp

import persistence.entities.constructs.PidmEntity

/**
  * Created by davenpcm on 4/11/2016.
  */
case class SORLFOS_R(
                    pidm: Int,
                    lcur_seqno: Int,
                    seqno: Int,
                    lfst_code: String,
                    term_code: String,
                    priority_no: Int,
                    csts_code: String,
                    cact_code: String,
                    data_origin: String,
                    user_id: String,
                    activity_date: Timestamp,
                    majr_code: String,
                    term_code_ctlg: String
                    ) extends PidmEntity
