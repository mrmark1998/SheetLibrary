package com.example.bugtracker;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class UserController {

    @FXML
    private Button Download;

    @FXML
    private Button Download1;

    @FXML
    private Button btnFavorite;

    @FXML
    private Button btnFavorite1;

    @FXML
    private Button btnFavorites;

    @FXML
    private Button btnHome;

    @FXML
    private Button btnLogout;

    @FXML
    private Button btnSearch;

    @FXML
    private Button btnUnfavorite;

    @FXML
    private Button btnUnfavorite1;

    @FXML
    private Button btnView;

    @FXML
    private Button btnView1;

    @FXML
    private Button btnX;

    @FXML
    private TableColumn<?, ?> colComposer;

    @FXML
    private TableColumn<?, ?> colComposer1;

    @FXML
    private TableColumn<?, ?> colPages;

    @FXML
    private TableColumn<?, ?> colPages1;

    @FXML
    private TableColumn<?, ?> colTitle;

    @FXML
    private TableColumn<?, ?> colTitle1;

    @FXML
    private TableColumn<?, ?> colYear;

    @FXML
    private TableColumn<?, ?> colYear1;

    @FXML
    private TextField tfSearch;

    @FXML
    private TableView<?> tvFavorites;

    @FXML
    private TableView<?> tvSheets;

    @FXML
    private Text txStatus;

    @FXML
    private VBox vbFavorites;

    @FXML
    private VBox vbHome;

    @FXML
    private VBox vbSearch;

    @FXML
    void addFavorite(ActionEvent event) {

    }

    @FXML
    void downloadSheet(ActionEvent event) {

    }

    @FXML
    void unFavorite(ActionEvent event) {

    }

    @FXML
    void viewSheet(ActionEvent event) {

    }

    @FXML
    void logout(ActionEvent event) throws IOException {
        Stage stage = (Stage) btnLogout.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("login-view.fxml"));
        Stage stage2 = new Stage();
        Scene scene = new Scene(fxmlLoader.load());
        stage2.setScene(scene);

        //if registration successful, goes through timeline of going back to Login Screen-
        Timeline timeline =
                new Timeline(
                        new KeyFrame(
                                Duration.ZERO,
                                e -> this.txStatus.setText("Logging out...")
                        ),
                        new KeyFrame(
                                Duration.seconds(2),
                                e -> {
                                    this.txStatus.setText("Bringing you back to login");
                                }),
                        // second rectangle to black, third to blue
                        new KeyFrame(
                                Duration.seconds(5.0), // duration doesn't stack
                                e -> {
                                    stage.close();
                                    stage2.show();
                                    stage2.setTitle("MySheet Login");
                                })
                );
        timeline.playFromStart();
    }


    @FXML
    void showHome(ActionEvent event) {
        vbHome.setVisible(true);
        vbSearch.setVisible(false);
        vbFavorites.setVisible(false);
    }

    @FXML
    void showSearch(ActionEvent event) {
        vbHome.setVisible(false);
        vbSearch.setVisible(true);
        vbFavorites.setVisible(false);
    }

    @FXML
    void showFavorites(ActionEvent event) {
        vbHome.setVisible(false);
        vbSearch.setVisible(false);
        vbFavorites.setVisible(true);
    }
}
