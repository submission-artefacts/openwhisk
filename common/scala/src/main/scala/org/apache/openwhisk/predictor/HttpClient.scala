package org.apache.openwhisk.predictor;

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding.Post
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import org.apache.openwhisk.common.AkkaLogging
import spray.json.DefaultJsonProtocol._
import spray.json.JsObject

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

class HttpClient(logger: AkkaLogging) {

    //    version - 10.2.3
    //    implicit val system = ActorSystem(Behaviors.empty, "SingleRequest")
    //    implicit val executionContext = system.executionContext

    //    version - 10.1.11
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher
    val apiUri = "http://10.10.1.1:5000/"


    def postRecord(data: String): Unit = {
        val postRequest = Post(uri = apiUri + "node", data)
        Http(system).singleRequest(postRequest)

        //        responseFuture
        //            .onComplete {
        //                case Success(res) =>
        //                    import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
        //                    import org.apache.openwhisk.predictor.Entity.ConfigJsonProtocol._
        //                    val futureConfig = Unmarshal(res).to[Config]
        //                    val result = Await.result(futureConfig, 10.seconds)
        //                    println(s"GOT VALUABLE RESULT ${result}")
        //                    system.terminate()
        //                case Failure(_) => sys.error("something wrong")
        //            }
    }


    def queryConfig(data: String): String = {
        val postRequest = Post(uri = apiUri + "query", data)
        val responseFuture: Future[HttpResponse] = Http(system).singleRequest(postRequest)

        val response = Await.result(responseFuture, 10.seconds)

        import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
        import org.apache.openwhisk.predictor.Entity.ConfigJsonProtocol._
        val futureConfig = Unmarshal(response).to[JsObject]
        getConfigIdFromJsObject(Await.result(futureConfig, 10.seconds))
        //        responseFuture
        //            .onComplete {
        //                case Success(res) =>
        //                    import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
        //                    import org.apache.openwhisk.predictor.Entity.ConfigJsonProtocol._
        //                    val futureConfig = Unmarshal(res).to[JsObject]
        //                    getConfigIdFromJsObject(Await.result(futureConfig, 10.seconds))
        //
        //                case Failure(_) =>
        //                    logger.error(this, "Failure during waiting for response from HTTP client")
        //                    null
        //            }
        //        null
    }

    def getConfigIdFromJsObject(jsonMsg: JsObject): String = {
        logger.info(this, s"GOT RESPONSE ${jsonMsg}")
        jsonMsg.fields.get("config_id") match {
            case Some(config) =>
                config.convertTo[String]
            case None => logger.error(this, "Fatal Error: config_id not found in response")
                null
        }
    }

}
