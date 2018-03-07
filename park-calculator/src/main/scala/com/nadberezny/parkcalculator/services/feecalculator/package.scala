package com.nadberezny.parkcalculator.services

package object feecalculator {
  type EitherCalculable = Either[String, Calculable]

  case class Calculable(
    vehicleId: String,
    currency: String,
    duration: Long = 0,
    isVip: Boolean = false,
    price: Double = 0.0,
    factors: List[Factor] = List.empty,
    fee: Double = 0
  )

  case class Factor(durationInterval: Long, value: Double)
}
