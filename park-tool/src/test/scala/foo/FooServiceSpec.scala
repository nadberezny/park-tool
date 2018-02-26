package foo

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest._

class FooServiceSpec extends FlatSpec with Matchers with ScalatestRouteTest with Service {
  override def config = testConfig

  it should "respond to OK with OK" in {
    Post("/app/foo", FooRequest(requestMsg = "OK")) ~> routes ~> check {
      status shouldBe OK
      contentType shouldBe `application/json`
      responseAs[FooResponse] shouldBe FooResponse(responseMsg = "OK")
    }
  }

  it should "if not OK then responds wit Error" in {
    Post("/app/foo", FooRequest(requestMsg = "Some")) ~> routes ~> check {
      status shouldBe OK
      contentType shouldBe `application/json`
      responseAs[FooResponse] shouldBe FooResponse(responseMsg = "Error")
    }
  }

  it should "bar" in {
    Post("/app/bar", FooRequest("OK")) ~> routes ~> check {
      status shouldBe OK
      contentType shouldBe `application/json`
      responseAs[FooResponse] shouldBe FooResponse(responseMsg = "Error")
    }
  }
}
