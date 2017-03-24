package ru.makkarpov.scamp.actor

import ru.makkarpov.scamp.ProtocolState

/**
 * Creation date: 24.03.2017
 * Copyright (c) harati
 */
object ProtocolSwitch {

}

trait onSendSwitch {
  def onSend(actor: ScampActor): Option[ProtocolState]
}

trait onReceiveSwitch {
  def onReceive(actor: ScampActor): Option[ProtocolState]
}
