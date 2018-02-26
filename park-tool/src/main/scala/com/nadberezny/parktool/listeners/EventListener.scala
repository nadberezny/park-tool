package com.nadberezny.parktool.listeners

import akka.actor.{ Actor, ActorLogging, Props }
import cakesolutions.kafka.KafkaProducer.Conf
import cakesolutions.kafka.{ KafkaProducer, KafkaProducerRecord }
import com.nadberezny.parktool.actors._
import com.nadberezny.parktool.serializers.JsonSerializer
import com.typesafe.config.ConfigFactory
import org.apache.kafka.common.serialization.StringSerializer

object EventListener {
  def props = Props[EventListener]
}

class EventListener extends Actor with ActorLogging {
  val kafkaConf = ConfigFactory.load.getConfig("kafka")

  override def receive: Receive = {
    case req: ParkingMeter.Start => {
      val record = KafkaProducerRecord(topic = "foo", key = Some("parkingMeterStart"), value = req)

      val producer = KafkaProducer(
        Conf(kafkaConf, new StringSerializer(), new JsonSerializer[ParkingMeter.Start])
      )
      producer send record
      log.info(s"Received Start event of vehicle: ${req.vehicleId}")
    }

    case req: ParkingMeter.Stop =>
      log.info(s"Received Stop event of vehicle: ${req.vehicleId}")
  }
}
