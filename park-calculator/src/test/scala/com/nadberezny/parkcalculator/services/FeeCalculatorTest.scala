package com.nadberezny.parkcalculator.services

import com.nadberezny.parkcalculator.testutils.TestParkingRepo
import org.joda.time.DateTime
import org.scalatest.FunSuite

class FeeCalculatorTest extends FunSuite {
  implicit val now = DateTime.now
  implicit val testRepo = new TestParkingRepo(duration = 30)
  lazy val calculator = new FeeCalculator()

  test("happy path") {
    val result = calculator.calculate("RST", "USD")

    assert(result.isRight)
    assert(result.map(_.fee) == Right(1.0))
  }

  test("failure") {
    val expectedFailure = calculator.calculate("RST", currency = "XXX")
    assert(expectedFailure.isLeft)
    assert(expectedFailure == Left("Price info unavailable."))

  }
}
