
import persistence.entities.representations.{GOBTPAC_R, SORLCUR_R, SORLFOS_R}
import persistence.entities.tables.SORLCUR
import utils.configuration.ConfigurationModuleImpl
import utils.persistence.PersistenceModuleImpl

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by chris on 4/9/16.
  */
object Main extends App {
  val modules = new ConfigurationModuleImpl with PersistenceModuleImpl
  import modules.dbConfig.driver.api._

  val spridenQuery = modules.spridenDal.findByFilter( x =>
    x.SPRIDEN_CHANGE_IND.isEmpty &&
      x.SPRIDEN_FIRST_NAME === "Christopher" &&
      x.SPRIDEN_LAST_NAME === "Davenport"
  )
  val spridenResult = Await.result(spridenQuery, Duration.Inf)

  val gobptacQuery = modules.gobtpacDal.findAll


//  val myPidm = spridenResult.head.pidm
//
//  val sorlcurQuery = modules.sorlcurDal.findByFilter( x =>
//    x.pidm === myPidm &&
//      x.SORLCUR_CACT_CODE === "ACTIVE"
//  ).map(seq =>
//    seq.sortWith(_.SORLCUR_SEQNO > _.SORLCUR_SEQNO)
//  ).map(sorted =>
//    sorted.takeWhile(sorted.head.SORLCUR_SEQNO == _.SORLCUR_SEQNO )
//  ).map(
//    _.head.SORLCUR_SEQNO
//  )
  val sorlcurQuery = modules.sorlcurDal.findAll


//  val sorlfosQuery = modules.sorlfosDal.findByFilter( x =>
//    x.LCUR_SEQNO === sorlcurResult &&
//    x.pidm === myPidm
//  )
  val sorlfosQuery = modules.sorlfosDal.findAll

  val gobtpacResult = Await.result(gobptacQuery, Duration.Inf)
  val sorlcurResult = Await.result(sorlcurQuery, Duration.Inf)
  val sorlfosResult = Await.result(sorlfosQuery, Duration.Inf)


  def getMajors(allGOBTPAC: Seq[GOBTPAC_R],
               allSORLCUR: Seq[SORLCUR_R],
               allSORLSOF: Seq[SORLFOS_R]): Seq[(Int, String)] = {

    def pidmToSeqNo(pidm: Int, allSORLCUR: Seq[SORLCUR_R]): Int = {
      allSORLCUR.filter(sor =>
        sor.pidm == pidm && sor.SORLCUR_CACT_CODE == "ACTIVE"
      ).sortWith(_.SORLCUR_SEQNO > _.SORLCUR_SEQNO).head.SORLCUR_SEQNO
    }

    def pidmAndSeqNoToRecords(pidm: Int, seqNo: Int, allSORLFOS: Seq[SORLFOS_R]): Seq[SORLFOS_R] = {
      allSORLFOS.filter(sor =>
        sor.pidm == pidm && sor.lcur_seqno == seqNo
      )
    }


  ???
  }



//  val largestSeqNo = sorlcurResult.head.SORLCUR_SEQNO
//  val sorlcurFinal = sorlcurResult.takeWhile(_.SORLCUR_SEQNO == largestSeqNo)

  println(spridenResult)
  println(sorlcurResult)
  println(sorlfosResult)

//  result.take(50).foreach(println(_))

}

