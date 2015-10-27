package mesosphere.crimedemo


import org.rogach.scallop._


class Conf(arguments: Seq[String]) extends ScallopConf(arguments) {
  val brokers = opt[String]("brokers", required = true)
  val topic = opt[String]("topic", required = true)
  val uri = opt[String]("uri", required = true)
  val eventsPerSecond = opt[Int]("events_per_second", default = Some(10))
}
