package org.apache.openwhisk.predictor

import java.net.InetSocketAddress
import akka.actor.{Actor, ActorRef, Props}
import akka.io.{IO, Tcp}
import akka.pattern.ask
import akka.remote.transport.ActorTransportAdapter.AskTimeout
import akka.util.ByteString

object TCPClient {
    def props(host: String, port: Int, listener: ActorRef) =
        Props(classOf[TCPClient], new InetSocketAddress(host, port), listener)
}

class TCPClient(remote: InetSocketAddress, listener: ActorRef) extends Actor {
    import Tcp._
    import context.system

    val manager = IO(Tcp)
    manager ! Connect(remote)

    override def receive: Receive = {
        case CommandFailed(con: Connect) =>
            listener ! s"""{"type":"message", "message": "Connection failed", "Cause": "${con.failureMessage.toString}"}"""
            context stop self

        case c@Connected(remote, local) =>
            listener ! s"""{"type":"message", "message": "${c.toString}"}"""
            val connection = sender()
            connection ! Register(self)
            context.become {
                case data: ByteString =>
                    connection ? Write(data)
                case CommandFailed(w: Write) =>
                    // O/S buffer was full
                    listener ! s"""{"type":"message", "message": "write failed"}"""
                case Received(rdata) =>
                    listener ! rdata.utf8String
                case "close" =>
                    connection ! Close
                case _: ConnectionClosed =>
                    listener ! s"""{"type":"message", "message": "connection closed"}"""
                    context.stop(self)
            }
    }
}