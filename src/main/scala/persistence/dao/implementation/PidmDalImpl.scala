package persistence.dao.implementation

import slick.driver.JdbcProfile
import slick.lifted.{CanBeQueryCondition, TableQuery}
import persistence.dao.api.PidmDal
import persistence.entities.constructs.{PidmEntity, PidmTable}
import utils.persistence.{DbModule, Profile}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by chris on 4/9/16.
  */
class PidmDalImpl[T <: PidmTable[A], A <: PidmEntity
](tableQ: TableQuery[T])(
  implicit val db: JdbcProfile#Backend#Database,
  implicit val profile: JdbcProfile
) extends PidmDal[T,A] with Profile with DbModule {

  import profile.api._

  override def insert(row: A): Future[Int] = {
    insert(Seq(row)).map(_.head)
  }
  override def insert(rows: Seq[A]): Future[Seq[Int]] = {
    db.run(tableQ returning tableQ.map(_.pidm) ++= rows.filter(_.isValid))
  }

  override def update(row: A): Future[Int] = {
    if (row.isValid)
      db.run(tableQ.filter(_.pidm === row.pidm).update(row))
    else
      Future {
        0
      }
  }

  override def update(rows: Seq[A]): Future[Unit] = {
    db.run(DBIO.seq(rows.filter(_.isValid).map(r => tableQ.filter(_.pidm === r.pidm).update(r)): _*))
  }

  override def findByPidm(pidm: Int): Future[Option[A]] = {
    db.run(tableQ.filter(_.pidm === pidm).result.headOption)
  }

  override def findByFilter[C: CanBeQueryCondition](f: (T) => C): Future[Seq[A]] = {
    db.run(tableQ.withFilter(f).result)
  }

  override def findAll: Future[Seq[A]] = {
    db.run(tableQ.result)
  }

  override def deleteByPidm(pidm: Int): Future[Int] = {
    deleteByPidm(Seq(pidm))
  }

  override def deleteByPidm(pidms: Seq[Int]): Future[Int] = {
    db.run(tableQ.filter(_.pidm.inSet(pidms)).delete)
  }

  override def deleteByFilter[C: CanBeQueryCondition](f: (T) => C): Future[Int] = {
    db.run(tableQ.withFilter(f).delete)
  }

  override def createTable(): Future[Unit] = {
    db.run(DBIO.seq(tableQ.schema.create))
  }

  override def dropTable(): Future[Unit] = {
    db.run(DBIO.seq(tableQ.schema.drop))
  }
}
