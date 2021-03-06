package com.zcia.example.session.manager

import java.util.UUID

import scala.concurrent.Future

/**
  * Simple session repository trait.
  *
  * @author jazumaquero
  * @since 27/02/17.
  */
trait SessionRepository {

  /**
    * Generates some new session id.
    *
    * @return
    */
  def create(): String = UUID.randomUUID().toString

  /**
    * Read session data for the given session id.
    *
    * @param id
    * @tparam T
    * @return
    */
  def read[T](id: String): Future[Option[T]]

  /**
    * Update the whole session data with a new one given some session id.
    *
    * @param id
    * @param value
    * @tparam T
    * @return
    */
  def update[T](id: String, value: T): Future[Unit]

  /**
    * Delete all data related to some given session id.
    *
    * @param id
    * @return
    */
  def delete(id: String): Future[Unit]

  /**
    * Remove all data from cache. Mainly used for testing purpose.
    *
    * @return
    */
  def clean(): Future[Unit]
}
