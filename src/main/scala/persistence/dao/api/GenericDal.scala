package persistence.dao.api

import slick.lifted.CanBeQueryCondition

import scala.concurrent.Future

/**
  * Created by davenpcm on 5/9/16.
  */
trait GenericDal[T,A] {
//  def insert(row: A): Future[Int]
//  def insert(rows: Seq[A]): Future[Seq[Int]]
  def insertOrUpdate(row: A): Future[Int]
  def insertOrUpdate(rows: Seq[A]): Future[Seq[Int]]
//  def update(row: A): Future[Int]
//  def update(rows: Seq[A]): Future[Unit]
//  def findByPidm(pidm: Int): Future[Option[A]]
  def findByFilter[C: CanBeQueryCondition](f: (T) => C): Future[Seq[A]]
  def findAll: Future[Seq[A]]
//  def deleteByPidm(pidm: Int): Future[Int]
//  def deleteByPidm(pidms: Seq[Int]): Future[Int]
//  def delete(row: A): Future[Int]
//  def delete(rows: Seq[A]): Future[Seq[Int]]
  def deleteByFilter[C: CanBeQueryCondition](f: (T) => C): Future[Int]
  def createTable(): Future[Unit]
  def dropTable(): Future[Unit]
}
