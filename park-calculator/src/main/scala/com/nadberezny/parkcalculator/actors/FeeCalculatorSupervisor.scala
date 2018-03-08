package com.nadberezny.parkcalculator.actors

import akka.actor.{ Actor, ActorLogging, Props }
import com.nadberezny.parkcalculator.actors.FeeCalculatorSupervisor.GetFeeRequest
import com.nadberezny.parkcalculator.services.FeeCalculator
import com.nadberezny.parkcalculator.services.feecalculator.EitherCalculable
import com.nadberezny.parkcalculator.ParkCalculatorApp.parkingRepository

object FeeCalculatorSupervisor {
  case class GetFeeRequest(vehicleId: String, currency: String)

  def props = Props[FeeCalculatorSupervisor]
}

class FeeCalculatorSupervisor extends Actor with ActorLogging {
  lazy val feeCalculator = new FeeCalculator()

  def receive = {
    case GetFeeRequest(vehicleId, currency) =>
      val eitherCalculable: EitherCalculable = feeCalculator.calculate(vehicleId, currency)
      sender ! eitherCalculable
  }
}
