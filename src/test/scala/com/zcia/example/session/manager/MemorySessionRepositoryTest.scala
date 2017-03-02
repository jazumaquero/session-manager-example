package com.zcia.example.session.manager

import scala.concurrent.duration.Duration

/**
  * Basic testing for {@link MemorySessionRepository}.
  *
  * @author jazumaquero
  * @since 28/02/17.
  */
class MemorySessionRepositoryTest extends SessionRepositoryTestSuite {

  /** Implementation of repository that is going to be tested. **/
  override protected implicit val repository: SessionRepository = new MemorySessionRepository()

  behavior of "MemorySessionRepository"
  it should "do not insert when max size is reach when maxSize is set to some long" in {
    val repository: SessionRepository = new MemorySessionRepository(maxSize = Some(10))
    // TODO
  }
  it should "remove data when ttl is set to some duration" in {
    val repository: SessionRepository = new MemorySessionRepository(ttl = Some(Duration(1, "second")))
    // TODO
  }
}
