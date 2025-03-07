name := "sparkmovies"

version := "1.0"

scalaVersion := "2.10.2"

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies += "com.typesafe.play" %% "play-json" % "2.2.1"
libraryDependencies += "org.apache.spark" %% "spark-core" % "2.0.0"
libraryDependencies += "org.apache.httpcomponents" % "httpclient" % "4.5.3"
libraryDependencies += "org.apache.zookeeper" % "zookeeper" % "3.4.8"
libraryDependencies += "org.apache.kafka" % "kafka_2.10" % "0.10.2.1"