package mesosphere.crimedemo

object Main {

  def main(args: Array[String]): Unit = {
    val conf = new Conf(args)

    val publisher = new KafkaPublisher(conf.brokers())

    while(true) {
      publisher.publishKafka("test", "test message".getBytes)
      Thread.sleep(1000);
    }
  }

}
