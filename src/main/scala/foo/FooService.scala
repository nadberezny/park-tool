package foo

import akka.actor.{ Actor, ActorSystem, Props }
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import spray.json.DefaultJsonProtocol
import akka.http.scaladsl.server.Directives._
import akka.stream.{ ActorMaterializer, Materializer }
import com.typesafe.config.{ Config, ConfigFactory }
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

import scala.concurrent.{ ExecutionContextExecutor, Future }

object Bar {
  def props = Props[Bar]
}

class Bar extends Actor {
  override def receive: Receive = {
    case _: FooRequest =>
      sender ! FooResponse("Bar")
  }
}

case class FooRequest(requestMsg: String)
case class FooResponse(responseMsg: String)

trait Protocols extends DefaultJsonProtocol {
  implicit val fooRequestFormat = jsonFormat1(FooRequest)
  implicit val fooResponseFormat = jsonFormat1(FooResponse)
}

trait Service extends Protocols {
  implicit val system: ActorSystem
  implicit def executor: ExecutionContextExecutor
  implicit val materializer: Materializer
  implicit val timeout = Timeout(1.seconds)

  def config: Config

  def foo(request: FooRequest): Future[Either[FooResponse, FooResponse]] = {
    Future.successful {
      request.requestMsg match {
        case "OK" => Right(FooResponse("OK"))
        case _ => Left(FooResponse("Error"))
      }
    }
  }

  def bar(request: FooRequest): Future[Either[FooResponse, FooResponse]] = {
    val barActor = system.actorOf(Bar.props)
    (barActor ? request).mapTo[FooResponse].map { res =>
      res.responseMsg match {
        case "OK" => Right(FooResponse("OK"))
        case _ => Left(FooResponse("Error"))
      }
    }
  }

  lazy val routes = {
    logRequestResult("foo-service") {
      pathPrefix("app") {
        concat(
          path("foo") {
            post {
              entity(as[FooRequest]) { req =>
                complete {
                  foo(req).map[ToResponseMarshallable] {
                    case Right(res) => res
                    case Left(res) => res
                  }
                }
              }
            }
          },
          path("bar") {
            post {
              entity(as[FooRequest]) { req =>
                complete {
                  bar(req).map[ToResponseMarshallable] {
                    case Right(res) => res
                    case Left(res) => res
                  }
                }
              }
            }
          }
        )
      }
    }
  }
}

object FooService extends App with Service {
  override implicit val system = ActorSystem()
  override implicit val executor = system.dispatcher
  override implicit val materializer = ActorMaterializer()

  override val config = ConfigFactory.load()

  Http().bindAndHandle(routes, config.getString("app.host"), config.getInt("app.port"))
}
