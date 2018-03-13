package com.nadberezny.parkcalculator.db

import slick.lifted.TableQuery

package object queries {
  val parkingRegisters = TableQuery[tables.ParkingRegisters]
}