package com.zcia.example.session.manager

import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.MediaTypes._
import akka.http.scaladsl.model.headers.{Cookie, HttpCookie, `Set-Cookie`}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.mockito.Matchers._
import org.mockito.Mockito._
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
      // Check response content
      header[`Set-Cookie`] shouldEqual Some(`Set-Cookie`(HttpCookie(cookieName, value = mockedSessionId)))
      status === Created
      // Verify that repository calls where properly performed
      verify(repository).create()
    }
  }
  it should "return ok including proper session id when cookie is present" in {
    Post() ~> addHeader(Cookie(cookieName, mockedSessionId)) ~> sessionRoutes ~> check {
      // Check response content
      header[`Set-Cookie`] shouldEqual Some(`Set-Cookie`(HttpCookie(cookieName, value = mockedSessionId)))
      status === Created
      // Verify that repository calls where properly performed
      verify(repository).create()
    }
  }
  it should "return ok including proper session data when session cookie is present" in {
    Get() ~> addHeader(Cookie(cookieName, mockedSessionId)) ~> sessionRoutes ~> check {
      // Check response content
      header[`Set-Cookie`] shouldEqual Some(`Set-Cookie`(HttpCookie(cookieName, value = mockedSessionId)))
      status === OK
      responseAs[Array[Byte]] should be(mockedSessionData)
      // Verify that repository calls where properly performed
      verify(repository).read(mockedSessionId)
    }
  }
  it should "return ok including none or empty data when session cookie id is missing" in {
    Get() ~> addHeader(Cookie(cookieName, missingSessionId)) ~> sessionRoutes ~> check {
      // Check response content
      header[`Set-Cookie`] shouldEqual Some(`Set-Cookie`(HttpCookie(cookieName, value = missingSessionId)))
      status === OK
      responseAs[Array[Byte]] should be(Array())
      // Verify that repository calls where properly performed
      verify(repository).read(missingSessionId)
    }
  }
  it should "return ok when update session data and when session cookie id is present" in {
    Put("/",HttpEntity(`application/json`, mockedSessionData)) ~> addHeader(Cookie(cookieName, mockedSessionId)) ~> sessionRoutes ~> check {
      // Check response content
      header[`Set-Cookie`] shouldEqual Some(`Set-Cookie`(HttpCookie(cookieName, value = mockedSessionId)))
      status === OK
      // Verify that repository calls where properly performed
      verify(repository).update(mockedSessionId, mockedSessionData)
    }
  }
  it should "return ok when delete session data when session cookie id is present" in {
    Delete() ~> addHeader(Cookie(cookieName, mockedSessionId)) ~> sessionRoutes ~> check {
      // Check response content
      header[`Set-Cookie`] shouldEqual Some(`Set-Cookie`(HttpCookie(cookieName, value = mockedSessionId)))
      status === OK
      // Verify that repository calls where properly performed
      verify(repository).delete(mockedSessionId)
    }
  }
}