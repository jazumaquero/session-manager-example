package com.zcia.example.session.manager

import akka.http.scaladsl.server.{HttpApp, Route}

/**
  * Main server class.
  *
  * @author jazumaquero
  * @since 27/02/17.
  */
object Main extends HttpApp with App with SessionServer with ServerBaseConfig with CacheBaseConfig{

  override protected implicit val repository: SessionRepository = SessionRepositoryFactory.build(cacheType, ttl,maxSize)

  override protected implicit val cookieName: String = cookie

  override protected def route: Route = path("session") {
    sessionRoutes
  }

  Main.startServer(host=host,port=port)
}
