package com.jantvrdik.scala.app

import javafx.scene.canvas.Canvas


class ResizableCanvas extends Canvas {
  override def isResizable: Boolean = {
    true
  }

  override def prefWidth(height: Double): Double = {
    getWidth
  }

  override def prefHeight(width: Double): Double = {
    getHeight
  }
}
