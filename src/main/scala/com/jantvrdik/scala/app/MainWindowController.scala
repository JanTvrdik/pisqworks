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
  val dim = Vector(5, 5, 5, 5)
  val winLength = 3

  val playersCount = 2
  val playersColors = mutable.ArraySeq(Color.Red, Color.Blue, Color.Green)
  var currentPlayer = 0

  var gameplan = ArrayBuffer[Int]()


  def handleCanvasClick(event: javafx.scene.input.MouseEvent) = {
    val gridPos = canvasPosToGridPos(event.getX, event.getY)
    val gamePos = gridPosToGamePos(gridPos._1, gridPos._2)

    if (isGamePosValid(gamePos) && getMark(gamePos) < 0) {
      setMark(gamePos, currentPlayer)
      drawMark(gamePos, playersColors(currentPlayer))
      checkVictory(gamePos)
      currentPlayer = (currentPlayer + 1) % playersCount
    }
  }

  def xhandleClick(event: ActionEvent) = {
    val source = event.getSource
    xbutton.text = "blbost"

    val gc = xcanvas.getGraphicsContext2D
    gc.setLineWidth(1)


    val cells = dim.product
    gameplan = ArrayBuffer.fill(cells)(-1)
    drawGrids(0, 0, dim, size)
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
    for (i <- 0 until dim.length) {
      if (i % 2 == 0) {
        x += pos(i) * xx
        xx *= dim(i) + 1
      } else {
        y += pos(i) * yy
        yy *= dim(i) + 1
      }
    }

    (x, y)
  }

  def gridPosToGamePos(x: Int, y: Int): Vector[Int] = {

    val pos = ArrayBuffer[Int]()
    val xx = mutable.Stack[Int](1)
    val yy = mutable.Stack[Int](1)

    for (i <- 0 until dim.length) {
      if (i % 2 == 0) {
        xx.push(xx.top * (dim(i) + 1))
      } else {
        yy.push(yy.top * (dim(i) + 1))
      }
    }

    xx.pop()
    yy.pop()

    var (x2, y2) = (x - 1, y - 1)
    pos.sizeHint(dim.length)

    for (i <- 0 until dim.length) {
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

  def gamePosToLinearPos(pos: Vector[Int]): Int = {
    var linearPos = 0
    var dimCoef = 1
    for (i <- 0 until pos.length) {
      linearPos += pos(i) * dimCoef
      dimCoef *= dim(i)
    }
    linearPos
  }

  def isGamePosValid(pos: Vector[Int]) = {
    pos.forall(_ >= 0) && (pos, dim).zipped.forall(_ < _)
  }

  def checkVictory(start: Vector[Int]) = {
    val longest = findLongestRow(start)
    if (longest.length >= winLength) {
      longest.foreach(pos => drawMark(pos, Color.Chocolate))
    }
  }

  def findLongestRow(start: Vector[Int]): List[Vector[Int]] = {
    findLongestRow(start, Vector.empty)
  }

  def findLongestRow(start: Vector[Int], direction: Vector[Int]): List[Vector[Int]] = {
    if (direction.length == dim.length) {
      if (direction.sum > 0) getSameInRow(getMark(start), start, direction)
      else List.empty

    } else {
      val a = findLongestRow(start, (0 :: direction.toList).toVector)
      val b = findLongestRow(start, (1 :: direction.toList).toVector)

      if (a.length < b.length) b
      else a
    }
  }

  def getSameInRow(mark: Int, start: Vector[Int], direction: Vector[Int]): List[Vector[Int]] = {
    val direction2 = invertVector(direction)
    val start2 = addVector(start, direction2)
    getSameInRowOriented(mark, start, direction) ::: getSameInRowOriented(mark, start2, direction2)
  }

  def getSameInRowOriented(mark: Int, start: Vector[Int], direction: Vector[Int]): List[Vector[Int]] = {
    if (isGamePosValid(start) && mark == getMark(start)) {
      start :: getSameInRowOriented(mark, addVector(start, direction), direction)
    } else {
      List.empty
    }
  }

  def addVector(a: Vector[Int], b: Vector[Int]): Vector[Int] = {
    (a, b).zipped.map(_ + _)
  }

  def invertVector(a: Vector[Int]): Vector[Int] = {
    a.map(-_)
  }

  def getMark(pos: Vector[Int]) = {
    gameplan(gamePosToLinearPos(pos))
  }

  def setMark(pos: Vector[Int], mark: Int) = {
    gameplan.update(gamePosToLinearPos(pos), mark)
  }

  def init(resolver: scalafxml.core.ControllerDependencyResolver) = {

  }

}
