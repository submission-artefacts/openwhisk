package org.apache.openwhisk.predictor

// keep everything that comes from tcp client here

import akka.actor.{Actor, Props}
import akka.util.ByteString
import org.apache.openwhisk.common.AkkaLogging
import spray.json.{JsObject, JsonParser}
import spray.json.DefaultJsonProtocol._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Listener {
    def props(logger: AkkaLogging) =
        Props(new Listener(logger))

    private var nextConfigs = JsObject()

    def getNextConfigId(uuid: String): Future[String] = Future {
        nextConfigs.fields.get(uuid) match {
            case Some(config) =>
                nextConfigs = JsObject(nextConfigs.fields.filterNot { f => f._1 == uuid })
                config.convertTo[String]
            case None => null
        }
    }
}

class Listener(logger: AkkaLogging) extends Actor {
    def receive = { //  Receiving message
        case msg: String =>
            logger.info(this, s"TCP server message String: ${msg}")
            val jsonMsg = JsonParser(msg).asJsObject()

            import Listener._
            jsonMsg.fields.get("type") match {
                case Some(responseType) =>
                    if (responseType.convertTo[String] == "data") {
                        jsonMsg.fields.get("uuid") match {
                            case Some(uuid) =>
                                jsonMsg.fields.get("config_id") match {
                                    case Some(config) =>
                                        nextConfigs = JsObject(nextConfigs.fields + (uuid.convertTo[String] -> config))
                                    case None => logger.error(this, "Fatal Error: config_id not found in response")
                                }
                            case None => logger.error(this, "Fatal Error: uuid not found in response")
                        }
                    }
                    else {
                        logger.warn(this, "Fatal Error: type of response not recognized")
                    }
                case None => logger.error(this, "Fatal Error: type not found")
            }

        case byteMsg: ByteString => logger.info(this, s"TCP server message ByteString: ${byteMsg.utf8String}")
        case _ => logger.warn(this, s"Unknown message received from TCP server: not String or ByteString") // Default case
    }

}

