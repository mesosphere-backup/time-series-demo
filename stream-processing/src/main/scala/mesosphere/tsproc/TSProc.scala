package mesosphere.tsproc

import java.util.Properties

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka._

import org.streum.configrity._

import dispatch._
import Defaults._

object TSProc {

  lazy val logger = org.slf4j.LoggerFactory.getLogger(getClass.getName)

  def processTimeSeries(brokers: String, consumerGroup: String, topics: String): Unit = {

    val conf = new SparkConf().setAppName("Time series DCOS demo")
    // setting up the Spark Streaming context with a 10s window
    val ssc = new StreamingContext(conf, Seconds(10))
    val numThreads = 1

    val topicMap = topics.split(",").map((_, numThreads.toInt)).toMap
    val kafkaStream = KafkaUtils.createStream(ssc, brokers, consumerGroup, topicMap)

    logger.info(s"Kafka consumer connected to $brokers and listening to topics: $topics")

    kafkaStream.foreachRDD(rdd => {
      val msgCount = rdd.count()

      logger.info("before request")

      val input = """10278872,HY467389,10/18/2015 11:50:00 PM,060XX S VERNON AVE,0560,ASSAULT,SIMPLE,RESIDENCE PORCH/HALLWAY,false,false,0313,003,20,42,08A,1180307,1865162,2015,10/25/2015 03:55:25 PM,41.785268355,-87.614452924,asdf"""

      val arr = input.split(",")
      val caseNumber = arr(1)
      val crimeType = arr(5)
      val lat = arr(arr.length-3)
      val lon = arr(arr.length-2)
      val data = s"crimedata0,case-no=${caseNumber},type=${crimeType},lat=${lat},lon=${lon} v=1"
      logger.info(s"data: ${data}")

      val req = url("http://influxdb.marathon.mesos:22372/write?db=tsdemo").POST << data
      val future = Http(req OK as.String)
      val resp = future()
      logger.info(s"response: ${resp}")

      logger.info(s"In the past 10 seconds I've seen $msgCount messages")
    })

    // kick off stream processing
    ssc.start()
    ssc.awaitTermination()
  }

  def main(args: Array[String]) {
     if (args.length < 3) {
        System.err.println("Usage:   \n  mesosphere.tsproc.TSProc $ZK_QUORUM $CONSUMER_GROUP $TOPIC_LIST ")
        System.err.println("ZK_QUORUM       ... 10.0.6.90,10.0.6.91 ")
        System.err.println("$CONSUMER_GROUP ... aconsumergoup ")
        System.err.println("$TOPIC_LIST     ... topic1,topic2,topic3")
        System.exit(1)
    }
    // setting up configuration:
    val brokers = args(0)
    val consumerGroup = args(1)
    val topics = args(2)

    processTimeSeries(brokers, consumerGroup, topics)
    System.exit(0)
  }
}
