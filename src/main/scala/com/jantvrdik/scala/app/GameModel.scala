package com.jantvrdik.scala.app

class GameModel(settings: GameSettings, plan: GamePlan) {

  var onVictory: (Player, Row) => Unit = null
  var onTurn: (Player, GamePos) => Unit = null

  private var currentPlayer = settings.players.head
  private var currentPlayerId = 0
  private var finished = false

  def select(pos: GamePos) = {
    if (!finished && isPosValid(pos) && plan.getMark(pos) == null) {
      plan.setMark(pos, currentPlayer)
      onTurn(currentPlayer, pos)

      val longest = findLongestRow(pos)
      if (longest.length >= settings.winLength) {
        onVictory(currentPlayer, longest)
        finished = true
      }

      currentPlayerId = (currentPlayerId + 1) % settings.players.length
      currentPlayer = settings.players(currentPlayerId)
      true

    } else {
      false
    }
  }

  private def isPosValid(pos: GamePos) = {
    pos.forall(_ >= 0) && (pos, settings.dim).zipped.forall(_ < _)
  }

  private def findLongestRow(start: GamePos): Row = {
    findLongestRow(start, Vector.empty)
  }

  private def findLongestRow(start: GamePos, direction: Direction): Row = {
    if (direction.length == settings.dim.length) {
      if (direction.exists(_ != 0)) getSameInRow(plan.getMark(start), start, direction)
      else List.empty

    } else {
      Vector(-1, 0, 1)
        .map(i => findLongestRow(start, direction :+ i))
        .reduceLeft((x, y) => if (x.length > y.length) x else y)
    }
  }

  private def getSameInRow(mark: Player, start: GamePos, direction: Direction): Row = {
    val direction2 = invertVector(direction)
    val start2 = addVector(start, direction2)
    getSameInRowOriented(mark, start, direction) ::: getSameInRowOriented(mark, start2, direction2)
  }

  private def getSameInRowOriented(mark: Player, start: GamePos, direction: Direction): Row = {
    if (isPosValid(start) && mark == plan.getMark(start)) {
      start :: getSameInRowOriented(mark, addVector(start, direction), direction)
    } else {
      List.empty
    }
  }

  private def addVector(a: GamePos, b: Direction) = {
    (a, b).zipped.map(_ + _)
  }

  private def invertVector(a: Direction): Direction = {
    a.map(-_)
  }
}
