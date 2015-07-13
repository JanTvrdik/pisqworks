package com.jantvrdik.scala.app

import scalafx.event.ActionEvent
import scalafx.scene.canvas.Canvas
import scalafx.scene.control.TextField
import scalafx.scene.paint.Color
import scalafxml.core.macros.sfxml

@sfxml
class MainWindowController(
                            private val baseCanvas: Canvas,
                            private val topCanvas: Canvas,
                            private val dimensionsInput: TextField,
                            private val winLengthInput: TextField,
                            private val playersCountInput: TextField
                            ) {

  // init
  val parent = baseCanvas.parent().asInstanceOf[javafx.scene.layout.Region]
  baseCanvas.widthProperty().bind(parent.widthProperty())
  baseCanvas.heightProperty().bind(parent.heightProperty())

  topCanvas.widthProperty().bind(parent.widthProperty())
  topCanvas.heightProperty().bind(parent.heightProperty())

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
    val plan = new GamePlan(settings)
    val model = new GameModel(settings, plan)
    val canvas = new GameCanvas(settings, baseCanvas, topCanvas)

    model.onVictory = (player, row) => row.foreach(pos => canvas.drawMark(pos, Color.Cyan))
    model.onTurn = (player, pos) => canvas.drawMark(pos, player.color)
    canvas.onClick = (pos) => model.select(pos)
    canvas.onHover = (pos) => canvas.drawNeighbours(model.neighbors(pos))
    canvas.onRedraw = () => plan.occupied.foreach(v => canvas.drawMark(v._1, v._2.color))
    canvas.redraw()
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

  private def getWinLength(dim: Dimensions) = {
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

}
