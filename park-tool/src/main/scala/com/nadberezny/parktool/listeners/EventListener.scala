package com.nadberezny.parktool.listeners

import akka.actor.{ Actor, Props }
import cakesolutions.kafka.KafkaProducer.Conf
import cakesolutions.kafka.{ KafkaProducer, KafkaProducerRecord }
import com.nadberezny.parktool.actors._
import com.nadberezny.parktool.serializers.JsonSerializer
import org.apache.kafka.common.serialization.StringSerializer

object EventListener {
  def props = Props[EventListener]
}

class EventListener extends Actor {
  import ParkingMeter.Event
  import com.nadberezny.parktool.ParkToolApp.conf

  val kafkaConf = conf.getConfig("kafka")
  val topic = conf.getString("kafkaTopic")

  val eventProducer = KafkaProducer(
    Conf(kafkaConf, new StringSerializer, new JsonSerializer[Event])
  )

  override def receive: Receive = {
    case event: Event =>
      eventProducer send KafkaProducerRecord(topic, Some(topic), event)
  }
}
