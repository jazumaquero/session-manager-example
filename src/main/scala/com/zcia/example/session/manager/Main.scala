package com.zcia.example.session.manager

import akka.http.scaladsl.server.{HttpApp, Route}

/**
  * Created by zuma on 27/02/17.
  */
object Main extends HttpApp {

  val path = "session"

  override protected def route: Route = getSession ~ putSession ~ postSession ~ deleteSession

  def getSession = path(path) {
    get{
      complete("TODO")
    }
  }

  def postSession=path(path) {
    post{
      complete("TODO")
    }
  }

  def putSession=path(path) {
    put{
      complete("TODO")
    }
  }

  def deleteSession=path(path) {
    delete{
      complete("TODO")
    }
  }

  Main.startServer(host="localhost",port=8080)
}
