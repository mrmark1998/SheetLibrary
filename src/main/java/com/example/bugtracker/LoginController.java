package com.example.bugtracker;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;


public class LoginController {

    @FXML
    private Button btnLogin;

    @FXML
    private Button btnRegister;

    @FXML
    private TextField tfPassword;

    @FXML
    private TextField tfUser;

    @FXML
    private Text txAbout;

    @FXML
    private Text txAdmin;

    @FXML
    private Text txStatus;


    @FXML
    void showRegister() throws IOException {
        Stage stage = (Stage) btnRegister.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("register-view.fxml"));
        Stage stage2 = new Stage();
        Scene scene = new Scene(fxmlLoader.load());
        stage2.setScene(scene);
        stage.close();
        stage2.show();
        stage2.setTitle("MySheet Registration");
    }

    @FXML
    public void login(ActionEvent actionEvent) throws IOException {

        //if username exists in users then
        //if select password from users where username = "tfUsername" == tfpassword
        //THEN execute code:

        Stage stage = (Stage) btnLogin.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));
        Stage stage2 = new Stage();
        Scene scene = new Scene(fxmlLoader.load());
        stage2.setScene(scene);


        //if login successful, goes through timeline of loading the library-
        Timeline timeline =
                new Timeline(
                        new KeyFrame(
                                Duration.ZERO,
                                e -> this.txStatus.setText("Logging in...")
                                ),
                        new KeyFrame(
                                Duration.seconds(2),
                                e -> {
                                    this.txStatus.setText("Success! Loading Library..");
                                }),
                        // second rectangle to black, third to blue
                        new KeyFrame(
                                Duration.seconds(5.0), // duration doesn't stack
                                e -> {
                                    stage.close();
                                    stage2.show();
                                    stage2.setTitle("MySheet Library");
                                })
                        );
        timeline.playFromStart();
    }

    @FXML
    void showAbout(MouseEvent event) {
        Alert a = new Alert(Alert.AlertType.NONE);
        a.setAlertType(Alert.AlertType.INFORMATION);
        a.setHeaderText("About MySheet");
        a.setContentText("MySheet is a program that was created by Michael Mark in the Fall of 2022. " +
                "MySheet allows the user to organize and create sheet music lists based on " +
                "a database filled with sheet music uploaded by various artists. The user can then " +
                "open these sheet music files or even download them and open them instantaneously.\n\n" +
                "The language used in MySheet was mostly Java and MySQL. Platforms that MySheet uses are: " +
                "JavaFX, Apache Commons Net, SceneBuilder, XAMPP, MySQL, Apache. If anyone finds this " +
                "program, please make sure to credit me for my work as I spent many hours on it.\n\nThanks,\nMike");
        a.show();
    }

    @FXML
    void showAdmin(MouseEvent event) throws IOException {
        Stage stage = (Stage) btnLogin.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("admin-user-view.fxml"));
        Stage stage2 = new Stage();
        Scene scene = new Scene(fxmlLoader.load());
        stage2.setScene(scene);
        stage.close();
        stage2.show();
        stage2.setTitle("MySheet User Admin Panel");
    }


}
