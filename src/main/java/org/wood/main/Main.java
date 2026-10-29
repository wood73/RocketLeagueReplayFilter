package org.wood.main;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.wood.replayfilterscene.ReplayFilterController;


public class Main extends Application {

    static void main(String[] args) {

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ReplayFilter.fxml"));
        Parent root = loader.load();

        ReplayFilterController controller = loader.getController();
        controller.setStage(primaryStage);

        Scene primaryScene = new Scene(root);
        String css = getClass().getResource(
                "/style.css").toExternalForm();
        primaryScene.getStylesheets().add(css);

        primaryStage.setScene(primaryScene);
        primaryStage.show();
        primaryStage.centerOnScreen();

        primaryStage.setTitle("RL Replay Filter");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));

        primaryStage.setResizable(false);
        primaryStage.setScene(primaryScene);
        primaryStage.show();
        primaryStage.centerOnScreen();

        primaryStage.setOnCloseRequest(
                event -> {
                    // Stop JavaFX from automatically closing the window right now
                    event.consume();
                    exitWindow(primaryStage);
                });

    }

    /** Called when the window is closed */
    public void exitWindow(Stage stage) {

        //stage.close();
        //Platform.exit();

        System.exit(0);
    }

}
