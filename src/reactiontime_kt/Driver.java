package reactiontime_kt;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * @author Thomas Povinelli
 *         Created 5/11/17
 *         In reactiontime
 */
public class Driver extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ReactionTime rt = new ReactionTime(primaryStage);
        rt.show();
    }

}
