package com.jantvrdik.scala.app

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafxml.core.{NoDependencyResolver, FXMLView}

object MainWindow extends JFXApp {
  val resource = getClass.getResource("MainWindow.fxml")
  val root = FXMLView(resource, NoDependencyResolver)
  stage = new PrimaryStage {
    title = "Scala Game!"
    scene = new Scene(root)
  }
}
