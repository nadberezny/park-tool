package com.nadberezny.parkcalculator.services.feecalculator

object GetPrice {
  def apply(input: Calculable): EitherCalculable = new GetPrice(input).process
}

class GetPrice(calculable: Calculable) {

  private[GetPrice] def process: EitherCalculable = {
    priceOption match {
      case Some(priceRow) => Right(calculable.copy(price = priceRow.amount))
      case _ => Left("Price info unavailable.")
    }
  }

  private lazy val priceOption: Option[PriceRow] =
    PriceRepository.find(calculable.currency)
}
