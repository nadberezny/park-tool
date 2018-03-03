package com.nadberezny.parkregistry.actors

import akka.actor.{ Actor, ActorLogging, ActorRef, Props }
import cakesolutions.kafka.akka.KafkaConsumerActor.{ Confirm, Subscribe, Unsubscribe }
import cakesolutions.kafka.akka.{ ConsumerRecords, KafkaConsumerActor }
import com.nadberezny.parkregistry.ParkRegistryApp
import com.nadberezny.parkregistry.serializers.JsonDeserializer
import org.apache.kafka.common.serialization.StringDeserializer
import play.api.libs.json.Json

import scala.collection.JavaConverters._

object EventConsumer {
  def props(persistEventService: ActorRef) =
    Props(new EventConsumer(persistEventService))

  object Event {
    implicit val msgFormat = Json.reads[Event]

    val Start = "start"
    val Stop = "stop"
  }
  case class Event(eventType: String, parkingMeterId: Int, vehicleId: String, date: String)
}

class EventConsumer(persistEventService: ActorRef) extends Actor with ActorLogging {
  import EventConsumer._
  import ParkRegistryApp.conf

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
    kafkaConsumer ! Subscribe.AutoPartition(topics)
  }

  override def postStop() = {
    kafkaConsumer ! Unsubscribe
    super.postStop()
  }

  def processRecords(records: Seq[(Option[String], Event)]) =
    records.foreach { record =>
      persistEventService ! record._2
    }
}

