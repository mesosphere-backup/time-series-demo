package mesosphere.tsproc

import java.util.Properties

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka._

import org.streum.configrity._

import scalaj.http.Http

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

      // influxDB http request
      //
      // curl -i -XPOST 'http://$PUBLIC_SLAVE_FQHN:22372/write?db=tsdemo' --data-binary 'crimedata0,case-no=HY467397,type=narcotics,lat=41.89,lon=-87.51 v=1'
      // influxdb.marathon.mesos:22372/write?db=tsdemo --data-binary

      rdd.collect().foreach(line => logger.info(line._2))

      //val result = Http("http://influxdb.marathon.mesos:22372/write?db=tsdemo").postData("""crimedata0,case-no=HY467397,type=narcotics,lat=41.89,lon=-87.51 v=1""")
        //.asString

      logger.info(s"In the past 10 seconds I've seen $msgCount messages")
    })

    // kick off stream processing
    ssc.start()
    ssc.awaitTermination()
  }

  def main(args: Array[String]) {
     if (args.length < 3) {
        System.err.println("Usage:   \n  mesosphere.tsproc.TSProc $BROKER_LIST $CONSUMER_GROUP $TOPIC_LIST ")
        System.err.println("$BROKER_LIST ... 10.0.6.90,10.0.6.91 ")
        System.err.println("$BROKER_LIST ... aconsumergoup ")
        System.err.println("$BROKER_LIST ... topic1,topic2,topic3")
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
