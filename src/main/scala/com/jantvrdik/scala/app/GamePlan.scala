package com.jantvrdik.scala.app

import scala.collection.mutable.ArrayBuffer

class GamePlan(settings: GameSettings) {

  type Pos = Vector[Int]

  private val gameplan = ArrayBuffer.fill(settings.dim.product)(-1)

  def getMark(pos: Pos) = {
    gameplan(toLinearPos(pos))
  }

  def setMark(pos: Pos, mark: Int) = {
    gameplan.update(toLinearPos(pos), mark)
  }

  private def toLinearPos(pos: Pos): Int = {
    var linearPos = 0
    var dimCoef = 1
    for (i <- 0 until pos.length) {
      linearPos += pos(i) * dimCoef
      dimCoef *= settings.dim(i)
    }
    linearPos
  }

}
