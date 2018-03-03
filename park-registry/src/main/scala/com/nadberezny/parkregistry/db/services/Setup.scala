package com.nadberezny.parkregistry.db.services

import com.nadberezny.parkregistry.db.queries._
import slick.jdbc.PostgresProfile.api._

object Setup {
  def apply(database: Database): Unit = database.run(recreateTables)

  private def recreateTables =
    DBIO.seq(
      sqlu"DROP TABLE IF EXISTS #${parkingRegisters.baseTableRow.tableName}",
      parkingRegisters.schema.create,
    )
}
