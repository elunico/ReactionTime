/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reactiontime;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javafx.stage.WindowEvent;

/**
 *
 * @author thomaspovinelli
 */
public class ReactionTime extends Application {

    Button btn;
    Stage scoreStage = new Stage();
    VBox scoreBox = new VBox(new Label("Previous Reactions (ms):"));
    Timer t = new Timer();
    Stage ps;

    boolean earlyStart = false;

    EventType<MouseEvent> beforeHold = MouseEvent.MOUSE_PRESSED;
    EventType<MouseEvent> whileHolding = MouseEvent.MOUSE_RELEASED;

    @Override
    public void start(Stage primaryStage) {
        ps = primaryStage;

        scoreBox.setSpacing(10);
        scoreBox.setPrefSize(200, 350);
        scoreBox.setPadding(new Insets(10));
        scoreStage.setX(10);
        scoreStage.setY(100);

        scoreStage.setScene(new Scene(scoreBox));
        scoreStage.show();

        primaryStage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
                scoreStage.close();
                t.purge();
                t.cancel();

            }
        });

        btn = new Button();
        btn.setPrefWidth(300);
        btn.setPrefHeight(250);
        btn.setText("Click and hold to begin");
        btn.setStyle("-fx-base: #CC0000");
        btn.setOnMousePressed(e -> timerStart());
        btn.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                // Silent. Only here to remove the event handler action
                // previously delegated to the action
            }

        });

        StackPane root = new StackPane();
        root.getChildren().add(btn);

        Scene scene = new Scene(root, 300, 250);

        primaryStage.setTitle("Reaction Test");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
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
                        ps.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, new EventHandler<WindowEvent>() {
                            @Override
                            public void handle(WindowEvent e) {
                                scoreStage.close();
                                t.purge();
                                t.cancel();

                            }
                        });
                        earlyStart = true;
                        btn.setText("TOO SOON!\nHold to try again");
                        btn.setStyle("-fx-base: #0000CC");
                        btn.setOnMousePressed(e -> timerStart());
                    }

                });
                btn.setOnMousePressed(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        // Silent. Only here to remove the event handler action
                        // previously delegated to the action
                    }

                });
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
                            btn.setOnMouseReleased(new EventHandler<MouseEvent>() {
                                @Override
                                public void handle(MouseEvent event) {
                                    long elapse = System.currentTimeMillis() - ctime;
                                    btn.setText("Reaction time: " + elapse + "\nHold to begin again.");
                                    btn.setStyle("-fx-base: #CC0000");
                                    btn.setOnMousePressed(e -> timerStart());
                                    scoreBox.getChildren().add(new Label(elapse + "\n"));
                                }
                            });
                            btn.setOnMousePressed(new EventHandler<MouseEvent>() {
                                @Override
                                public void handle(MouseEvent event) {
                                    // Silent. Only here to remove the event handler action
                                    // previously delegated to the action
                                }

                            });
                        }
                    });
                }
            }, (new Random()).nextInt(1200) + 800);
        }

    }

}
