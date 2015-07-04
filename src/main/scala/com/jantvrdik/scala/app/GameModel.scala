package com.jantvrdik.scala.app

class GameModel(settings: GameSettings, plan: GamePlan) {

  var onVictory: (Player, List[Vector[Int]]) => Unit = null
  var onTurn: (Player, Vector[Int]) => Unit = null

  private var currentPlayer = 0
  private var finished = false

  def select(pos: Vector[Int]) = {
    if (!finished && isPosValid(pos) && plan.getMark(pos) < 0) {
      plan.setMark(pos, currentPlayer)
      onTurn(settings.players(currentPlayer), pos)

      val longest = findLongestRow(pos)
      if (longest.length >= settings.winLength) {
        onVictory(settings.players(currentPlayer), longest)
        finished = true
      }

      currentPlayer = (currentPlayer + 1) % settings.players.length
      true

    } else {
      false
    }
  }

  private def isPosValid(pos: Vector[Int]) = {
    pos.forall(_ >= 0) && (pos, settings.dim).zipped.forall(_ < _)
  }

  private def findLongestRow(start: Vector[Int]): List[Vector[Int]] = {
    findLongestRow(start, Vector.empty)
  }

  private def findLongestRow(start: Vector[Int], direction: Vector[Int]): List[Vector[Int]] = {
    if (direction.length == settings.dim.length) {
      if (direction.sum > 0) getSameInRow(plan.getMark(start), start, direction)
      else List.empty

    } else {
      val a = findLongestRow(start, (0 :: direction.toList).toVector)
      val b = findLongestRow(start, (1 :: direction.toList).toVector)

      if (a.length < b.length) b
      else a
    }
  }

  private def getSameInRow(mark: Int, start: Vector[Int], direction: Vector[Int]): List[Vector[Int]] = {
    val direction2 = invertVector(direction)
    val start2 = addVector(start, direction2)
    getSameInRowOriented(mark, start, direction) ::: getSameInRowOriented(mark, start2, direction2)
  }

  private def getSameInRowOriented(mark: Int, start: Vector[Int], direction: Vector[Int]): List[Vector[Int]] = {
    if (isPosValid(start) && mark == plan.getMark(start)) {
      start :: getSameInRowOriented(mark, addVector(start, direction), direction)
    } else {
      List.empty
    }
  }

  private def addVector(a: Vector[Int], b: Vector[Int]) = {
    (a, b).zipped.map(_ + _)
  }

  private def invertVector(a: Vector[Int]): Vector[Int] = {
    a.map(-_)
  }
}
