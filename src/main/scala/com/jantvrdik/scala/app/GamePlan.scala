package com.jantvrdik.scala.app

import scala.collection.mutable.ArrayBuffer

class GamePlan(settings: GameSettings) {

  /** dimension coefficients for transforming to linear linear position */
  private val coef = settings.dim.scanLeft(1)(_ * _)

  /** linear position => player */
  private val gameplan = ArrayBuffer.fill[Player](settings.dim.product)(null)

  /** list of occupied positions */
  private var used = List[(GamePos, Player)]()

  def getMark(pos: GamePos) = {
    gameplan(toLinearPos(pos))
  }

  def setMark(pos: GamePos, mark: Player) {
    gameplan.update(toLinearPos(pos), mark)
    used = (pos, mark) :: used
  }

  def occupied = {
    used.toIterable
  }

  private def toLinearPos(pos: GamePos): Int = {
    (pos, coef).zipped.map(_ * _).sum
  }
}
