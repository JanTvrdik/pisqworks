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

  private def drawGrids(top: Int, left: Int, dim: Vector[Int]): (Int, Int) = {
    if (dim.length == 2) {
      drawGrid(top, left, dim)

    } else {
      val subDim = dim.dropRight(1)
      val space = 1 << (subDim.length / 2 - 1)
      var (totalWidth, totalHeight) = (-space, -space)

      for (i <- 0 until dim.last) {
        if (dim.length % 2 == 0) {
          val (w, h) = drawGrids(top + totalHeight + space, left, subDim)
          totalWidth = w
          totalHeight += h + space

        } else {
          val (w, h) = drawGrids(top, left + totalWidth + space, subDim)
          totalWidth += w + space
          totalHeight = h
        }
      }

      (totalWidth, totalHeight)
    }
  }

  private def drawGrid(top: Int, left: Int, dim: Vector[Int]) = {
    assert(dim.length == 2)

    for (i <- 0 to dim.head) {
      context.strokeLine((left + i) * size, top * size, (left + i) * size, (top + dim.last) * size)
    }

    for (i <- 0 to dim.last) {
      context.strokeLine(left * size, (top + i) * size, (left + dim.head) * size, (top + i) * size)
    }

    (dim.head, dim.last)
  }

  private def toGridPos(pos: Vector[Int]): (Int, Int) = {
    var (x, y) = (0, 0)
    var (xx, yy) = (1, 1)

    for (i <- 0 until settings.dim.length) {
      val space = 1 << (i / 2)
      val shift = space - space / 2
      if (i % 2 == 0) {
        x += pos(i) * xx
        xx = xx * settings.dim(i) + shift
      } else {
        y += pos(i) * yy
        yy = yy * settings.dim(i) + shift
      }
    }

    (x, y)
  }

  private def toGamePos(x: Int, y: Int): Vector[Int] = {

    val pos = ArrayBuffer[Int]()
    val xx = mutable.Stack[Int](1)
    val yy = mutable.Stack[Int](1)

    for (i <- 0 until settings.dim.length) {
      val space = 1 << (i / 2)
      val shift = space - space / 2
      if (i % 2 == 0) {
        xx.push(xx.top * settings.dim(i) + shift)
      } else {
        yy.push(yy.top * settings.dim(i) + shift)
      }
    }

    var (x2, y2) = (x, y)
    pos.sizeHint(settings.dim.length)

    for (i <- 0 until settings.dim.length) {
      if (i % 2 == (settings.dim.length + 1) % 2) {
        xx.pop()
        pos.insert(i, x2 / xx.top)
        x2 %= xx.top

      } else {
        yy.pop()
        pos.insert(i, y2 / yy.top)
        y2 %= yy.top
      }
    }

    pos.toVector.reverse
  }

}
