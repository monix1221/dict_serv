package zad1.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainGuiClient extends Application {

    private static final String APP_TITLE = "TPO 3 - Dictionary";
    private static final String MAIN_VIEW_PATH = "../resources/mainGuiView.fxml";

    private static final int SCENE_WIDTH = 400;
    private static final int SCENE_HEIGHT = 240;

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(MAIN_VIEW_PATH));
        primaryStage.setScene(new Scene(root, SCENE_WIDTH, SCENE_HEIGHT));
        primaryStage.setTitle(APP_TITLE);
        primaryStage.setResizable(true);
        primaryStage.show();
    }
}
