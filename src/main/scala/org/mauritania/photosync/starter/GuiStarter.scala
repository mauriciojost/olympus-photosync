package org.mauritania.photosync.starter

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.effect.DropShadow
import scalafx.scene.layout.HBox
import scalafx.scene.paint.Color._
import scalafx.scene.paint._
import scalafx.scene.text.Text

object GuiStarter extends JFXApp {
  val scala = new Text {
    text = "Scala"
    style = "-fx-font: normal bold 100pt sans-serif"
    fill = new LinearGradient(
      endX = 0,
      stops = Stops(Red, DarkRed))
  }
  val fx = new Text {
    text = "FX"
    style = "-fx-font: italic bold 100pt sans-serif"
    fill = new LinearGradient(
      endX = 0,
      stops = Stops(White, DarkGray)
    )
    effect = new DropShadow {
      color = DarkGray
      radius = 15
      spread = 0.25
    }
  }

  stage = new PrimaryStage {
    //    initStyle(StageStyle.Unified)
    title = "ScalaFX Hello World"
    scene = new Scene {
      fill = Color.rgb(38, 38, 38)
      content = new HBox {
        padding = Insets(50, 80, 50, 80)
        children = Seq(
          scala,
          fx

        )
      }
    }

  }
}
