package com.jantvrdik.scala.app

class GameModel(settings: GameSettings, plan: GamePlan) {

  type Pos = Vector[Int]
  type Direction = Vector[Int]

  var onVictory: (Player, List[Pos]) => Unit = null
  var onTurn: (Player, Pos) => Unit = null

  private var currentPlayer = settings.players.head
  private var currentPlayerId = 0
  private var finished = false

  def select(pos: Pos) = {
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

  private def isPosValid(pos: Pos) = {
    pos.forall(_ >= 0) && (pos, settings.dim).zipped.forall(_ < _)
  }

  private def findLongestRow(start: Pos): List[Pos] = {
    findLongestRow(start, Vector.empty)
  }

  private def findLongestRow(start: Pos, direction: Direction): List[Pos] = {
    if (direction.length == settings.dim.length) {
      if (direction.exists(_ != 0)) getSameInRow(plan.getMark(start), start, direction)
      else List.empty

    } else {
      Vector(-1, 0, 1)
        .map(i => findLongestRow(start, direction :+ i))
        .reduceLeft((x, y) => if (x.length > y.length) x else y)
    }
  }

  private def getSameInRow(mark: Player, start: Pos, direction: Direction): List[Pos] = {
    val direction2 = invertVector(direction)
    val start2 = addVector(start, direction2)
    getSameInRowOriented(mark, start, direction) ::: getSameInRowOriented(mark, start2, direction2)
  }

  private def getSameInRowOriented(mark: Player, start: Pos, direction: Direction): List[Pos] = {
    if (isPosValid(start) && mark == plan.getMark(start)) {
      start :: getSameInRowOriented(mark, addVector(start, direction), direction)
    } else {
      List.empty
    }
  }

  private def addVector(a: Pos, b: Direction) = {
    (a, b).zipped.map(_ + _)
  }

  private def invertVector(a: Direction): Direction = {
    a.map(-_)
  }
}
