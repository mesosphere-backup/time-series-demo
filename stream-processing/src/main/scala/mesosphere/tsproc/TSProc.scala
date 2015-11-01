package mesosphere.tsproc

import java.util.Properties
import com.amazonaws.services.s3.model._

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka._
import java.io._
import scala.util._

import org.streum.configrity._

import dispatch._
import Defaults._
import scala.collection.mutable.{ListBuffer, StringBuilder}
//import com.lambdaworks.jacks.JacksMapper

import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.auth.BasicAWSCredentials

import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule

object JsonUtil {
  val mapper = new ObjectMapper() with ScalaObjectMapper
  mapper.registerModule(DefaultScalaModule)
  mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

  def toJson(value: Map[Symbol, Any]): String = {
    toJson(value map { case (k,v) => k.name -> v})
  }

  def toJson(value: Any): String = {
    mapper.writeValueAsString(value)
  }

  def toMap[V](json:String)(implicit m: Manifest[V]) = fromJson[Map[String,V]](json)

  def fromJson[T](json: String)(implicit m : Manifest[T]): T = {
    mapper.readValue[T](json)
  }
}

object TSProc {

  lazy val logger = org.slf4j.LoggerFactory.getLogger(getClass.getName)

  def processTimeSeries(brokers: String,
    consumerGroup: String,
    topics: String,
    influxDBPort : String,
    awsAccessKey: String,
    awsSecretKey: String): Unit = {

    val yourAWSCredentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey)
    val amazonS3Client = new AmazonS3Client(yourAWSCredentials)

    val conf = new SparkConf().setAppName("Crime time series data processing")
    // setting up the Spark Streaming context with a 10s window
    val ssc = new StreamingContext(conf, Seconds(10))
    val numThreads = 1

    val topicMap = topics.split(",").map((_, numThreads.toInt)).toMap
    val influxDBEndpoint = s"""http://influxdb.marathon.mesos:${influxDBPort}/write?db=tsdemo"""
    val kafkaStream = KafkaUtils.createStream(ssc, brokers, consumerGroup, topicMap)

    logger.info(s"Kafka consumer connected to $brokers and listening to topics: $topics")

    val offline_json = ListBuffer[Map[String, Any]]()

    kafkaStream.foreachRDD(rdd => {
      val msgCount = rdd.count()

      logger.info("before request")

      val inputs = rdd.collect()
      val datas = new ListBuffer[String]()
      for (input_pair <- inputs) {
        val input = input_pair._2
        val arr = input.split(",")
        val caseNumber = arr(1).replaceAll(" ", "")
        val crimeType = arr(5).replaceAll(" ", "")
        val lat = arr(arr.length-4).replaceAll(" ", "")
        val lon = arr(arr.length-3).replaceAll(" ", "")
        if (lat.nonEmpty && lon.nonEmpty) {
          val data = s"""crimedata0,case-no=${caseNumber},type=${crimeType},lat=${lat},lon=${lon} v=1"""
          datas += data
          logger.info(s"data: ${data}")

          val m = Map("lat" -> lat, "lng" -> lon, "count" -> "1")
          offline_json += m
        }
      }

      val datas_list = datas.toList

      val msg = datas_list.mkString("\n")
      //logger.info(s"msg: ${msg}")

      val req = url(influxDBEndpoint).POST << msg
      val future = Http(req OK as.String)
      val resp = future()

      logger.info("building list...")
      val offline_json_list = offline_json.toList

      logger.info("serializing data...")
      val sb = new StringBuilder("")
      sb ++= """{"max": 1, "data": ["""
      for (item <- offline_json_list) {
        val lat = item("lat")
        val lng = item("lng")
        sb ++= s"""{"lat": ${lat}, "lng": ${lng}, "count": 1},"""
        sb ++= "\n"
      }
      if (offline_json_list.length > 0) {
        val lat = offline_json_list(offline_json_list.length-1)("lat")
        val lng = offline_json_list(offline_json_list.length-1)("lng")
        sb ++= s"""{"lat": ${lat}, "lng": ${lng}, "count": 1}"""
      }
      sb ++= "]}"

      val oj_out = sb.toString
      //logger.info(s"oj_out: ${oj_out}")

      val pw = new PrintWriter(new File("/tmp/offline-crime-data.json" ))
      pw.write(oj_out)
      pw.close

      //val file = new File()

      logger.info("Uploading crime data...")

      val inputStream = new FileInputStream("/tmp/offline-crime-data.json")
      val metadata = new ObjectMetadata()
      metadata.setContentType("application/json")
      val request = new PutObjectRequest("mesosphere-tsdemo", "offline-crime-data.json", inputStream, metadata)
      request.setCannedAcl(CannedAccessControlList.PublicRead);
      amazonS3Client.putObject(request)

      logger.info(s"In the past 10 seconds I've seen $msgCount messages")
    })

    // kick off stream processing
    ssc.start()
    ssc.awaitTermination()
  }

  def main(args: Array[String]) {
     if (args.length < 6) {
        System.err.println("Usage:   \n  mesosphere.tsproc.TSProc $ZK_QUORUM $CONSUMER_GROUP $TOPIC_LIST $INFLUXDB_PORT $AWS_ACCESS_KEY $AWS_SECRET_KEY")
        System.err.println("ZK_QUORUM         ... 10.0.6.90,10.0.6.91 ")
        System.err.println("$CONSUMER_GROUP   ... aconsumergoup ")
        System.err.println("$TOPIC_LIST       ... topic1,topic2,topic3")
        System.err.println("$INFLUXDB_PORT    ... 98765")
        System.err.println("$AWS_ACCESS_KEY   ... ***")
        System.err.println("$AWS_SECRET_KEY   ... ***")
        System.exit(1)
    }
    // setting up configuration:
    val brokers = args(0)
    val consumerGroup = args(1)
    val topics = args(2)
    val influxDBPort = args(3)
    val awsAccessKey = args(4)
    val awsSecretKey = args(5)

    processTimeSeries(brokers, consumerGroup, topics, influxDBPort, awsAccessKey, awsSecretKey)
    System.exit(0)
  }
}
