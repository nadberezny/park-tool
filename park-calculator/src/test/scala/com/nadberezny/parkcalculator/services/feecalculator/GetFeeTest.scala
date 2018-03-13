package com.nadberezny.parkcalculator.services.feecalculator

import org.scalatest.FunSuite

class GetFeeTest extends FunSuite {
  case class T(calculable: Calculable, expectation: EitherCalculable)

  val regularFactors = List(
    Factor(durationInterval = 60, value = 1),
    Factor(60, 2),
    Factor(60, 4)
  )

  val vipFactors = List(
    Factor(durationInterval = 60, value = 0),
    Factor(60, 2),
    Factor(60, 3)
  )
  val calculable =
    Calculable(vehicleId = "RST", currency = "USD", price = 1.0, factors = regularFactors)

  val vipCalculable =
    calculable.copy(isVip = true, factors = vipFactors)

  val testCases = List(
    // failure
    T(calculable.copy(duration = -1),         Left("Invalid data.")),
    T(calculable.copy(factors = List.empty),  Left("Invalid data.")),

    // regular
    T(calculable.copy(duration = 15),  Right(calculable.copy(duration = 15,  fee = 1))),
    T(calculable.copy(duration = 45),  Right(calculable.copy(duration = 45,  fee = 1))),
    T(calculable.copy(duration = 75),  Right(calculable.copy(duration = 75,  fee = 3))),
    T(calculable.copy(duration = 120), Right(calculable.copy(duration = 120, fee = 7))),
    T(calculable.copy(duration = 200), Right(calculable.copy(duration = 200, fee = 11))),

    // vip
    T(vipCalculable.copy(duration = 15),  Right(vipCalculable.copy(duration = 15,  fee = 0))),
    T(vipCalculable.copy(duration = 45),  Right(vipCalculable.copy(duration = 45,  fee = 0))),
    T(vipCalculable.copy(duration = 75),  Right(vipCalculable.copy(duration = 75,  fee = 2))),
    T(vipCalculable.copy(duration = 120), Right(vipCalculable.copy(duration = 120, fee = 5))),
    T(vipCalculable.copy(duration = 200), Right(vipCalculable.copy(duration = 200, fee = 8))),
  )

  test("fee calculation") {
    testCases.foreach(testCase => {
      assert(GetFee(testCase.calculable) == testCase.expectation)
    })
  }
}
