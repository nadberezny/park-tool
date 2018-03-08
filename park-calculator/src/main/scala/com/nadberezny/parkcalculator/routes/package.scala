package com.nadberezny.parkcalculator

package object routes {
  case class ParkingFeeResponse(
    vehicleId: String,
    duration: Long,
    fee: Double,
    currency: String
  )
}
