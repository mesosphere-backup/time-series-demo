package mesosphere.crimedemo

import java.net.URI

import scala.io.Source

object Main {

  lazy val log = org.slf4j.LoggerFactory.getLogger(getClass.getName)

  def main(args: Array[String]): Unit = {
    val conf = new Conf(args)
    val publisher = new KafkaPublisher(conf.brokers())
    val source = Source.fromURI(new URI(conf.uri()))
    val topic = conf.topic()
    val sleep = 1000L / conf.eventsPerSecond()

    var done = 0

    log.info(s"Reading crime from ${conf.uri()} and publishing to ${conf.brokers()} every ${sleep}ms")

    source.getLines().foreach(line => {
      publisher.publishKafka(topic, line.getBytes)
      done += 1

      if (done % 1000 == 0) {
        log.info(s"$done lines done")
      }

      Thread.sleep(sleep)
    })

    log.info(s"$done lines done")
  }
}
