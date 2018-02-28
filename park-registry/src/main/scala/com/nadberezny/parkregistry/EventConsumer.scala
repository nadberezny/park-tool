package com.nadberezny.parkregistry

import akka.actor.{ Actor, ActorLogging, Props }
import cakesolutions.kafka.akka.KafkaConsumerActor.{ Confirm, Subscribe, Unsubscribe }
import cakesolutions.kafka.akka.{ ConsumerRecords, KafkaConsumerActor }
import org.apache.kafka.common.serialization.StringDeserializer
import play.api.libs.json.Json
import scala.collection.JavaConverters._

object EventConsumer {
  def props = Props[EventConsumer]

  object Event { implicit val msgFormat = Json.reads[Event] }
  case class Event(eventType: String, parkingMeterId: Int, vehicleId: String, date: String)
}

class EventConsumer extends Actor with ActorLogging {
  import ParkRegistryApp.conf
  import EventConsumer._

  val recordsExtractor = ConsumerRecords.extractor[String, Event]

  val consumerConf = conf.getConfig("kafkaConsumer")
  val topics: List[String] = consumerConf.getStringList("topics").asScala.toList

  val kafkaConsumer = context.actorOf(
    KafkaConsumerActor.props(
      consumerConf, new StringDeserializer, new JsonDeserializer[Event], self
    )
  )

  override def receive: Receive = {
    case recordsExtractor(records) =>
      processRecords(records.pairs)
      kafkaConsumer ! Confirm(records.offsets, commit = true)
  }

  override def preStart() = {
    super.preStart()
    //    kafkaConsumer ! Subscribe.AutoPartition(topics)
    kafkaConsumer ! Subscribe.AutoPartition(List("parking-meter-event"))
  }

  override def postStop() = {
    kafkaConsumer ! Unsubscribe
    super.postStop()
  }

  def processRecords(records: Seq[(Option[String], Event)]) =
    records.foreach { record =>
      log.info(s"Consumed: ${record._2.eventType}")
    }
}
