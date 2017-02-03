/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reactiontime;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author thomaspovinelli
 */
public class ReactionTime extends Application {

    private final double average = 0.0;
    private final Label averageLabel = new Label("Average: " + average);
    private final ArrayList<Double> values = new ArrayList();
    private final Stage scoreStage = new Stage();
    private final TextArea textArea = new TextArea();
    private final VBox scoreBox = new VBox(
      new Label("Previous Reactions (ms):"), textArea, averageLabel);
    private Button btn;
    private Timer t = new Timer();
    private Stage ps;
    private boolean earlyStart = false;
    private boolean cheater = false;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    public double averageOf(ArrayList<Double> list) {
        double sum = 0;
        for (Double d : list) {
            sum += d;
        }
        return sum / list.size();
    }

    @Override
    public void start(Stage primaryStage) {
        ps = primaryStage;
        textArea.setEditable(false);

        ps.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            if (event.getCode() == KeyCode.C) {
                cheater = !cheater;
            }
        });

        scoreBox.setSpacing(10);
        scoreBox.setPrefSize(200, 350);
        scoreBox.setPadding(new Insets(10));
        scoreStage.setX(10);
        scoreStage.setY(100);

        scoreStage.setScene(new Scene(scoreBox));
        scoreStage.show();

        primaryStage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST,
          (WindowEvent e) -> {
              scoreStage.close();
              t.purge();
              t.cancel();
          });

        scoreStage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST,
          (WindowEvent e) -> {
              primaryStage.close();
              t.purge();
              t.cancel();
          });

        btn = new Button();
        btn.setPrefWidth(300);
        btn.setPrefHeight(250);
        btn.setText("Click and hold to begin");
        btn.setStyle("-fx-base: #CC0000");
        btn.setOnMousePressed(e -> timerStart());
        btn.setOnMouseReleased(new NoActionHandler());

        StackPane root = new StackPane();
        root.getChildren().add(btn);

        Scene scene = new Scene(root, 300, 250);

        primaryStage.setTitle("Reaction Test");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void timerStart() {
        earlyStart = false;
        btn.setText("HOLD...");
        btn.setStyle("-fx-base: #CC0000");
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                btn.setOnMouseReleased(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        t.purge();
                        t.cancel();
                        t = new Timer();
                        ps.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST,
                          (WindowEvent e) -> {
                              scoreStage.close();
                              t.purge();
                              t.cancel();
                          });
                        earlyStart = true;
                        btn.setText("TOO SOON!\nHold to try again");
                        btn.setStyle("-fx-base: #0000CC");
                        btn.setOnMousePressed(e -> timerStart());
                    }

                });
                btn.setOnMousePressed(new NoActionHandler());
            }

        });
        if (!earlyStart) {
            t.schedule(new TimerTask() {
                @Override
                public void run() {

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {

                            long ctime = System.currentTimeMillis();
                            btn.setStyle("-fx-base: #00CC00");
                            btn.setText("RELEASE!");
                            btn.setOnMouseReleased(
                              new EventHandler<MouseEvent>() {
                                  @Override
                                  public void handle(MouseEvent event) {
                                      long elapse = cheater ?
                                                    1 :
                                                    System.currentTimeMillis() - ctime;
                                      btn.setText(
                                        "Reaction time: " + elapse + "\nHold to begin again.");
                                      btn.setStyle("-fx-base: #CC0000");
                                      btn.setOnMousePressed(e -> timerStart());
                                      values.add((double) elapse);
                                      averageLabel.setText(
                                        "Average: " + String.format("%.3f",
                                          averageOf(values)));
                                      textArea.setText(
                                        textArea.getText() + "\n" + elapse);
                                  }
                              });
                            btn.setOnMousePressed(new NoActionHandler());
                        }
                    });
                }
            }, (new Random()).nextInt(1200) + 800);
        }

    }

    private static final class NoActionHandler<T extends Event>
      implements EventHandler<T> {

        @Override
        public void handle(T event) {
            /* do nothing */
        }
    }

}
