package com.nadberezny.parkcalculator.services.feecalculator

object GetFee {
  def apply(calculable: Calculable): EitherCalculable =
    new GetFee(calculable).process
}

class GetFee(calculable: Calculable) {
  import math.ceil

  private[GetFee] def process: EitherCalculable = {
    if (isValid) {
      val fee = calculate(calculable.duration, calculable.factors)
      Right(calculable.copy(fee = fee))
    } else {
      Left("Invalid data.")
    }
  }

  private def isValid: Boolean = {
    calculable.duration >= 0 && calculable.factors.nonEmpty
  }

  private def calculate(durationToCalculate: Long, factorsToInclude: List[Factor], feeAccumulated: Double = 0): Double = {
    def getFee(factorValue: Double, times: Double) = {
      feeAccumulated + (times * calculable.price * factorValue)
    }

    factorsToInclude match {
      case factor :: _ if durationToCalculate < factor.durationInterval =>
        getFee(factor.value, times = 1)

      case factor :: Nil =>
        val times = ceil(durationToCalculate / factor.durationInterval.toFloat)
        getFee(factor.value, times)

      case factor :: factors =>
        val fee = getFee(factor.value, times = 1)
        calculate(durationToCalculate - factor.durationInterval, factors, fee)
    }
  }
}
