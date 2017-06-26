package ru.maximkulikov.validationemaillist.controllers;


import java.io.File;
import java.io.IOException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import ru.maximkulikov.validationemaillist.C;
import ru.maximkulikov.validationemaillist.Validator;

public class Uno {

    private final FileChooser fileChooser = new FileChooser();

    private java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

    private boolean[] ready = {false, false, false, false};

    @FXML
    private TextField taMailFrom, taMailServer, taSubsList, taUnSubsList;

    @FXML
    private Button butProcessData, butGoodEmails, butBadEmails;

    @FXML
    private ProgressBar progress;

    @FXML
    private VBox vbProgress;

    @FXML
    void butActionProcessData(ActionEvent event) {
        butProcessData.setDisable(true);
        butProcessData.setText("Progress...");
        progress.setVisible(true);
        new Thread(() -> new Validator().execute()).start();
    }

    @FXML
    void butChooseSubs(ActionEvent event) {


        File file = chooseFile();


        if (file != null) {
            taSubsList.setText(file.getName());

            Validator.saveProperty(C.SUB_LIST, file.getAbsolutePath());
            changeReady(2, true);
        }
    }

    @FXML
    void butChooseUnSubs(ActionEvent event) {
        File file = chooseFile();
        if (file != null) {
            taUnSubsList.setText(file.getName());

            Validator.saveProperty(C.UNSUB_LIST, file.getAbsolutePath());
            changeReady(3, true);
        }
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

        fileChooser.setTitle("Файл с базой");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        fileChooser.getExtensionFilters().addAll(

                new FileChooser.ExtensionFilter("TXT", "*.txt"),
                new FileChooser.ExtensionFilter("CVS", "*.csv")


        );
        return fileChooser.showOpenDialog(taMailFrom.getScene().getWindow());

    }

    public VBox getVbProgress() {
        return vbProgress;
    }

    @FXML
    void initialize() {

        Validator.gui = this;

        butProcessData.setDisable(false);

        String mailFrom = Validator.property.getProperty(C.MAIL_FROM);
        if (mailFrom != null) {
            taMailFrom.setText(mailFrom);
            changeReady(0, true);

        }

        String mxDomain = Validator.property.getProperty(C.MX_DOMAIN);
        if (mxDomain != null) {
            taMailServer.setText(mxDomain);
            changeReady(1, true);
        }


        taMailFrom.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {

                taMailFrom.setStyle("-fx-background-color: burlywood");
            } else {
                taMailFrom.setStyle(null);
                String mailFrom1 = taMailFrom.getText();

                if (mailFrom1 != null && !mailFrom1.equals("")) {
                    Validator.saveProperty(C.MAIL_FROM, mailFrom1.trim());
                    changeReady(0, true);
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
                    if (mxDomain != null && !mxDomain.equals("")) {
                        Validator.saveProperty(C.MX_DOMAIN, mxDomain.trim());
                        changeReady(1, true);

                    }

                }
            }
        });


    }


    public void setProgress(double v) {
        progress.setProgress(v);
    }

    public void showResults(File goodFile, File badFile) {

        butGoodEmails.setOnAction(event -> {

            try {
                desktop.open(goodFile);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        butBadEmails.setOnAction(event -> {

            try {
                desktop.open(badFile);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        progress.setVisible(false);
        butGoodEmails.setVisible(true);
        butBadEmails.setVisible(true);
    }
}
