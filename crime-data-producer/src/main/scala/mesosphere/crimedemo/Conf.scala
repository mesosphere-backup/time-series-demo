package mesosphere.crimedemo

import org.rogach.scallop._


class Conf(arguments: Seq[String]) extends ScallopConf(arguments) {
  val brokers = opt[String]("brokers", required = true)
}
