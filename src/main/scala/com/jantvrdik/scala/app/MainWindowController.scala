package com.jantvrdik.scala.app

import scalafx.event.ActionEvent
import scalafx.scene.canvas.Canvas
import scalafx.scene.control.TextField
import scalafx.scene.input.MouseButton
import scalafx.scene.paint.Color
import scalafxml.core.macros.sfxml

@sfxml
class MainWindowController(
                            private val baseCanvas: Canvas,
                            private val topCanvas: Canvas,
                            private val playerCanvas: Canvas,
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

  val players = Vector(Player(Color.Red), Player(Color.Blue), Player(Color.Green), Player(Color.Black), Player(Color.Magenta))

  def handleStartButtonClick(event: ActionEvent) = {
    val settings = loadSettings
    val model = new GameModel(settings)
    val canvas = new GameCanvas(settings, baseCanvas, topCanvas)

    model.onTurn = (player, pos) => {
      canvas.drawMark(pos, player.color)
      showCurrentPlayer(model)
    }

    model.onVictory = (player, row) => {
      row.foreach(pos => canvas.drawMark(pos, Color.Cyan))
    }

    canvas.onMousePressed = (event, pos) => {
      if (event.button == MouseButton.SECONDARY) {
        canvas.drawNeighbours(model.neighbors(pos))
      }
    }

    canvas.onMouseReleased = (event, pos) => {
      canvas.drawNeighbours(List.empty)
      if (event.button == MouseButton.PRIMARY) {
        model.select(pos)
      }
    }

    canvas.onMouseDragged = (event, pos) => {
      if (event.secondaryButtonDown) {
        canvas.drawNeighbours(model.neighbors(pos))
      }
    }

    canvas.redraw()
    showCurrentPlayer(model)
    topCanvas.requestFocus()
  }

  private def loadDimensions = {
    try {
      var dim = dimensionsInput.text().split("\\*").map(_.trim.toInt).toVector
      if (dim.exists(_ <= 0)) throw new NumberFormatException
      if (dim.length < 2) dim = dim :+ 1
      dim

    } catch {
      case _: NumberFormatException => Vector(7, 7, 7)
    }
  }

  private def loadWinLength(dim: Dimensions) = {
    try {
      Math.max(1, Math.min(dim.max, winLengthInput.text().toInt))

    } catch {
      case _: NumberFormatException => Math.min(dim.max, 5)
    }
  }

  private def loadPlayersCount(players: Vector[Player]) = {
    try {
      Math.max(1, Math.min(players.length, playersCountInput.text().toInt))

    } catch {
      case _: NumberFormatException => Math.min(players.length, 3)
    }
  }

  private def loadSettings = {
    val dim = loadDimensions
    val winLength = loadWinLength(dim)
    val playersCount = loadPlayersCount(players)

    dimensionsInput.text = dim.mkString(" * ")
    winLengthInput.text = winLength.toString
    playersCountInput.text = playersCount.toString

    GameSettings(dim, winLength, players.take(playersCount))
  }

  private def showCurrentPlayer(model: GameModel) {
    val context = playerCanvas.graphicsContext2D
    context.setFill(model.currentPlayer.color)
    context.clearRect(0, 0, 10, 10)
    context.fillOval(0, 0, 10, 10)
  }
}
