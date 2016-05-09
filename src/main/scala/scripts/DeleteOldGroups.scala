package scripts

import google.services.admin.directory.Directory
import persistence.entities.tables.GWBALIAS
import persistence.entities.representations.GWBALIAS_R
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

import language.postfixOps
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.breakOut
import google.services.admin.directory.models._


import scala.concurrent.Future
import scala.util.{Failure, Try}

/**
  * Created by davenpcm on 5/9/16.
  */
object DeleteOldGroups {

  def deleteTermCourses(term: String,
                        dbConfig: DatabaseConfig[JdbcProfile],
                        directory: Directory
                       ) = {
    import dbConfig.driver.api._

    def genericRemoveGoogle(optionsOfGroups: Seq[Option[Group]])(f: Group => Try[Group])
    : Seq[(Try[Group], Option[Group] )] = {
      val result = optionsOfGroups.map{
        case Some(group) => Thread.sleep(333) ; f(group)
        case None => Failure(new Throwable("No Matching Group"))
      }.zip(optionsOfGroups)

      result
    }

    def productionRemoveGoogle(optionsOfGroups: Seq[Option[Group]]) ={
      genericRemoveGoogle(optionsOfGroups)(group => Try{ directory.groups.delete( group.id.get ); group })
    }

    def debugRemoveGoogle(optionsOfGroups: Seq[Option[Group]]) = {
      genericRemoveGoogle(optionsOfGroups)(group => directory.groups.get( group.id.get ))
    }

    def genericRemoveDB[T](records: Seq[GWBALIAS_R],
                        tableQuery: TableQuery[GWBALIAS],
                        db: JdbcProfile#Backend#Database)(f: (String, String) => Future[T])(g: T => T): Seq[T] = {
      val seqFutures = records.map(record => f(record.typePkCk, record.keyPk))
      val FutureSeq = Future.sequence(seqFutures)
      val Result = Await.result(FutureSeq, Duration.Inf)
      Result.map(g)
    }

    def productionRemoveDB(records: Seq[GWBALIAS_R],
                           tableQuery: TableQuery[GWBALIAS],
                           db: JdbcProfile#Backend#Database): Seq[Int] = {

      def deleteFromGwbaliasByPrimaryKey(typePkCk: String, keyPk: String): Future[Int] = {
        val q = tableQuery.filter(rec => rec.typePkCk === typePkCk && rec.keyPk === keyPk)
        val action = q.delete
        val affectedRowsCount = db.run(action)
        affectedRowsCount
      }

      genericRemoveDB(records, tableQuery, db)(deleteFromGwbaliasByPrimaryKey)(T => T)
    }

    def debugRemoveDB(records: Seq[GWBALIAS_R], tableQuery: TableQuery[GWBALIAS], db: JdbcProfile#Backend#Database): Seq[String] = {

      def deleteStatementsFromGwbaliasByPrimaryKey(typePkCk: String, keyPk: String): Future[String] = {
        val q = tableQuery.filter(rec => rec.typePkCk === typePkCk && rec.keyPk === keyPk)
        val action = q.delete
        Future(action.statements.head)
      }

      genericRemoveDB(records, tableQuery, db)(deleteStatementsFromGwbaliasByPrimaryKey)(rec => {println(rec); rec})

    }

    def generic[O](f: Seq[Option[Group]] => Seq[(Try[Group], Option[Group] )])
                  (g: (Seq[GWBALIAS_R], TableQuery[GWBALIAS], JdbcProfile#Backend#Database) => Iterable[O]) = {
      val db = dbConfig.db

      val googleGroups = directory.groups.list()
      val googleGroupsMap = googleGroups.map(group => group.email.toUpperCase -> group)(collection.breakOut): Map[String, Group]

      val oldGroupTable = TableQuery[GWBALIAS]
      val query = oldGroupTable.withFilter(_.termCode === term)
      val queryResults = Await.result(db.run(query.result), Duration.Inf)
      val queryResultsMap = queryResults.map(rec =>
        rec.alias.toUpperCase + "@ECKERD.EDU" -> rec)(collection.breakOut): Map[String, GWBALIAS_R]
      val theGroups= queryResultsMap.keys.map( key =>
        googleGroupsMap.get(key)
      ).toSeq

      val attempt = f(theGroups)

      val FailureSeq = attempt.filter( tryAttemptTuple => tryAttemptTuple._1 isFailure  )
      val SuccessSeq = attempt.filter( tryAttemptTuple => tryAttemptTuple._1 isFailure )

      val toRemoveFromTable = SuccessSeq.map(suc => suc._1.get)


      val successfulRecords = toRemoveFromTable.map( group => queryResultsMap.get(group.email.toUpperCase))
        .filter(_ isDefined)
        .map( _.get)

      g(successfulRecords, oldGroupTable, db)


      val queryResultsLength = queryResults.toList.length

      val FailLength = FailureSeq.toList.length
      val SuccessLength = SuccessSeq.toList.length

      println(s"Total: $queryResultsLength")
      println(s"Failures: $FailLength")
      println(s"Success's: $SuccessLength")

    }

    def debug() = {
      generic(debugRemoveGoogle)(debugRemoveDB)
    }

    def prod() = {
      generic(productionRemoveGoogle)(productionRemoveDB)
    }

    debug()

  }

}
