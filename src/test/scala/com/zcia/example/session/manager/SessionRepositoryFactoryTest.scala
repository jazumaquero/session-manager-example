package com.zcia.example.session.manager

import org.scalatest.FlatSpec

/**
  * Simple testing of {@link SessionRepository} factory.
  *
  * @author jazumaquero
  * @since 02/03/2017.
  */
class SessionRepositoryFactoryTest extends FlatSpec {
  behavior of "SessionRepository "
  it should "return MemorySessionRepository when cache type is 'memory' " in {
    assert(SessionRepositoryFactory.build("memory").isInstanceOf[MemorySessionRepository])
  }
  it should "throw IllegalArgumentException when cache type is not supported" in {
    assertThrows[IllegalArgumentException](SessionRepositoryFactory.build("foo"))
  }
}
