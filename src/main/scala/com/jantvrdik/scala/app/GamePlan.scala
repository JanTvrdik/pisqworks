package com.jantvrdik.scala.app

import scala.collection.mutable.ArrayBuffer

class GamePlan(settings: GameSettings) extends Iterable[(Vector[Int], Int)] {

  type Pos = Vector[Int]

  private val gameplan = ArrayBuffer.fill(settings.dim.product)(-1)

  private var used = List[(Pos, Int)]()

  def getMark(pos: Pos) = {
    gameplan(toLinearPos(pos))
  }

  def setMark(pos: Pos, mark: Int) = {
    gameplan.update(toLinearPos(pos), mark)
    used = (pos, mark) :: used
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

  override def iterator: Iterator[(Vector[Int], Int)] = {
    used.toIterator
  }
}
