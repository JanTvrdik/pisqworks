package com.jantvrdik.scala.app

import scala.collection.mutable.ArrayBuffer

class GamePlan(settings: GameSettings) {

  val gameplan = ArrayBuffer.fill(settings.dim.product)(-1)

  def getMark(pos: Vector[Int]) = {
    gameplan(toLinearPos(pos))
  }

  def setMark(pos: Vector[Int], mark: Int) = {
    gameplan.update(toLinearPos(pos), mark)
  }

  private def toLinearPos(pos: Vector[Int]): Int = {
    var linearPos = 0
    var dimCoef = 1
    for (i <- 0 until pos.length) {
      linearPos += pos(i) * dimCoef
      dimCoef *= settings.dim(i)
    }
    linearPos
  }

}
