package com.zcia.example.session.manager

import akka.persistence.PersistentActor

/**
  * Created by zuma on 27/02/17.
  */
class SessionActor extends PersistentActor{

  override def receiveRecover: Receive = ???

  override def receiveCommand: Receive = ???

  override def persistenceId: String = ???
}
