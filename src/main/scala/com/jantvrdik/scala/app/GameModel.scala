package com.jantvrdik.scala.app

class GameModel(settings: GameSettings, plan: GamePlan) {

  /** called when a player wins */
  var onVictory: (Player, Row) => Unit = null

  /** called on every successful turn */
  var onTurn: (Player, GamePos) => Unit = null

  /** number of current turn */
  private var turn = 0

  /** has any player already won?  */
  private var finished = false

  def select(pos: GamePos): Boolean = {
    if (finished || !isPosValid(pos)) {
      return false
    }

    val pointer = plan.pointer(pos)
    if (!pointer.free) {
      return false
    }

    pointer.mark = currentPlayer
    turn = turn + 1
    onTurn(pointer.mark, pos)

    val longest = longestRow(pos)
    if (longest.length >= settings.winLength) {
      finished = true
      onVictory(pointer.mark, longest)
    }

    true
  }

  def currentPlayer = {
    settings.players(turn % settings.players.length)
  }

  def neighbors(pos: GamePos): List[GamePos] = {
    if (isPosValid(pos)) {
      neighbors(pos, List.empty, positive = false)
    } else {
      List.empty
    }
  }

  private def neighbors(start: GamePos, neighbor: Direction, positive: Boolean): List[GamePos] = {
    if (neighbor.length == settings.dim.length) {
      List(neighbor.toVector).filter(_ => positive)

    } else {
      val j = settings.dim.length - neighbor.length - 1
      val base = start(j)
      val max = settings.dim(j)

      List(base, base + 1, base - 1)
        .filter(i => i >= 0 && i < max)
        .flatMap(i => neighbors(start, i :: neighbor, positive || i != base))
    }
  }

  private def isPosValid(pos: GamePos) = {
    pos.forall(_ >= 0) && (pos, settings.dim).zipped.forall(_ < _)
  }

  private def longestRow(start: GamePos): Row = {
    longestRow(plan.pointer(start)).map(_.pos)
  }

  private def longestRow(pointer: plan.Pointer): List[plan.Pointer] = {
    val mark = pointer.mark
    plan.directions.foldLeft(List.empty[plan.Pointer])((prev, dir) => {
      val currentA = row(mark, pointer, -dir)
      val currentB = row(mark, pointer.move(dir), dir)
      if (currentA.length + currentB.length > prev.length) {
        currentA ::: currentB
      } else {
        prev
      }
    })
  }

  private def row(mark: Player, pointer: plan.Pointer, direction: Int): List[plan.Pointer] = {
    if (mark != pointer.mark) {
      List.empty
    } else {
      pointer :: row(mark, pointer.move(direction), direction)
    }
  }
}
