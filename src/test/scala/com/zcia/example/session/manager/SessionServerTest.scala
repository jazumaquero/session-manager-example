package com.zcia.example.session.manager

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.headers.{Cookie, HttpCookie, `Set-Cookie`}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.mockito.Mockito._
import org.mockito.Matchers._
import org.scalatest.mockito.MockitoSugar
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
  val mockedSessionData: Array[Byte] = "tar".getBytes
}

/**
  * Simple test for {@link SessionServer}
  *
  * @author jazumaquero
  * @since 01/03/2017.
  */
class SessionServerTest extends FlatSpec with Matchers with BeforeAndAfter with MockitoSugar with ScalatestRouteTest with SessionServer {

  import SessionServerTest._

  /** Session repository used to deal with session data. **/
  override protected implicit val repository: SessionRepository = mock[SessionRepository]

  /** Name of the cookie is going to be used to store the session id. **/
  protected implicit val cookieName: String = "myCookie"

  before {
    when(repository.create()).thenReturn(mockedSessionId)
    when(repository.read[Array[Byte]](mockedSessionId)).thenReturn(Future(Some(mockedSessionData)))
    when(repository.read[Array[Byte]](missingSessionId)).thenReturn(Future(None))
    when(repository.update[Array[Byte]](anyString, any[Array[Byte]])).thenReturn(Future())
    when(repository.delete(anyString)).thenReturn(Future())
  }

  behavior of "SessionServer instance"
  it should "return ok including proper session id when cookie is not present" in {
    Post() ~> sessionRoutes ~> check {
      header[`Set-Cookie`] shouldEqual Some(`Set-Cookie`(HttpCookie(cookieName, value = mockedSessionId)))
      status === Created
    }
  }
  it should "return ok including proper session id when cookie is present" in {
    Post() ~> addHeader(Cookie(cookieName, mockedSessionId)) ~> sessionRoutes ~> check {
      header[`Set-Cookie`] shouldEqual Some(`Set-Cookie`(HttpCookie(cookieName, value = mockedSessionId)))
      status === Created
    }
  }
  it should "return ok including proper session data when session cookie is present" in {
    Get() ~> addHeader(Cookie(cookieName, mockedSessionId)) ~> sessionRoutes ~> check {
      header[`Set-Cookie`] shouldEqual Some(`Set-Cookie`(HttpCookie(cookieName, value = mockedSessionId)))
      status === OK
      //verify(repository.read(mockedSessionId))
      //responseAs[Array[Byte]] should be(mockedSessionData)
    }
  }
  it should "return ok including none data when session cookie id is missing" in {
    Get() ~> addHeader(Cookie(cookieName, missingSessionId)) ~> sessionRoutes ~> check {
      header[`Set-Cookie`] shouldEqual Some(`Set-Cookie`(HttpCookie(cookieName, value = mockedSessionId)))
      status === OK
      //verify(repository.read(missingSessionId))
    }
  }
  it should "return ok when update session data and when session cookie id is missing" in {
    Put() ~> addHeader(Cookie(cookieName, mockedSessionId)) ~> sessionRoutes ~> check {
      header[`Set-Cookie`] shouldEqual Some(`Set-Cookie`(HttpCookie(cookieName, value = mockedSessionId)))
      status === OK
      //verify(repository.update(anyString, any))
    }
  }
  it should "return ok when delete session data when session cookie id is missing" in {
    Delete() ~> addHeader(Cookie(cookieName, missingSessionId)) ~> sessionRoutes ~> check {
      header[`Set-Cookie`] shouldEqual Some(`Set-Cookie`(HttpCookie(cookieName, value = mockedSessionId)))
      status === OK
      //verify(repository.delete(anyString))
    }
  }
}