package org.apache.openwhisk.predictor

import spray.json.{DefaultJsonProtocol, DeserializationException, JsNumber, JsObject, JsString, JsValue, RootJsonFormat}


object Entity {
    case class Config(configId: Int, cpus: Float, memory: Int)
    object ConfigJsonProtocol extends DefaultJsonProtocol {
        implicit val configFormat = jsonFormat3(Config)
    }

//    case class Job(jobName: String, jobId: Int)
//    object JobJsonProtocol extends DefaultJsonProtocol {
//        implicit val jobFormat = jsonFormat2(Job)
//    }

    class JobConfig(var invocation: Int, var jobName: String, var configId: Int)
    object JobConfigJsonProtocol extends DefaultJsonProtocol {
        implicit object JobConfigJsonFormat extends RootJsonFormat[JobConfig] {
            def write(obj: JobConfig) =
                JsObject("invocation" -> JsNumber(obj.invocation),
                         "jobName" -> JsString(obj.jobName),
                         "configId" -> JsNumber(obj.configId))

            def read(value: JsValue) = {
                value.asJsObject.getFields("invocation", "jobName", "configId") match {
                    case Seq(JsNumber(invocation), JsString(jobName), JsNumber(configId)) =>
                        new JobConfig(invocation.toInt, jobName.toString, configId.toInt)
                    case _ => throw new DeserializationException("InstanceConfig expected")
                }
            }
        }
    }
}
