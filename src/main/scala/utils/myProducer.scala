package utils

import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

/**
  * Created by muratcengiz on 05/07/17.
  */
object myProducer {
  val props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("acks", "all")
  props.put("linger.ms", "1")
  props.put("client.id", "ScalaProducerExample")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  def sendMessage(topic: String, ip: String, msg: String): Unit = {
    println("Message sent 0")
    val producer = new KafkaProducer[String, String](props)
    println("Message sent 1")
    val data = new ProducerRecord[String, String](topic, ip, msg)
    println("Message sent 2")
    producer.send(data)
    println("Message sent")
    producer.close()
  }
}
