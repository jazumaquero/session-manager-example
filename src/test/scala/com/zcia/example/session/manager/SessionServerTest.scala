package com.zcia.example.session.manager

import akka.http.scaladsl.model.headers.{Cookie, HttpCookie, `Set-Cookie`}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

import scala.concurrent.Future

/**
  * Object that includes all required literals are going to be used to test {@link SessionServer}
  *
  * @author jazumaquero
  * @since 01/03/2017.
  */
object SessionServerTest {
  val mockedSessionId: String = "foo"
  val missingSessionId: String = "bar"
  val mockedSessionData: Array[Byte]=Array()
}

/**
  * Simple test for {@link SessionServer}
  *
  * @author jazumaquero
  * @since 01/03/2017.
  */
class SessionServerTest extends FlatSpec with Matchers with BeforeAndAfter with MockFactory with ScalatestRouteTest with SessionServer {

  import SessionServerTest._

  /** Session repository used to deal with session data. **/
  override protected implicit val repository: SessionRepository = mock[SessionRepository]

  /** Name of the cookie is going to be used to store the session id. **/
  protected implicit val cookieName: String = "myCookie"

  before {
    (repository.create _).expects().returning(mockedSessionId).anyNumberOfTimes()
    //(repository.read[Array[Byte]] _).expects(mockedSessionId).returning(Future(Some(mockedSessionData))).anyNumberOfTimes()
    //(repository.read[Array[Byte]] _).expects(missingSessionId).returning(Future(None)).anyNumberOfTimes()
  }

  behavior of "SessionServer instance"
  it should "return ok including proper session id when cookie is not present" in {
    Post() ~> sessionRoutes ~> check {
      header[`Set-Cookie`] shouldEqual Some(`Set-Cookie`(HttpCookie(cookieName, value = mockedSessionId)))
    }
  }/*
  it should "return ok including proper session id when cookie is present" in {
    Post() ~> addHeader(Cookie(cookieName,mockedSessionId)) ~> sessionRoutes ~> check {
      header[`Set-Cookie`] shouldEqual Some(`Set-Cookie`(HttpCookie(cookieName, value = mockedSessionId)))
    }
  }*/
  it should "return ok including proper session data when session cookie is present" in {
    Get() ~> addHeader(Cookie(cookieName,mockedSessionId)) ~> sessionRoutes ~> check {
      header[`Set-Cookie`] shouldEqual Some(`Set-Cookie`(HttpCookie(cookieName, value = mockedSessionId)))
      responseAs[Array[Byte]] should be (mockedSessionData)
    }
  }
}
