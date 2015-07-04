package com.jantvrdik.scala.app


import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scalafx.Includes._
import scalafx.scene.canvas.Canvas
import scalafx.scene.input.MouseEvent
import scalafx.scene.paint.Color

class GameCanvas(settings: GameSettings, canvas: Canvas, size: Double) {

  var onClick: (Vector[Int]) => Unit = null

  private val context = canvas.graphicsContext2D

  canvas.onMouseClicked = (event: MouseEvent) => {
    val x = (event.getX / size).asInstanceOf[Int]
    val y = (event.getY / size).asInstanceOf[Int]
    onClick(toGamePos(x, y))
  }

  def draw() = {
    drawGrids(0, 0, settings.dim)
  }

  def drawMark(pos: Vector[Int], color: Color) = {
    val (x, y) = toGridPos(pos)
    val markSize = size / 2
    val shift = size / 2 - markSize / 2

    context.setFill(color)
    context.fillOval(x * size + shift, y * size + shift, markSize, markSize)
  }

  private def drawGrids(top: Double, left: Double, dim: Vector[Int]): (Double, Double) = {
    if (dim.length == 2) {
      drawGrid(top, left, dim(0), dim(1), size)

    } else {
      val subDim = dim.dropRight(1)

      val even = dim.length % 2 == 0
      var (totalWidth, totalHeight) = (0.0, 0.0)

      for (i <- 0 until dim.last) {
        if (even) {
          val (w, h) = drawGrids(top + totalHeight + size, left, subDim)
          totalWidth = w
          totalHeight += h + size

        } else {
          val (w, h) = drawGrids(top, left + totalWidth + size, subDim)
          totalWidth += w + size
          totalHeight = h
        }
      }

      (totalWidth, totalHeight)
    }
  }

  private def drawGrid(top: Double, left: Double, width: Int, height: Int, size: Double) = {
    val gc = canvas.getGraphicsContext2D

    for (i <- 0 to width) {
      gc.strokeLine(left + i * size, top, left + i * size, top + height * size)
    }

    for (i <- 0 to height) {
      gc.strokeLine(left, top + i * size, left + width * size, top + i * size)
    }

    (width * size, height * size)
  }

  private def toGridPos(pos: Vector[Int]): (Int, Int) = {
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

  private def toGamePos(x: Int, y: Int): Vector[Int] = {

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

}
