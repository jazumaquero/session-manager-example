package com.zcia.example.session.manager

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
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
    val maxSize = 10
    val repository: SessionRepository = new MemorySessionRepository(maxSize = Some(maxSize))
    for (i <- 1 to maxSize) {
      Await.ready(repository.update(s"$i", s"foo$i"), Duration.Inf)
    }
    val excededKey = s"${maxSize + 1}"
    val excededValue = s"foo${maxSize + 1}"
    Await.ready(repository.update(excededKey, excededValue), Duration.Inf)
    Await.ready(repository.read[String](excededKey) map { data => assert(data.isEmpty) }, Duration.Inf)
  }
  it should "remove data when ttl is set to some duration" in {
    val timeout = Duration(1, "second")
    val key = "foo"
    val value = "bar"
    val repository: SessionRepository = new MemorySessionRepository(ttl = Some(timeout))
    // Force store result
    Await.ready(repository.update(key, value), Duration.Inf)
    // Read in time must be ok
    Await.ready(repository.read[String](key) map { _ => assert(true) }, Duration.Inf)
    // Sleep at least timeout
    Thread.sleep(timeout.toMillis)
    Await.ready(repository.read[String](key) map { data => assert(!value.equals(data.get)) }, Duration.Inf)
  }
}