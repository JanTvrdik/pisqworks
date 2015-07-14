package com.jantvrdik.scala.app

import javafx.beans.Observable

import scala.collection.mutable.ArrayBuffer
import scalafx.Includes._
import scalafx.scene.canvas.Canvas
import scalafx.scene.input.MouseEvent
import scalafx.scene.paint.Color

class GameCanvas(settings: GameSettings, baseCanvas: Canvas, topCanvas: Canvas) {
  type MouseListener = (MouseEvent, GamePos) => Unit

  var onMousePressed: MouseListener = null
  var onMouseReleased: MouseListener = null
  var onMouseDragged: MouseListener = null

  private val baseContext = baseCanvas.graphicsContext2D
  private val topContext = topCanvas.graphicsContext2D

  private val spaces = initSpaces()
  private val sizes = initSizes()
  private var size = 0.0

  private var lastPos: GamePos = null

  /** list of placed marks */
  private var marks = List.empty[Mark]

  val canvasSizeListener = (obs: Observable) => redraw()
  baseCanvas.width.addListener(canvasSizeListener)
  baseCanvas.height.addListener(canvasSizeListener)
  topCanvas.width.addListener(canvasSizeListener)
  topCanvas.height.addListener(canvasSizeListener)

  topCanvas.onMousePressed = (event: MouseEvent) => onMousePressed(event, toGamePos(event))
  topCanvas.onMouseReleased = (event: MouseEvent) => onMouseReleased(event, toGamePos(event))
  topCanvas.onMouseDragged = (event: MouseEvent) => {
    val currentPos = toGamePos(event)
    if (!currentPos.equals(lastPos)) {
      lastPos = currentPos
      onMouseDragged(event, currentPos)
    }
  }

  def redraw() {
    clear(baseCanvas)
    clear(topCanvas)

    baseContext.clearRect(0, 0, baseCanvas.width(), baseCanvas.height())
    size = initSize()
    drawGrids(GridPos(0, 0), settings.dim)

    marks.foreach(mark => drawMark(mark))
  }

  def drawMark(gamePos: GamePos, color: Color) {
    val mark = Mark(toGridPos(gamePos), color)
    drawMark(mark)
    marks = mark :: marks
  }

  def drawNeighbours(neighbours: List[GamePos]) {
    clear(topCanvas)
    neighbours.foreach(gamePos => {
      val gridPos = toGridPos(gamePos)
      val markSize = size * 0.6
      val shift = size / 2 - markSize / 2

      topContext.setStroke(Color.Brown)
      topContext.strokeOval(gridPos.x * size + shift, gridPos.y * size + shift, markSize, markSize)
    })
  }

  private def drawMark(mark: Mark) = {
    val markSize = size / 2
    val shift = size / 2 - markSize / 2
    baseContext.setFill(mark.color)
    baseContext.fillOval(mark.pos.x * size + shift, mark.pos.y * size + shift, markSize, markSize)
  }

  private def clear(canvas: Canvas) {
    canvas.graphicsContext2D.clearRect(0, 0, canvas.width(), canvas.height())
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
      baseContext.strokeLine((pos.x + i) * size, pos.y * size, (pos.x + i) * size, (pos.y + dim.last) * size)
    }

    for (i <- 0 to dim.last) {
      baseContext.strokeLine(pos.x * size, (pos.y + i) * size, (pos.x + dim.head) * size, (pos.y + i) * size)
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

  private def toGamePos(event: MouseEvent): GamePos = {
    val x = (event.getX / size).asInstanceOf[Int]
    val y = (event.getY / size).asInstanceOf[Int]
    toGamePos(GridPos(x, y))
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
    scala.math.min(baseCanvas.width() / limits.last, baseCanvas.height() / limits.head)
  }

  private case class GridPos(var x: Int, var y: Int)

  private case class Mark(pos: GridPos, color: Color)

}
