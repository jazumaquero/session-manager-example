package com.zcia.example.session.manager

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.headers.HttpCookie
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._

import scala.util.Failure

/**
  * CRUD interface used to manage all options from some session repository.
  *
  * @author jazumaquero
  * @since 27/02/17.
  */
trait SessionServer {

  /** Session repository used to deal with session data. **/
  protected implicit val repository: SessionRepository

  /** Name of the cookie is going to be used to store the session id. **/
  protected implicit val cookieName: String

  def sessionRoutes : Route = optionalCookie(cookieName) {
    case Some(sid) =>
      get {
        onComplete(repository.read[Array[Byte]](sid.value)) { data =>
          complete(OK, data)
        }
      } ~ put {
        entity(as[Array[Byte]]){ data =>
          onComplete(repository.update(sid.value,data)) {
            case Failure(ex) => complete(InternalServerError, s"Cannot update session ${sid.value} with $data due to :${ex.getMessage}")
            case _ => complete(OK)
          }
        }
      } ~ delete {
        onComplete(repository.delete(sid.value)) {
          case Failure(ex) => complete(InternalServerError, s"Cannot delete session ${sid.value} due to :${ex.getMessage}")
          case _ => complete(OK)
        }
      }
    case None =>
      post {
        val sessionId = repository.create()
        setCookie(HttpCookie(cookieName, value = sessionId)){
          complete(Created)
        }
      }
  }
}
