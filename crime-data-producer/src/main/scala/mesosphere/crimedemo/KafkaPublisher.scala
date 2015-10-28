package mesosphere.crimedemo

import java.util.Properties

import kafka.producer.{KeyedMessage, Producer, ProducerConfig}


class KafkaPublisher(brokers: String,
                     async: Boolean = false) {

  lazy val log = org.slf4j.LoggerFactory.getLogger(getClass.getName)

  protected type KeyType = Array[Byte]
  protected type ValueType = Array[Byte]

  val producer = {
    log.info(s"Configured to publish to Kafka env: on $brokers using async = $async")

    val props = new Properties()
    props.put("metadata.broker.list", brokers)
    props.put("request.required.acks", "1")
    props.put("max.message.size", "52428800") //50 MB

    if (async) props.put("producer.type", "async")

    val config = new ProducerConfig(props)
    val producer = new Producer[KeyType, ValueType](config)
    log.info(s"Kafka producer connected to $brokers")
    producer
  }

  def shutdownPublisher() = producer.close()

  def publishKafka(topic: String, message: ValueType): Unit = {
    log.debug(s"publishKafka(topic : $topic, message : $message)")
    producer.send(new KeyedMessage[KeyType, ValueType](topic, null, message))
  }

}