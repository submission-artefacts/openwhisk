package org.apache.openwhisk.predictor
// keep everything that goes to tcp client here
import akka.actor.ActorSystem
import org.apache.openwhisk.common.AkkaLogging
//import org.apache.openwhisk.predictor.Entity.JobConfigJsonProtocol.JobConfigJsonFormat
import org.apache.openwhisk.predictor.Entity._
import spray.json.{JsNumber, JsObject, JsString, JsonParser} //, enrichAny

class PredictorReactive {
  val actorSystem: ActorSystem = ActorSystem("predictor-actor-system")
  val logger: AkkaLogging = new AkkaLogging(akka.event.Logging.getLogger(actorSystem, this))
//  val listener: ActorRef = actorSystem.actorOf(Listener.props(logger), "tcpListener")
//  val tcpClient: ActorRef = actorSystem.actorOf(TCPClient.props("127.0.0.1", 8888, listener), "tcpActor")
  val httpClient = new HttpClient(logger)
  var jobConfigs = JsObject()

//  val workerConfFile = scala.io.Source.fromFile("../../common/scala/src/main/resources/worker_conf.json")
//  val workerConfLines = try workerConfFile.mkString finally workerConfFile.close()
  val workerConfLines = """{
                          |    "01": {
                          |        "cpus": 0.5,
                          |        "memory": 256
                          |    },
                          |    "02": {
                          |        "cpus": 0.5,
                          |        "memory": 512
                          |    },
                          |    "03": {
                          |        "cpus": 0.5,
                          |        "memory": 1024
                          |    },
                          |    "04": {
                          |        "cpus": 0.5,
                          |        "memory": 2048
                          |    },
                          |    "05": {
                          |        "cpus": 0.5,
                          |        "memory": 4096
                          |    },
                          |    "06": {
                          |        "cpus": 0.5,
                          |        "memory": 8192
                          |    },
                          |    "07": {
                          |        "cpus": 0.5,
                          |        "memory": 16384
                          |    },
                          |    "08": {
                          |        "cpus": 1,
                          |        "memory": 256
                          |    },
                          |    "09": {
                          |        "cpus": 1,
                          |        "memory": 512
                          |    },
                          |    "10": {
                          |        "cpus": 1,
                          |        "memory": 1024
                          |    },
                          |    "11": {
                          |        "cpus": 1,
                          |        "memory": 2048
                          |    },
                          |    "12": {
                          |        "cpus": 1,
                          |        "memory": 4096
                          |    },
                          |    "13": {
                          |        "cpus": 1,
                          |        "memory": 8192
                          |    },
                          |    "14": {
                          |        "cpus": 1,
                          |        "memory": 16384
                          |    },
                          |    "15": {
                          |        "cpus": 2,
                          |        "memory": 256
                          |    },
                          |    "16": {
                          |        "cpus": 2,
                          |        "memory": 512
                          |    },
                          |    "17": {
                          |        "cpus": 2,
                          |        "memory": 1024
                          |    },
                          |    "18": {
                          |        "cpus": 2,
                          |        "memory": 2048
                          |    },
                          |    "19": {
                          |        "cpus": 2,
                          |        "memory": 4096
                          |    },
                          |    "20": {
                          |        "cpus": 2,
                          |        "memory": 8192
                          |    },
                          |    "21": {
                          |        "cpus": 2,
                          |        "memory": 16384
                          |    },
                          |    "22": {
                          |        "cpus": 4,
                          |        "memory": 256
                          |    },
                          |    "23": {
                          |        "cpus": 4,
                          |        "memory": 512
                          |    },
                          |    "24": {
                          |        "cpus": 4,
                          |        "memory": 1024
                          |    },
                          |    "25": {
                          |        "cpus": 4,
                          |        "memory": 2048
                          |    },
                          |    "26": {
                          |        "cpus": 4,
                          |        "memory": 4096
                          |    },
                          |    "27": {
                          |        "cpus": 4,
                          |        "memory": 8192
                          |    },
                          |    "28": {
                          |        "cpus": 4,
                          |        "memory": 16384
                          |    }
                          |}""".stripMargin
  val workerConfigs = JsonParser(workerConfLines).asJsObject()

//  val jobIdFile = scala.io.Source.fromFile("../../common/scala/src/main/resources/job_id.json")
//  val jobIdLines = try jobIdFile.mkString finally jobIdFile.close()
//  val jobIds = JsonParser(jobIdLines).asJsObject()
//  import java.nio.file.Paths
//  println(Paths.get(".").toAbsolutePath)

//  def getCurrentConfig(keyName: String): JobConfig ={
//    jobConfigs.fields.get(keyName) match {
//      case Some(value) =>
//        import JobConfigJsonProtocol._
//        val jobConfig = value.convertTo[JobConfig]
//        jobConfig
//      case None =>
//        logger.error(this,s"No current config found for Container: ${keyName}")
//        null
//    }
//  }

//  def getJobId(keyName: String): Int ={
//    import spray.json.DefaultJsonProtocol._
//    jobIds.fields.get(keyName) match {
//      case Some(jobId) =>
//        jobId.convertTo[Int]
//      case None =>
//        logger.error(this, s"Fatal Error: JobId not found for job: ${keyName}")
//        -1
//    }
//  }

//  def updateJobConfigs(jobName: String): Unit={
//    jobConfigs.fields.get(jobName) match {
//      case Some(value) =>
//        import JobConfigJsonProtocol._
//        val jobConfig = value.convertTo[JobConfig]
//        jobConfig.invocation += 1
//        jobConfigs = JsObject(jobConfigs.fields + (jobName -> jobConfig.toJson))
//      case None =>
//       logger.error(this,s"updateJobConfigs: No current config found for job: ${jobName}")
////        val jobConfig = new JobConfig(1,jobName, 22)
////        jobConfigs = JsObject(jobConfigs.fields + (jobName -> jobConfig.toJson))
//    }
//  }

//  def updateCurrentConfig(jobName: String, configId: Int): Unit = {
//    logger.info(this, s"Updating CurrentConfig for Container: ${jobName} to ${configId}")
//    jobConfigs.fields.get(jobName) match {
//      case Some(value) =>
//        import JobConfigJsonProtocol._
//        val jobConfig = value.convertTo[JobConfig]
//        jobConfig.configId = configId
//        jobConfigs = JsObject(jobConfigs.fields + (jobName -> jobConfig.toJson))
//      case None =>
//        // logger.error(this,s"No current config found for job: ${jobName}")
//        val jobConfig = new JobConfig(1,jobName, configId)
//        jobConfigs = JsObject(jobConfigs.fields + (jobName -> jobConfig.toJson))
//    }
//  }

  def recordPerformance(jobName: String, runtime: String, parameters: JsObject, currentConfig: Config): Unit ={
    logger.info(this, s"Received Runtime: ${runtime} for job: ${jobName}")

//    val jobId = getJobId(jobName);
//    updateJobConfigs(jobName)
//    val currentConfig = getCurrentConfig(jobName)
    val perfObject = JsObject(
      "type" -> JsString("node"),
      "job_name" -> JsString(jobName),
      "config_id" -> JsNumber(currentConfig.configId),
      "runtime" -> JsString(runtime),
      "parameters" -> parameters)
//    reportToSVD(perfObject)
    httpClient.postRecord(perfObject.toString)
  }

  def queryForConfig(jobName: String, parameters: JsObject): Config ={
    logger.info(this, s"Querying Next Configuration fon Job: ${jobName}")
//    val currentConfig = getCurrentConfig(jobName)
//    val uuid = java.util.UUID.randomUUID.toString
    val queryObject = JsObject(
      "type" -> JsString("query"),
      "job_name" -> JsString(jobName),
//      "invocation" -> JsNumber(currentConfig.invocation),
      "parameters" -> parameters)
//    reportToSVD(queryObject)
//    val configId = Await.result(Listener.getNextConfigId(uuid), 10 seconds)
    val configId = httpClient.queryConfig(queryObject.toString)
    logger.warn(this, s"GOT CONFIG CODE: ${configId.toInt}")
//    updateCurrentConfig(jobName, configId.toInt)
    import ConfigJsonProtocol._
    workerConfigs.fields.get(configId) match {
      case Some(conf) =>
        val config_obj = JsObject(conf.asJsObject.fields + ("configId" -> JsNumber(configId)))
        config_obj.convertTo[Config]
      case None =>
        logger.error(this, s"Fatal Error: config not found for code: ${configId}")
        null
    }
  }

//  def reportToSVD(data: JsObject): Unit ={
//    tcpClient ! ByteString(data.toString)
//  }
}
