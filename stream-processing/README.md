# Stream Processing

## Kafka

Install and set up:

     $ dcos package install kafka
     $ dcos kafka broker add 1
     broker added:
      id: 1
      active: false
      state: stopped
      resources: cpus:1.00, mem:2048, heap:1024, port:auto
      failover: delay:1m, max-delay:10m
      stickiness: period:10m

## Spark Data Ingestions

Install and set up:

    $ dcos package install spark

See the docs on [Kafka-Spark integration]http://spark.apache.org/docs/latest/streaming-kafka-integration.html and [this example](https://github.com/apache/spark/blob/master/examples/src/main/scala/org/apache/spark/examples/streaming/KafkaWordCount.scala).

Launch the Spark job manually like so:

    dcos spark run --submit-args=’--class TSProc https://dl.dropboxusercontent.com/u/10436738/tmp/tsdemo-1.0-SNAPSHOT-jar-with-dependencies.jar http://de4f29954-elasticl-1lm67o8k2g1ac-1877371296.us-west-2.elb.amazonaws.com/mesos true’
