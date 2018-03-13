package com.nadberezny.parkregistry.serializers

import java.util

import org.apache.kafka.common.serialization.{ Deserializer, StringDeserializer }
import play.api.libs.json.{ Json, Reads }

class JsonDeserializer[T: Reads] extends Deserializer[T] {

  private val stringDeserializer = new StringDeserializer

  override def configure(configs: util.Map[String, _], isKey: Boolean) =
    stringDeserializer.configure(configs, isKey)

  override def deserialize(topic: String, data: Array[Byte]) =
    Json.parse(stringDeserializer.deserialize(topic, data)).as[T]

  override def close() =
    stringDeserializer.close()
}
