package com.zcia.example.session.manager

import org.scalatest.{BeforeAndAfter, FlatSpec}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

/**
  * Suite that allows to perform a basic set of test to some {@link SessionRepository} implementation.
  *
  * @author jazumaquero
  * @since 28/02/17.
  */
abstract class SessionRepositoryTestSuite extends FlatSpec with BeforeAndAfter {

  /** Implementation of repository that is going to be tested. **/
  protected implicit val repository: SessionRepository

  /** Ensure that repository is clean before test */
  before {
    repository.clean()
  }

  behavior of "SessionRepository "
  it should "create some randon session id when create new session id" in {
    val generatedSessionIds = Array[String]()
    for (i <- 1 to 1000000) {
      val sessionId = repository.create()
      assert(!generatedSessionIds.contains(sessionId))
      generatedSessionIds ++ sessionId
    }
  }
  it should "return some empty data when get non previous stored session id " in {
    val sessionId = repository.create()
    val result = repository.read(sessionId) map { cachedData =>
      cachedData match {
        case Some(_) => assert(false)
        case None => assert(true)
      }
    } recover {
      case _ => assert(false)
    }
    Await.ready(result, Duration.Inf)
  }
  it should "return previous updated data when get previous stored session id" in {
    val sessionId = repository.create()
    val sessionData = Array("hi", "world", 2, true)
    val putResult = repository.update(sessionId, sessionData)
    Await.ready(putResult, Duration.Inf)
    val getResult = repository.read(sessionId) map { cachedData =>
      cachedData match {
        case Some(x) => assert(sessionData.equals(x))
        case None => assert(false)
      }
    } recover {
      case _ => assert(false)
    }
    Await.ready(getResult, Duration.Inf)
  }
  it should "return some empty data when data is present when delete some session id " in {
    val sessionId = repository.create()
    val sessionData = Array("hi", "world", 2, true)
    val putResult = repository.update(sessionId, sessionData)
    Await.ready(putResult, Duration.Inf)
    val getResult = repository.read(sessionId) map { cachedData =>
      cachedData match {
        case Some(x) => assert(sessionData.equals(x))
        case None => assert(false)
      }
    } recover {
      case _ => assert(false)
    }
    Await.ready(getResult, Duration.Inf)
    val delResult = repository.delete(sessionId)
    Await.ready(delResult, Duration.Inf)
    val result = repository.read(sessionId) map { cachedData =>
      cachedData match {
        case Some(_) => assert(false)
        case None => assert(true)
      }
    } recover {
      case _ => assert(false)
    }
    Await.ready(result, Duration.Inf)
  }
  it should "do nothing when session id is missing" in {
    val sessionId = repository.create()
    val delResult = repository.delete(sessionId)
    Await.ready(delResult, Duration.Inf)
    val result = repository.read(sessionId) map { cachedData =>
      cachedData match {
        case Some(_) => assert(false)
        case None => assert(true)
      }
    } recover {
      case _ => assert(false)
    }
    Await.ready(result, Duration.Inf)
  }
}