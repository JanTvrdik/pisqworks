package com.jantvrdik.scala.app

import org.junit.Assert._
import org.junit.Test

import scalafx.scene.paint.Color

class GameModelTest {
  var model: GameModel = _
  var winningRow: Row = _

  @Test
  def testVictory() {
    val rows = List(List(Vector(0, 0, 0), Vector(0, 0, 1), Vector(0, 0, 2)))

    for (row <- rows) {
      val player = new Player(Color.Black)
      val settings: GameSettings = new GameSettings(Vector(3, 5, 7), 3, Vector(player))
      model = new GameModel(settings)
      model.onTurn = (player, pos) => Unit
      model.onVictory = (player, row) => winningRow = row
      winningRow = List.empty

      for (pos <- row) {
        assertTrue(model.select(pos))
      }

      assertEquals(row.length, winningRow.length)
    }
  }
}
