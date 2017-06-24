package ru.maximkulikov.validationemaillist;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Validation-List-of-Emails
 * Created by maxim on 24.06.2017.
 */
public class Main extends Application {

    public static void main(String[] args) {
        System.setProperty("java.net.preferIPv4Stack", "true");

        Validator.loadConfig();
        if (System.getProperty("java.runtime.name").startsWith("Java(TM)") &&
                Double.parseDouble(System.getProperty("java.specification.version")) >= 1.8d) {
            launch();
        } else {

            new Validator().execute();
        }

    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/resources/fxml/uno.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("Validation-Emails-List");
        stage.setResizable(true);
        stage.setScene(scene);
        stage.show();

    }
}
