package utils

import java.util

import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer}
import org.apache.kafka.common.TopicPartition
import utils.FeelUtils.Feels
import utils.MovieUtils.Movie

import scala.collection.mutable.ListBuffer

object myConsumer {
  import java.util.Properties

  val topicMovie = "movie3"
  val topicFeels = "feel3"

  def createConsumerConfig(): Properties = {
    val props = new Properties()
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-consumer-group")
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
    props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000")
    //props.put("zookeeper.connect", "localhost:2181")
    props.put("auto.offset.reset", "earliest")
    props.put("session.timeout.ms", "30000")
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("partition.assignment.strategy", "org.apache.kafka.clients.consumer.RangeAssignor")
    props
  }

  val props = createConsumerConfig()
  val consumerMovie = new KafkaConsumer[String, String](props)
  val topicsMovie : util.List[String] = new util.ArrayList[String]()
  val consumerFeels = new KafkaConsumer[String, String](props)
  val topicsFeels : util.List[String] = new util.ArrayList[String]()

  def start() = {
    topicsMovie.add(topicMovie)
    topicsFeels.add(topicFeels)
    consumerMovie.subscribe(topicsMovie)
    consumerFeels.subscribe(topicsFeels)
  }

  def getMovies(n : Long, restart : Boolean) : List[Movie] = {
    consumerMovie.poll(100)
    val partition = new TopicPartition(topicMovie, 0)
    val top = new util.ArrayList[TopicPartition]()
    top.add(partition)
    consumerMovie.seekToEnd(top)
    val pos = consumerMovie.position(partition)
    println("Pos = " + pos)
    var toGet = n
    if (n < 0)
      toGet = pos
    else if (n > pos)
      toGet = pos

    if (restart) {
      consumerMovie.seekToBeginning(top)
    }

    var count = 0
    val records = consumerMovie.poll(100)
    val it = records.records(topicMovie).iterator()
    var messages = new ListBuffer[Movie]()
    while (it.hasNext) {
      val record = it.next()
      println(record.value)
      val res = MovieUtils.StringToMovie(record.value)
      res match {
        case Some(x) => {
          messages += x
          count += 1
        }
      }
      if (count >= toGet)
        return messages.toList
      else if (count >= pos)
        return messages.toList
    }
    return messages.toList
  }

  def getFeels(n : Long, restart : Boolean) : List[Feels] = {
    consumerFeels.poll(100)
    val partition = new TopicPartition(topicFeels, 0)
    val pos = consumerFeels.position(partition)
    var toGet = n
    if (n < 0)
      toGet = pos
    else if (n > pos)
      toGet = pos

    if (restart) {
      val top = new util.ArrayList[TopicPartition]()
      top.add(partition)
      consumerFeels.seekToBeginning(top)
    }

    var count = 0
    val records = consumerFeels.poll(100)
    val it = records.records(topicFeels).iterator()
    var messages = new ListBuffer[Feels]()
    while (it.hasNext) {
      val record = it.next()
      val res = FeelUtils.StringToFeels(record.value)
      res match {
        case Some(x) => {
          messages += x
          count += 1
        }
      }
      if (count >= toGet)
        return messages.toList
      else if (count >= pos)
        return messages.toList
    }
    return messages.toList
  }
}