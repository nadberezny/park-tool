package com.nadberezny.parkregistry

import akka.actor.{ Actor, ActorLogging, Props }
import cakesolutions.kafka.akka.KafkaConsumerActor.{ Confirm, Subscribe, Unsubscribe }
import cakesolutions.kafka.akka.{ ConsumerRecords, KafkaConsumerActor }
import com.nadberezny.parkregistry.EventConsumer.StartEvent
import org.apache.kafka.common.serialization.StringDeserializer
import play.api.libs.json.Json

object EventConsumer {
  def props = Props[EventConsumer]

  object StartEvent {
    implicit val msgFormat = Json.reads[StartEvent]
  }
  case class StartEvent(vehicleId: String, date: String)
}

class EventConsumer extends Actor with ActorLogging {
  import ParkRegistryApp.conf

  val recordsExtractor = ConsumerRecords.extractor[String, StartEvent]
  val consumerConf = conf.getConfig("kafkaConsumer")
  val kafkaConsumer = context.actorOf(
    KafkaConsumerActor.props(
      consumerConf, new StringDeserializer, new JsonDeserializer[StartEvent], self
    )
  )

  override def receive: Receive = {
    case recordsExtractor(records) =>
      processRecords(records.pairs)
      kafkaConsumer ! Confirm(records.offsets, commit = true)
  }

  override def preStart() = {
    super.preStart()
    log.info("Consumer started")
    kafkaConsumer ! Subscribe.AutoPartition(List("foo"))
  }

  override def postStop() = {
    log.info("consumer stopped")
    kafkaConsumer ! Unsubscribe
    super.postStop()
  }

  def processRecords(records: Seq[(Option[String], StartEvent)]) =
    records.foreach { record =>
      log.info("dupa")
      log.info(s"Consumed: ${record._2.date}")
    }
}

