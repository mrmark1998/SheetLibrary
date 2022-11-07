package com.example.bugtracker;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class RegisterController {

    @FXML
    private Button btnBack;

    @FXML
    private Button btnClear;

    @FXML
    private Button btnLogin;

    @FXML
    private Text regStatus;

    @FXML
    private TextField tfEmail;

    @FXML
    private Text tfHeader;

    @FXML
    private TextField tfName;

    @FXML
    private TextField tfPassword;

    @FXML
    private TextField tfUsername;

    @FXML
    private ComboBox<?> userType;

    @FXML
    public void initialize() {
        //initializes the combobox for user type
        List<String> list = new ArrayList<String>();
        list.add("User");
        list.add("Artist");
        list.add("Admin");
        ObservableList types = FXCollections.observableList(list);
        userType.setItems(types);
    }
    @FXML
    void clearFields(ActionEvent event) {
        tfUsername.clear();
        tfPassword.clear();
        tfName.clear();
        tfEmail.clear();
        userType.getSelectionModel().select(0);
    }

    @FXML
    void showLogin(ActionEvent event) throws IOException {
        Stage stage = (Stage) btnBack.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("login-view.fxml"));
        Stage stage2 = new Stage();
        Scene scene = new Scene(fxmlLoader.load());
        stage2.setScene(scene);
        stage.close();
        stage2.show();
        stage2.setTitle("MySheet Login");
    }

    @FXML
    void userRegister(ActionEvent event) throws IOException {
        try {
            if (formValidator(tfName.getText(), tfEmail.getText(), tfUsername.getText(), tfPassword.getText())) {
                String query = "INSERT INTO users VALUES (NULL, '" + tfName.getText() + "','" +
                        tfEmail.getText() + "','" +
                        tfUsername.getText() + "','" +
                        tfPassword.getText() + "','" +
                        userType.getValue() + "')";
                System.out.println(query);
                executeQuery(query);
                Stage stage = (Stage) btnLogin.getScene().getWindow();
                FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("login-view.fxml"));
                Stage stage2 = new Stage();
                Scene scene = new Scene(fxmlLoader.load());
                stage2.setScene(scene);

                //if registration successful, goes through timeline of going back to Login Screen-
                Timeline timeline =
                        new Timeline(
                                new KeyFrame(
                                        Duration.ZERO,
                                        e -> this.regStatus.setText("Registration Successful!")
                                ),
                                new KeyFrame(
                                        Duration.seconds(2),
                                        e -> {
                                            this.regStatus.setText("Bringing you back to login");
                                        }),
                                // second rectangle to black, third to blue
                                new KeyFrame(
                                        Duration.seconds(5.0), // duration doesn't stack
                                        e -> {
                                            stage.close();
                                            stage2.show();
                                            stage2.setTitle("Login to Sheetify");
                                        })
                        );
                timeline.playFromStart();
            }
        } catch (Exception ex) {
            regStatus.setText("User database error");
            ex.printStackTrace();
        }
    }

    public Connection getConnection(){
        Connection conn;
        try{
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sheets", "root", "");
            return conn;
        } catch(Exception ex){
            System.out.println("Error: " + ex.getMessage());
            this.regStatus.setText("Database Error!");
            return null;
        }
    }

    private void executeQuery(String query) {
        Connection conn = getConnection();
        Statement st;
        try {
            st = conn.createStatement();
            st.executeUpdate(query);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public boolean formValidator(String name, String email, String username, String password) {
        if(name.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            regStatus.setText("Please completely fill out form");
            return false;
        }

        if(!Pattern.compile("^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$").matcher(name).matches()) {
            regStatus.setText("Name can only contain only spaces and letters");
            return false;
        }

        if (!Pattern.compile("^(.+)@(\\S+)$").matcher(email).matches()) {
            regStatus.setText("Email address incorrect (example@you.com)");
            return false;
        }

        if (!Pattern.compile("[a-zA-Z0-9_.-]{5,30}").matcher(username).matches()) {
            regStatus.setText("Username must be between 5-30 characters, and can only contain letters and '_.-'");
            return false;
        }

        if (!Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$").matcher(password).matches()) {
            regStatus.setText("Password must be between 8-20 characters and include 1 uppercase, 1 lowercase, 1 special char");
            return false;
        }

        return true;
    }

}

