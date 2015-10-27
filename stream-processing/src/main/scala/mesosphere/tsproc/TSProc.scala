package mesosphere.tsproc

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka._

import org.streum.configrity._

object TSProc {

  lazy val logger = org.slf4j.LoggerFactory.getLogger(getClass.getName)
  
  def processTimeSeries(): Unit = {

    val conf = new SparkConf().setAppName("Time series DCOS demo")
    // setting up the Spark Streaming context with a 10s window
    val ssc = new StreamingContext(conf, Seconds(10))

    val zkQuorum = "zoo01,zoo02,zoo03"
    val consumerGroup = "agroup"
    val topics = "topic1,topic2"
    val numThreads = 1

    val topicMap = topics.split(",").map((_, numThreads.toInt)).toMap
    val kafkaStream = KafkaUtils.createStream(ssc, zkQuorum, consumerGroup, topicMap)
    
    logger.info(s"Kafka consumer set up listening on topics: $topics")
    
    kafkaStream.foreachRDD(rdd => {
      val msgCount = rdd.count()
      
      logger.info(s"In the past 10 seconds I've seen $msgCount messages")
    })
    
    // kick off stream processing
    ssc.start()
    ssc.awaitTermination()
  }

  def main(args: Array[String]) {
    processTimeSeries()
    System.exit(0)
  }
}