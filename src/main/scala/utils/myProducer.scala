package utils

import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

/**
  * Created by muratcengiz on 05/07/17.
  */
object myProducer {
  val props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  val localhost = "10.41.170.143"
  props.put("zookeeper.connect", localhost + ":2181")
  props.put("client.id", "ScalaProducerExample")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  def sendmsg(msg: String, ip: String, topic: String): Unit = {
    val producer = new KafkaProducer[String, String](props)
    val data = new ProducerRecord[String, String](topic, ip, msg)
    producer.send(data)
    producer.close()
  }
}
