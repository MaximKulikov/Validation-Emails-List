package ru.maximkulikov.validationemaillist.controllers;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import ru.maximkulikov.validationemaillist.Validator;

public class Uno {

    final FileChooser fileChooser = new FileChooser();

    private Desktop desktop = Desktop.getDesktop();

    private boolean[] ready = {false, false, false, false};

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField taMailFrom, taMailServer, taSubsList, taUnSubsList;

    @FXML
    private Button butProcessData;

    @FXML
    void butChooseSubs(ActionEvent event) {


        File file = chooseFile();


        if (file != null) {
            taSubsList.setText(file.getName());

            Validator.saveProperty("subLIst", file.getAbsolutePath());
            changeReady(2, true);
        }
    }

    @FXML
    void butChooseUnSubs(ActionEvent event) {
        File file = chooseFile();
        if (file != null) {
            taUnSubsList.setText(file.getName());

            Validator.saveProperty("unsubList", file.getAbsolutePath());
            changeReady(3, true);
        }
    }

    @FXML
    void butActionProcessData(ActionEvent event) {
        new Validator().execute();
    }

    private void changeReady(int i, boolean b) {
        ready[i] = b;

        boolean check = true;
        for (boolean b1 : ready) {
            check = check && b1;
        }
        if (check) {
            butProcessData.setDisable(false);
        }

    }

    private File chooseFile() {

        fileChooser.setTitle("View Pictures");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        fileChooser.getExtensionFilters().addAll(

                new FileChooser.ExtensionFilter("TXT", "*.txt")

        );
        return fileChooser.showOpenDialog(taMailFrom.getScene().getWindow());

    }

    @FXML
    void initialize() {

        String mailFrom = Validator.property.getProperty("mailFrom");
        if (mailFrom != null) {
            taMailFrom.setText(mailFrom);
            changeReady(0, true);

        }

        String mxDomain = Validator.property.getProperty("mxDomain");
        if (mxDomain != null) {
            taMailServer.setText(mxDomain);
            changeReady(1, true);
        }


        taMailFrom.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {

                    taMailFrom.setStyle("-fx-background-color: burlywood");
                } else {
                    taMailFrom.setStyle(null);
                    String mailFrom = taMailFrom.getText();

                    if (mailFrom != null && mailFrom != "") {
                        Validator.saveProperty("mailFrom", mailFrom.trim());
                        changeReady(0, true);
                    }
                }
            }
        });

        taMailServer.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {

                    taMailServer.setStyle("-fx-background-color: burlywood");
                } else {

                    taMailServer.setStyle(null);

                    String mxDomain = taMailServer.getText();
                    if (mxDomain != null && mxDomain != "") {
                        Validator.saveProperty("mxDomain", mxDomain.trim());
                        changeReady(1, true);

                    }

                }
            }
        });


    }

    private void openFile(File file) {
        try {
            desktop.open(file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
