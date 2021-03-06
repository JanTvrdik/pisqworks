package com.jantvrdik.scala.app

import scala.collection.mutable.ArrayBuffer

class GamePlan(settings: GameSettings) {

  /** dimension coefficients for transforming to linear linear position */
  private val coefIn = settings.dim
    .map(_ + 2)
    .scanLeft(1)(_ * _)

  /** dimension coefficients for transforming to game position */
  private val coefOut = coefIn
    .reverse
    .toList

  /** direction => jump */
  private val jumps = neighborDirections()
    .toVector
    .map(toLinearJump)
    .filter(_ >= 0)

  /** linear position => player */
  private val gameplan = ArrayBuffer.fill[Player](coefIn.last)(null)

  def directions = {
    1 until jumps.length
  }

  def pointer(pos: GamePos) = {
    Pointer(toLinearPos(pos))
  }

  private def toLinearPos(pos: GamePos): Int = {
    (pos.map(_ + 1), coefIn).zipped.map(_ * _).sum
  }

  private def toLinearJump(dir: Direction): Int = {
    (dir, coefIn).zipped.map(_ * _).sum
  }

  private def toGamePos(pos: Int): GamePos = {
    toGamePos(pos, coefOut).tail.toVector.reverse
  }

  private def toGamePos(pos: Int, coef: List[Int]): List[Int] = {
    coef.length match {
      case 0 => List.empty
      case _ => (pos / coef.head - 1) :: toGamePos(pos % coef.head, coef.tail)
    }
  }

  private def neighborDirections(direction: Direction = List.empty): List[Direction] = {
    settings.dim.length - direction.length match {
      case 0 => List(direction)
      case _ => List(0, +1, -1).flatMap(i => neighborDirections(i :: direction))
    }
  }

  case class Pointer(private val linearPos: Int) {
    def mark = {
      gameplan(linearPos)
    }

    def mark_=(mark: Player) {
      gameplan.update(linearPos, mark)
    }

    def pos = {
      toGamePos(linearPos)
    }

    def free = {
      mark == null
    }

    def move(direction: Int) = {
      val jump = math.signum(direction) * jumps(math.abs(direction))
      Pointer(linearPos + jump)
    }
  }
}
