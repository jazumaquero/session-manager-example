package com.zcia.example.session.manager

import com.typesafe.config.{Config, ConfigException, ConfigFactory}

import scala.concurrent.duration.Duration

/**
  * Simple trait used to give access to different configuration settings
  *
  * @author jazumaquero
  * @since 28/02/17.
  */
trait BaseConfig {
  /** Root configuration settings.  **/
  protected val config: Config = ConfigFactory.load()
}

/**
  * Simple trait used to give access to cache configuration
  *
  * @author jazumaquero
  * @since 28/02/17.
  */
trait CacheBaseConfig extends BaseConfig {
  /** Cache settings. **/
  protected val cacheConfig: Config = config.getConfig("cache")

  /** Optional max time to live for any cache entry **/
  val ttl: Option[Duration] = try {
    Some(Duration(cacheConfig.getLong("ttl.duration"),cacheConfig.getString("ttl.unit")))
  } catch {
    case ex: ConfigException.Missing => None
  }

  /** Option max allowed cache size. **/
  val maxSize: Option[Long] = try {
    Some(cacheConfig.getLong("maxSize"))
  } catch {
    case ex: ConfigException.Missing => None
  }

  /** Indicates what implementation is going to be used. Currently supported: memory. **/
  val cacheType: String = cacheConfig.getString("type")
}

/**
  * Simple trait used to give access to the server configuration settings
  *
  * @author jazumaquero
  * @since 28/02/17.
  */
trait ServerBaseConfig extends BaseConfig {
  /** Server settings. **/
  protected val serverConfig: Config = config.getConfig("server")

  /** Network interface where server is going to bind to. **/
  val host: String = serverConfig.getString("host")

  /** Port where server is going to listen to. **/
  val port: Int = serverConfig.getInt("port")

  /** Cookie name is going to be used by the session manager service. **/
  val cookie: String = serverConfig.getString("cookie")
}

