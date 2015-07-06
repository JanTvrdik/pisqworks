package com.jantvrdik.scala.app

import javafx.beans.Observable

import scala.collection.mutable.ArrayBuffer
import scalafx.Includes._
import scalafx.scene.canvas.Canvas
import scalafx.scene.input.MouseEvent
import scalafx.scene.paint.Color

class GameCanvas(settings: GameSettings, canvas: Canvas) {

  var onClick: (GamePos) => Unit = null
  var onRedraw: () => Unit = null

  private val context = canvas.graphicsContext2D

  private val spaces = initSpaces()
  private val sizes = initSizes()
  private var size = 0.0

  canvas.width.addListener((obs: Observable) => redraw())
  canvas.height.addListener((obs: Observable) => redraw())

  canvas.onMouseClicked = (event: MouseEvent) => {
    val x = (event.getX / size).asInstanceOf[Int]
    val y = (event.getY / size).asInstanceOf[Int]
    onClick(toGamePos(GridPos(x, y)))
  }

  def redraw(): Unit = {
    context.clearRect(0, 0, canvas.width(), canvas.height())
    size = initSize()
    drawGrids(GridPos(0, 0), settings.dim)
    onRedraw()
  }

  def drawMark(gamePos: GamePos, color: Color) = {
    val gridPos = toGridPos(gamePos)
    val markSize = size / 2
    val shift = size / 2 - markSize / 2

    context.setFill(color)
    context.fillOval(gridPos.x * size + shift, gridPos.y * size + shift, markSize, markSize)
  }

  private def drawGrids(pos: GridPos, dim: Dimensions): (Int, Int) = {
    if (dim.length == 2) {
      drawGrid(pos, dim)

    } else {
      val subDim = dim.dropRight(1)
      val space = 1 << (subDim.length / 2 - 1)
      var (totalWidth, totalHeight) = (-space, -space)

      for (i <- 0 until dim.last) {
        if (dim.length % 2 == 0) {
          val (w, h) = drawGrids(GridPos(pos.x, pos.y + totalHeight + space), subDim)
          totalWidth = w
          totalHeight += h + space

        } else {
          val (w, h) = drawGrids(GridPos(pos.x + totalWidth + space, pos.y), subDim)
          totalWidth += w + space
          totalHeight = h
        }
      }

      (totalWidth, totalHeight)
    }
  }

  private def drawGrid(pos: GridPos, dim: Dimensions) = {
    assert(dim.length == 2)

    for (i <- 0 to dim.head) {
      context.strokeLine((pos.x + i) * size, pos.y * size, (pos.x + i) * size, (pos.y + dim.last) * size)
    }

    for (i <- 0 to dim.last) {
      context.strokeLine(pos.x * size, (pos.y + i) * size, (pos.x + dim.head) * size, (pos.y + i) * size)
    }

    (dim.head, dim.last)
  }

  private def toGridPos(gamePos: GamePos): GridPos = {
    val gridPos = GridPos(0, 0)

    for (i <- 0 until settings.dim.length) {
      if (i % 2 == 0) {
        gridPos.x += gamePos(i) * sizes(i)
      } else {
        gridPos.y += gamePos(i) * sizes(i)
      }
    }

    gridPos
  }

  private def toGamePos(gridPos: GridPos): GamePos = {
    var gamePos = List[Int]()

    for (i <- settings.dim.length - 1 to 0 by -1) {
      if (i % 2 == 0) {
        gamePos = gridPos.x / sizes(i) :: gamePos
        gridPos.x %= sizes(i)
      } else {
        gamePos = gridPos.y / sizes(i) :: gamePos
        gridPos.y %= sizes(i)
      }
    }

    gamePos.toVector
  }

  private def initSpaces() = {
    Range(0, settings.dim.length + 2).map(i => (1 << (i / 2)) / 2).toVector
  }

  private def initSizes() = {
    val sizes = ArrayBuffer[Int](1, 1)
    for (i <- 0 until settings.dim.length) {
      sizes += sizes(i) * settings.dim(i) + spaces(i + 2) - spaces(i + 2) / 2
    }
    sizes.toVector
  }

  private def initSize() = {
    var limits = (sizes.takeRight(2), spaces.takeRight(2)).zipped.map(_ - _)
    if (settings.dim.length % 2 == 0) limits = limits.reverse
    scala.math.min(canvas.width() / limits.last, canvas.height() / limits.head)
  }

  private case class GridPos(var x: Int, var y: Int)

}
