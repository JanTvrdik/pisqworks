package com.jantvrdik.scala.app

import scala.collection.mutable.ArrayBuffer

class GamePlan(settings: GameSettings) extends Iterable[(Vector[Int], Player)] {

  type Pos = Vector[Int]
  type Pair = (Pos, Player)

  private val gameplan = ArrayBuffer.fill[Player](settings.dim.product)(null)

  private var used = List[Pair]()

  def getMark(pos: Pos) = {
    gameplan(toLinearPos(pos))
  }

  def setMark(pos: Pos, mark: Player) = {
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

  override def iterator: Iterator[Pair] = {
    used.toIterator
  }
}
