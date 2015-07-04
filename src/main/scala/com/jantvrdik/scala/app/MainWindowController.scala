package com.jantvrdik.scala.app

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scalafx.event.ActionEvent
import scalafx.scene.canvas.Canvas
import scalafx.scene.control.Button
import scalafx.scene.paint.Color
import scalafxml.core.macros.sfxml

@sfxml
class MainWindowController(private val xbutton: Button, private val xcanvas: Canvas) {

  val size = 20.0
  var settings: GameSettings = null
  var model: GameModel = null


  def handleCanvasClick(event: javafx.scene.input.MouseEvent) = {
    val gridPos = canvasPosToGridPos(event.getX, event.getY)
    val gamePos = gridPosToGamePos(gridPos._1, gridPos._2)

    model.select(gamePos)
  }

  def xhandleClick(event: ActionEvent) = {
    xbutton.text = "clicked!"

    val players = Vector(Player(Color.Red), Player(Color.Blue))
    settings = GameSettings(Vector(5, 5, 5, 5), 3, players)
    val plan = new GamePlan(settings)

    model = new GameModel(settings, plan)

    model.onVictory = (player, row) => {
      row.foreach(pos => drawMark(pos, Color.Cyan))
    }

    model.onTurn = (player, pos) => {
      drawMark(pos, player.color)
    }

    drawGrids(0, 0, settings.dim, size)
  }

  def drawGrids(top: Double, left: Double, dim: Vector[Int], size: Double): (Double, Double) = {
    if (dim.length == 2) {
      drawGrid(top, left, dim(0), dim(1), size)

    } else {
      val subDim = dim.dropRight(1)

      val even = dim.length % 2 == 0
      var (totalWidth, totalHeight) = (0.0, 0.0)

      for (i <- 0 until dim.last) {
        if (even) {
          val (w, h) = drawGrids(top + totalHeight + size, left, subDim, size)
          totalWidth = w
          totalHeight += h + size

        } else {
          val (w, h) = drawGrids(top, left + totalWidth + size, subDim, size)
          totalWidth += w + size
          totalHeight = h
        }
      }

      (totalWidth, totalHeight)
    }
  }

  def drawGrid(top: Double, left: Double, width: Int, height: Int, size: Double) = {
    val gc = xcanvas.getGraphicsContext2D

    for (i <- 0 to width) {
      gc.strokeLine(left + i * size, top, left + i * size, top + height * size)
    }

    for (i <- 0 to height) {
      gc.strokeLine(left, top + i * size, left + width * size, top + i * size)
    }

    (width * size, height * size)
  }

  def drawMark(pos: Vector[Int], color: Color) = {
    val gc = xcanvas.getGraphicsContext2D
    gc.setFill(color)

    val (x, y) = gamePosCanvasPos(pos)
    val markSize = size / 2
    val shift = size / 2 - markSize / 2
    gc.fillOval(x + shift, y + shift, markSize, markSize)
  }

  def gamePosCanvasPos(pos: Vector[Int]): (Double, Double) = {
    val (x, y) = toGridPos(pos)
    (x * size, y * size)
  }

  def toGridPos(pos: Vector[Int]): (Int, Int) = {

    var (x, y) = (1, 1)
    var (xx, yy) = (1, 1)
    for (i <- 0 until settings.dim.length) {
      if (i % 2 == 0) {
        x += pos(i) * xx
        xx *= settings.dim(i) + 1
      } else {
        y += pos(i) * yy
        yy *= settings.dim(i) + 1
      }
    }

    (x, y)
  }

  def gridPosToGamePos(x: Int, y: Int): Vector[Int] = {

    val pos = ArrayBuffer[Int]()
    val xx = mutable.Stack[Int](1)
    val yy = mutable.Stack[Int](1)

    for (i <- 0 until settings.dim.length) {
      if (i % 2 == 0) {
        xx.push(xx.top * (settings.dim(i) + 1))
      } else {
        yy.push(yy.top * (settings.dim(i) + 1))
      }
    }

    xx.pop()
    yy.pop()

    var (x2, y2) = (x - 1, y - 1)
    pos.sizeHint(settings.dim.length)

    for (i <- 0 until settings.dim.length) {
      if (i % 2 == 1) {
        pos.insert(i, x2 / xx.top)
        x2 %= xx.top
        xx.pop()

      } else {
        pos.insert(i, y2 / yy.top)
        y2 %= yy.top
        yy.pop()
      }
    }

    pos.toVector.reverse
  }

  def canvasPosToGridPos(x: Double, y: Double): (Int, Int) = {
    ((x / size).asInstanceOf[Int], (y / size).asInstanceOf[Int])
  }

  def init(resolver: scalafxml.core.ControllerDependencyResolver) = {

  }

}
