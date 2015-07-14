package com.jantvrdik.scala.app

import org.junit.{Before, Test}
import org.junit.Assert._
import scalafx.scene.paint.Color

class GamePlanTest {
  var plan: GamePlan = _
  var player: Player = _

  @Before
  def setUp() {
    plan = new GamePlan(new GameSettings(Vector(3, 5, 7), 3, Vector.empty))
    player = new Player(Color.Black)
  }

  @Test
  def testPointer() {
    val positions = List(Vector(0, 0, 0), Vector(2, 4, 6), Vector(1, 2, 3))
    for (pos <- positions) {
      val pointer = plan.pointer(pos)

      assertEquals(pos, pointer.pos)
      assertNull(pointer.mark)

      pointer.mark = player
      assertSame(player, pointer.mark)
    }
  }

  @Test
  def testPointerMove() {
    val positions = List(Vector(0, 0, 0), Vector(2, 4, 6), Vector(1, 2, 3))
    for (pos <- positions) {
      val pointer = plan.pointer(pos)
      for (dir <- plan.directions) {
        assertEquals(pointer, pointer.move(dir).move(-dir))
      }
    }
  }
}
