package de.bht.fpa.mail.gruppe6.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 *
 * @author Nessi
 */
public class HistoryController implements Initializable {

    @FXML
    private Button ok;
    @FXML
    private Button cancel;
    @FXML
    private ListView<String> list;

    private static ObservableList<String> data;
    private String message = "No base directories in history.";
    private AppController app;

    HistoryController(AppController aThis) {
        app = aThis;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ok.setDisable(true);
        ok.setOnAction((eve) -> ok(eve));
        cancel.setOnAction((event) -> exit(event));
        inItList();
    }

    private void inItList() {
        ArrayList<String> historyData = app.getHistory();
        data = FXCollections.observableArrayList(historyData);
          if (historyData.isEmpty()) {
            data.add(message);
        } else {
        historyData.remove(message);
        ObservableList<String> data = (ObservableList<String>) list.getSelectionModel().getSelectedItems();
        data.addListener((ListChangeListener.Change<? extends String> c) -> ok.setDisable(false));

        }
        list.setItems(data);
    }

    public void ok(ActionEvent e) {
        if (!list.getItems().contains(message)) {
            String newFolder = list.getSelectionModel().getSelectedItem();
            app.historyAction(newFolder);
            Stage stage = (Stage) ok.getScene().getWindow();
            stage.close();
        }
    }

    public void exit(ActionEvent e) {
        Stage historyStage = (Stage) cancel.getScene().getWindow();
        historyStage.close();
    }
}
