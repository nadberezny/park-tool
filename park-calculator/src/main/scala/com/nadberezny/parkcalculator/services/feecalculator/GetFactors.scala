package com.nadberezny.parkcalculator.services.feecalculator

import cats.data.Kleisli
import cats.implicits._

object GetFactors {
  def apply(calculable: Calculable): EitherCalculable = new GetFactors(calculable).process
}

class GetFactors(calculable: Calculable) {

  private[GetFactors] def process: EitherCalculable = {
    sortedByRankOption match {
      case Some(factorRows) =>
        Factor(0, 0)
        val factors = factorRows.map(row =>
          Factor(
            row.duration,
            if (calculable.isVip) row.vip else row.regular
          ))
        Right(calculable.copy(factors = factors))

      case _ =>
        Left("VipInfo not available.")
    }
  }

  lazy val sortedByRankOption: Option[List[FactorRow]] = {
    val all = (_: Unit) => FactorRepository.all
    val sort = (factors: List[FactorRow]) => { (factors.sortWith(_.rank < _.rank)).some }

    (Kleisli(all) andThen Kleisli(sort)).run()
  }
}
