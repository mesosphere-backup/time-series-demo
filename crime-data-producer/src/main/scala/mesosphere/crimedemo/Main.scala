package mesosphere.crimedemo

import java.io.BufferedInputStream
import java.net.URI
import java.util.zip.GZIPInputStream

import org.tukaani.xz.XZInputStream

import scala.io.Source

object Main {

  lazy val log = org.slf4j.LoggerFactory.getLogger(getClass.getName)

  def main(args: Array[String]): Unit = {
    val conf = new Conf(args)
    val publisher = new KafkaPublisher(conf.brokers())
    val topic = conf.topic()
    val sleep = 1000L / conf.eventsPerSecond()
    val uri = new URI(conf.uri())
    val inputStream = new BufferedInputStream(uri.toURL.openStream())

    val wrappedStream = if (conf.uri().endsWith(".gz")) {
      new GZIPInputStream(inputStream)
    }
    else if (conf.uri().endsWith(".xz")) {
      new XZInputStream(inputStream)
    }
    else {
      inputStream
    }
    val source = Source.fromInputStream(wrappedStream)

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
