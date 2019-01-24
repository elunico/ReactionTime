/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reactiontime_kt

import javafx.application.Platform
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.stage.Stage
import javafx.stage.WindowEvent
import java.util.*

/**
 * @author thomaspovinelli
 */


class ReactionTime(private var primaryStage: Stage) {
  private val average = 0.0
  private val averageLabel = Label("Average: $average")
  private val values = ArrayList<Double>()
  private val scoreStage = Stage()
  private val textArea = TextArea()
  private var btn: Button = Button()
  private var clearButton: Button = Button()
  private val scoreBox = VBox(Label("Previous Reactions (ms):"), textArea, averageLabel, clearButton)
  private var t = Timer()
  private var earlyStart = false
  private var cheater = false
  var scene: Scene
    private set

  fun show() {
    primaryStage.show()
  }

  private fun buildUp() {
    textArea.isEditable = false

    primaryStage.addEventHandler(KeyEvent.KEY_PRESSED) { event: KeyEvent ->
      if (event.code == KeyCode.C) {
        cheater = !cheater
      }
    }

    scoreBox.spacing = 10.0
    scoreBox.setPrefSize(200.0, 350.0)
    scoreBox.padding = Insets(10.0)
    scoreStage.x = 10.0
    scoreStage.y = 100.0

    scoreStage.scene = Scene(scoreBox)
    scoreStage.show()

    primaryStage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST) {
      scoreStage.close()
      t.purge()
      t.cancel()
    }

    scoreStage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST) {
      primaryStage.close()
      t.purge()
      t.cancel()
    }

    btn.prefWidth = 300.0
    btn.prefHeight = 250.0
    btn.text = "Click and hold to begin"
    btn.style = "-fx-base: #CC0000"
    btn.setOnMousePressed { timerStart() }
    btn.onMouseReleased = NoAction

    clearButton.text = "Clear"
    clearButton.setOnMouseClicked { clearAction() }

    val root = StackPane()
    root.children.add(btn)

    scene = Scene(root, 300.0, 250.0)

    primaryStage.title = "Reaction Test"
    primaryStage.scene = scene
  }

  private val random = Random()

  private fun timerStart() {
    earlyStart = false
    btn.text = "HOLD..."
    btn.style = "-fx-base: #CC0000"
    Platform.runLater {
      btn.onMouseReleased = EventHandler<MouseEvent> {
        t.purge()
        t.cancel()
        t = Timer()
        primaryStage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST) {
          scoreStage.close()
          t.purge()
          t.cancel()
        }
        earlyStart = true
        btn.text = "TOO SOON!\nHold to try again"
        btn.style = "-fx-base: #0000CC"
        btn.setOnMousePressed { timerStart() }
      }
      btn.onMousePressed = NoAction
    }
    if (!earlyStart) {
      val task = object : TimerTask() {
        override fun run() {

          Platform.runLater {
            val ctime = System.currentTimeMillis()
            btn.style = "-fx-base: #00CC00"
            btn.text = "RELEASE!"
            btn.setOnMouseReleased {
              var elapse = System.currentTimeMillis() - ctime
              // more accurate then checking first
              elapse = if (cheater) 1 else elapse
              btn.text = "Reaction time: $elapse\nHold to begin again."
              btn.style = "-fx-base: #CC0000"
              btn.setOnMousePressed { timerStart() }
              values.add(elapse.toDouble())
              averageLabel.text = "Average: " + String.format("%.3f",
                                                              values.average())
              textArea.text = textArea.text + "\n" + elapse
            }
            btn.onMousePressed = NoAction
          }
        }
      }
      t.schedule(task, random.nextInt(1600) + 600L)
    }
  }

  init {
    scene = Scene(HBox())
    buildUp()
  }

  private fun clearAction() {
    textArea.text = ""
    values.clear()
    averageLabel.text = ""
  }
}
