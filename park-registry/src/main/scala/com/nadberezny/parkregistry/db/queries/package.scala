package com.nadberezny.parkregistry.db

import slick.lifted.TableQuery

package object queries {
  val parkingRegisters = TableQuery[tables.ParkingRegisters]
}
