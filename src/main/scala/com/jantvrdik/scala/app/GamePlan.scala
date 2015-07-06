package com.jantvrdik.scala.app

import scala.collection.mutable.ArrayBuffer

class GamePlan(settings: GameSettings) extends Iterable[(GamePos, Player)] {

  private val gameplan = ArrayBuffer.fill[Player](settings.dim.product)(null)

  private var used = List[(GamePos, Player)]()

  def getMark(pos: GamePos) = {
    gameplan(toLinearPos(pos))
  }

  def setMark(pos: GamePos, mark: Player) = {
    gameplan.update(toLinearPos(pos), mark)
    used = (pos, mark) :: used
  }

  private def toLinearPos(pos: GamePos): Int = {
    var linearPos = 0
    var dimCoef = 1
    for (i <- 0 until pos.length) {
      linearPos += pos(i) * dimCoef
      dimCoef *= settings.dim(i)
    }
    linearPos
  }

  override def iterator: Iterator[(GamePos, Player)] = {
    used.toIterator
  }
}
