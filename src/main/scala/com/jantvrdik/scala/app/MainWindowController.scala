package com.jantvrdik.scala.app

import scalafx.event.ActionEvent
import scalafx.scene.canvas.Canvas
import scalafx.scene.control.Button
import scalafx.scene.paint.Color
import scalafxml.core.macros.sfxml

@sfxml
class MainWindowController(private val xbutton: Button, private val xcanvas: Canvas) {

  var settings: GameSettings = null
  var model: GameModel = null
  var canvas: GameCanvas = null


  def xhandleClick(event: ActionEvent) = {
    xbutton.text = "clicked!"

    val players = Vector(Player(Color.Red), Player(Color.Blue))
    val dim = Vector(5, 5, 5, 5)
    val winLength = 3
    val size = 20.0

    settings = GameSettings(dim, winLength, players)

    model = new GameModel(settings, new GamePlan(settings))

    model.onVictory = (player, row) => {
      row.foreach(pos => canvas.drawMark(pos, Color.Cyan))
    }

    model.onTurn = (player, pos) => {
      canvas.drawMark(pos, player.color)
    }

    canvas = new GameCanvas(settings, xcanvas, size)

    canvas.onClick = (pos) => {
      model.select(pos)
    }

    canvas.draw()
  }



  def init(resolver: scalafxml.core.ControllerDependencyResolver) = {

  }

}
