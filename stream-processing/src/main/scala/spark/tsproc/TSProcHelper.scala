package spark.tsproc

import org.apache.spark.Logging

import org.apache.log4j.{Level, Logger}

object TSProcHelper extends Logging {

  def setLogLevel() {
    val log4jInitialized = Logger.getRootLogger.getAllAppenders.hasMoreElements
    if (!log4jInitialized) {
      logInfo("Setting log level to [ERROR].")
      Logger.getRootLogger.setLevel(Level.ERROR)
    }
  }
}