package com.jantvrdik.scala.app

import scalafx.event.ActionEvent
import scalafx.scene.canvas.Canvas
import scalafx.scene.control.TextField
import scalafx.scene.paint.Color
import scalafxml.core.macros.sfxml

@sfxml
class MainWindowController(
                            private val xcanvas: Canvas,
                            private val dimensionsInput: TextField,
                            private val winLengthInput: TextField,
                            private val playersCountInput: TextField
                            ) {

  def handleStartButtonClick(event: ActionEvent) = {
    val players = Vector(Player(Color.Red), Player(Color.Blue), Player(Color.Green), Player(Color.Black), Player(Color.Magenta))

    // load values
    val dim = getDimensions
    val winLength = getWinLength(dim)
    val playersCount = getPlayersCount(players)

    // normalize value in inputs
    dimensionsInput.text = dim.mkString(" * ")
    winLengthInput.text = winLength.toString
    playersCountInput.text = playersCount.toString

    val settings = GameSettings(dim, winLength, players.take(playersCount))
    val model = new GameModel(settings, new GamePlan(settings))
    val canvas = new GameCanvas(settings, xcanvas)

    model.onVictory = (player, row) => {
      row.foreach(pos => canvas.drawMark(pos, Color.Cyan))
    }

    model.onTurn = (player, pos) => {
      canvas.drawMark(pos, player.color)
    }

    canvas.onClick = (pos) => {
      model.select(pos)
    }

    canvas.draw()
  }

  private def getDimensions = {
    try {
      var dim = dimensionsInput.text().split("\\*").map(_.trim.toInt).toVector
      if (dim.exists(_ <= 0)) throw new NumberFormatException
      if (dim.length < 2) dim = dim :+ 1
      dim

    } catch {
      case _: NumberFormatException => Vector(7, 7, 7)
    }
  }

  private def getWinLength(dim: Vector[Int]) = {
    try {
      Math.max(1, Math.min(dim.max, winLengthInput.text().toInt))

    } catch {
      case _: NumberFormatException => Math.min(dim.max, 5)
    }
  }

  private def getPlayersCount(players: Vector[Player]) = {
    try {
      Math.max(1, Math.min(players.length, playersCountInput.text().toInt))

    } catch {
      case _: NumberFormatException => Math.min(players.length, 3)
    }
  }

  def init(resolver: scalafxml.core.ControllerDependencyResolver) = {

  }

}
