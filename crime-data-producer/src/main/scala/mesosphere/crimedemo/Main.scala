package mesosphere.crimedemo

import java.net.URI

import scala.io.Source

object Main {

  lazy val log = org.slf4j.LoggerFactory.getLogger(getClass.getName)

  def main(args: Array[String]): Unit = {
    val conf = new Conf(args)
    val publisher = new KafkaPublisher(conf.brokers())
    val uri = new URI(conf.uri())
    val topic = conf.topic()

    var done = 0

    Source.fromURI(uri).getLines().foreach(line => {
      publisher.publishKafka(topic, line.getBytes)
      done += 1

      if (done % 1000 == 0) {
        log.info(s"$done lines done")
      }
    })

    log.info(s"$done lines done")
  }
}
