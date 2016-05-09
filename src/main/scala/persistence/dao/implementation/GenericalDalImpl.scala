package persistence.dao.implementation

import persistence.dao.api.GenericDal
import persistence.entities.constructs.{GenericEntity, GenericTable}
import slick.driver.JdbcProfile
import slick.lifted.{CanBeQueryCondition, TableQuery}
import utils.persistence.{DbModule, Profile}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by davenpcm on 5/9/16.
  */
class GenericalDalImpl[T <: GenericTable[A], A <: GenericEntity](tableQ: TableQuery[T])(
  implicit val db: JdbcProfile#Backend#Database,
  implicit val profile: JdbcProfile
) extends GenericDal[T, A] with Profile with DbModule{
  import profile.api._

  val tableQuery = tableQ

//  override def insert(row: A): Future[Int] = {
//    insert(Seq(row)).map(_.head)
//  }
//
  override def insertOrUpdate(row: A): Future[Int] = {
    db.run(tableQ.insertOrUpdate(row))
  }

  override def insertOrUpdate(rows: Seq[A]): Future[Seq[Int]] = {
    val futures = rows.map(insertOrUpdate)
    Future.sequence(futures)
  }

//  override def insert(rows: Seq[A]): Future[Seq[Int]] = {
//    db.run(tableQ returning tableQ.map(_.pk) ++= rows.filter(_.isValid))
//  }
//
//  override def update(row: A): Future[Int] = {
//    if (row.isValid)
//      db.run(tableQ.filter(_.pk === row.pk).update(row))
//    else
//      Future {
//        0
//      }
//  }
//
//  override def update(rows: Seq[A]): Future[Unit] = {
//    db.run(DBIO.seq(rows.filter(_.isValid).map(r => tableQ.filter(_.pk === r.pk).update(r)): _*))
//  }

//  override def delete(row: A): Future[Int] = {
//    db.run()
//  }

  override def findByFilter[C: CanBeQueryCondition](f: (T) => C): Future[Seq[A]] = {
    db.run(tableQ.withFilter(f).result)
  }

  override def findAll: Future[Seq[A]] = {
    db.run(tableQ.result)
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
