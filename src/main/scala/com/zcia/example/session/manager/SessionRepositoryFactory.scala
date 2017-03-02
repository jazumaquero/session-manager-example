package com.zcia.example.session.manager

import scala.concurrent.duration.Duration


/**
  * Simple factory that allows to select different implementations of {@link SessionRepository} depending on configuration.
  *
  * @author jazumaquero
  * @since 02/03/2017.
  */
object SessionRepositoryFactory extends CacheBaseConfig {
  /**
    * Returns some new instance of the requested session repository (according to given cache implementation).
    * Currently supported types:
    * <li>memory</li>
    *
    * @param cacheType is the requested implementation
    * @param ttl       is the max time to live of the cached data, or none for infinite cached time.
    * @param maxSize   is teh max cache size, or none for unlimited cache.
    * @return
    * @throws {@link IllegalArgumentException} when not supported type is requested.
    */
  def build(cacheType: String, ttl: Option[Duration] = None, maxSize: Option[Long] = None): SessionRepository = cacheType match {
    case "memory" => new MemorySessionRepository(maxSize, ttl)
    case _ => throw new IllegalArgumentException(s"Not supported requested cache type: $cacheType.")
  }
}
