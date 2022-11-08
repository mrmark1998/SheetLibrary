package com.example.bugtracker;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class UserController {


    @FXML
    private Button btnLogout;

    @FXML
    private Button btnFavorites;

    @FXML
    private Button btnHome;

    @FXML
    private Button btnSearch;

    @FXML
    private Text txStatus;

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
    void showFavorites(ActionEvent event) {

    }

    @FXML
    void showHome(ActionEvent event) {

    }

    @FXML
    void showSearch(ActionEvent event) {

    }

}
