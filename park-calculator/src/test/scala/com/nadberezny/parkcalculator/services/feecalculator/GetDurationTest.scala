package com.nadberezny.parkcalculator.services.feecalculator

import org.joda.time.DateTime
import com.nadberezny.parkcalculator.testutils.TestParkingRepo
import org.scalatest.FunSuite

class GetDurationTest extends FunSuite {
  lazy val testRepo = new TestParkingRepo(duration = 30)
  test("duration calculation") {
    val calculable = Calculable(vehicleId = "RST", currency = "USD")
    assert(
      GetDuration(testRepo, calculable) == Right(calculable.copy(duration = 30))
    )

    val invalidCalculable = calculable.copy(vehicleId = "")
    assert(
      GetDuration(testRepo, invalidCalculable) == Left("Invalid vehicle ID.")
    )
  }
}
