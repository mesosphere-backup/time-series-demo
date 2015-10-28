name := "crime-data-producer"

version := "0.1"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "org.apache.kafka" %% "kafka" % "0.8.2.2",
  "org.slf4j" % "slf4j-api" % "1.7.12",
  "org.rogach" %% "scallop" % "0.9.5",
  "org.tukaani" % "xz" % "1.5"
)
