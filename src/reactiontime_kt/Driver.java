package reactiontime_kt;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author Thomas Povinelli
 *         Created 5/11/17
 *         In reactiontime
 */
public class Driver extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        ReactionTime.init(primaryStage);
        Scene scene = ReactionTime.scene();

        primaryStage.setTitle("Reaction Test");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }


}
