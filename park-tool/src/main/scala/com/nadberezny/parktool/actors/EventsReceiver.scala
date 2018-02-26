package com.nadberezny.parktool.actors

import akka.actor.{ Actor, ActorLogging, Props }
import cakesolutions.kafka.akka.KafkaConsumerActor.{ Confirm, Subscribe, Unsubscribe }
import cakesolutions.kafka.akka.{ ConsumerRecords, KafkaConsumerActor }
import com.nadberezny.parktool.ParkToolApp.conf
import org.apache.kafka.common.serialization.StringDeserializer
import com.nadberezny.parktool.serializers.JsonDeserializer

object EventsReceiver {
  def props = Props[EventsReceiver]
}

class EventsReceiver extends Actor with ActorLogging {
  val consumerConf = conf.getConfig("kafkaConsumer")
  val consumer = context.actorOf(
    KafkaConsumerActor.props(consumerConf, new StringDeserializer(), new JsonDeserializer[ParkingMeter.Start], self)
  )

  // Extractor for ensuring type safe cast of records
  val recordsExt = ConsumerRecords.extractor[String, ParkingMeter.Start]

  override def preStart(): Unit = {
    super.preStart()
    consumer ! Subscribe.AutoPartition(List("test"))
    log.info("consumer subscribed")
  }

  override def postStop(): Unit = {
    consumer ! Unsubscribe
    log.info("Consumer unsubscribe")
    super.postStop()
  }

  override def receive: Receive = {
    // Type safe cast of records to correct serialisation type
    case recordsExt(records) =>
      log.info("started to consume")
      processRecords(records.pairs)
      consumer ! Confirm(records.offsets, commit = true)
  }

  // Process the whole batch of received records.
  // The first value in the tuple is the optional key of a record.
  // The second value in the tuple is the actual value from a record.
  def processRecords(records: Seq[(Option[String], ParkingMeter.Start)]) =
    records.foreach { x => log.info(s"Consumed: ${x._2.vehicleId}") }
}
