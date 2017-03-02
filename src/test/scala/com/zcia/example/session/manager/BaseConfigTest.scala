package com.zcia.example.session.manager

import org.scalatest.FlatSpec

import scala.concurrent.duration.Duration

/**
  * Simple test of configuration loading using application.conf
  *
  * @author jazumaquero
  * @since 02/03/2017.
  */
class BaseConfigTest extends FlatSpec with CacheBaseConfig with ServerBaseConfig {

  behavior of "CacheBaseConfig "
  it should "load cache type" in {
    assert(cacheType.equals("memory"))
  }
  it should "load ttl cache duration" in {
    assert(ttl.equals(Some(Duration(600, "seconds"))))
  }
  it should "load cache max size" in {
    assert(maxSize.equals(Some(1000)))
  }

  behavior of "ServerBaseConfig "
  it should "load  bind interface for server" in {
    assert(host.equals("0.0.0.0"))
  }
  it should "load listen port for server" in {
    assert(port == 9000)
  }
  it should "load cookie name" in {
    assert(cookie.equals("foo.bar.nav"))
  }
}
