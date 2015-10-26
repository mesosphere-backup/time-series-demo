package spark.tsproc

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka._

import org.streum.configrity._

object TSProc {

  def processTimeSeries(sparkMasterURL: String): Unit = {

    // setting up the Spark configuration
    val conf = new SparkConf().setAppName("Time series DCOS demo").setMaster(sparkMasterURL)
    // setting up the Spark Streaming context with a 10s window
    val ssc = new StreamingContext(conf, Seconds(10))

    val zkQuorum = "zoo01,zoo02,zoo03"
    val consumerGroup = "agroup"
    val topics = "topic1,topic2"
    val numThreads = 1

    val topicMap = topics.split(",").map((_, numThreads.toInt)).toMap
    val kafkaStream = KafkaUtils.createStream(ssc, zkQuorum, consumerGroup, topicMap)
    
    kafkaStream.foreachRDD(rdd => {
      val msgCount = rdd.count()
      
      // overall stats:
      print("\n\nIn the past 10 seconds " + "I've seen " + msgCount + " messages. "
      )
    })
    
    // kick off the ongoing stream processing:
    ssc.start()
    ssc.awaitTermination()
  }

  def main(args: Array[String]) {
    if (args.length < 1) {
      System.err.println("Usage:   \n  TSProc $MASTER_URL $ENABLE_DEBUG")
      System.err.println("Note that the $ENABLE_DEBUG defaults to `false`.\n\n")
      System.err.println("Example: \n  TSProc mesos://127.0.1.1:5050 true")
      System.exit(1)
    }
    // setting up configuration:
    val sparkMasterURLArg = args(0)
    val enableDebugArg = args(1)

    if (!enableDebugArg.isEmpty && enableDebugArg == "true") {
        System.out.println("Launching in DEBUG mode ...")
    }
    else {
        TSProcHelper.setLogLevel() // only show ERROR log info
    }
    
    processTimeSeries(sparkMasterURLArg)
    System.exit(0)
  }
}