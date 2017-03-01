package com.zcia.example.session.manager

import com.google.common.cache.CacheBuilder

import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scalacache._
import scalacache.guava._


/**
  * Simple implementation of {@link SessionRepository},
  * just by using {@link ScalaCache} with {@link Guava} in memory cache.
  *
  * @param maxSize
  * @param ttl
  * @author jazumaquero
  * @since 27/02/17.
  * @todo implement strategy that allow using multiple implementations: Redis, Ehcache, etc.
  */
class MemorySessionRepository(maxSize: Option[Long]=None, ttl: Option[Duration]=None) extends SessionRepository {

  /** Undeliying implementation of the cache. **/
  protected val underlyingCache = if (maxSize.isEmpty) {
    CacheBuilder.newBuilder().build[String, Object]
  } else {
    CacheBuilder.newBuilder().maximumSize(maxSize.get).build[String, Object]
  }

  /** {@link ScalaCache} that will be used to store data. **/
  protected implicit val scalaCache = ScalaCache(GuavaCache(underlyingCache))

  /**
    * Read session data for the given session id.
    *
    * @param id
    * @return
    */
  override def read[T](id: String): Future[Option[T]] = get(id)

  /**
    * Update the whole session data with a new one given some session id.
    *
    * @param id
    * @param value
    * @return
    */
  override def update[T](id: String, value: T): Future[Unit] = put(id)(value, ttl)

  /**
    * Delete all data related to some given session id.
    *
    * @param id
    * @return
    */
  override def delete(id: String): Future[Unit] = remove(id)

  /**
    * Remove all data from cache. Mainly used for testing purpose.
    *
    * @return
    */
  override def clean(): Future[Unit] = removeAll

}