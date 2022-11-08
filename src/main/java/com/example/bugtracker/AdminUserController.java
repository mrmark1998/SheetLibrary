package com.example.bugtracker;

import java.io.IOException;
import java.lang.String;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class AdminUserController {

    @FXML
    private String artist;

    @FXML
    private Button btnClear;

    @FXML
    private Button btnShowSheets;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnLogout;

    @FXML
    private Button btnSubmit;

    @FXML
    private Button btnUpdate;

    @FXML
    private TableColumn<Users, String> colEmail;

    @FXML
    private TableColumn<Users, String> colName;

    @FXML
    private TableColumn<Users, String> colPassword;

    @FXML
    private TableColumn<Users, String> colType;

    @FXML
    private TableColumn<Users, String> colUsername;

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
    private TableView<Users> tvUsers;

    @FXML
    private String user;

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
        showUserList();
    }

    @FXML
    void clearFields(ActionEvent event) {
        tfUsername.clear();
        tfPassword.clear();
        tfName.clear();
        tfEmail.clear();
        tvUsers.getSelectionModel().select(null);
        userType.getSelectionModel().select(0);
    }

    @FXML
    void showSheets() throws IOException {
        Stage stage = (Stage) btnLogout.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));
        Stage stage2 = new Stage();
        Scene scene = new Scene(fxmlLoader.load());
        stage2.setScene(scene);

        //if registration successful, goes through timeline of going back to Login Screen-
        Timeline timeline =
                new Timeline(
                        new KeyFrame(
                                Duration.ZERO,
                                e -> this.regStatus.setText("Saving everything")
                        ),
                        new KeyFrame(
                                Duration.seconds(1.5),
                                e -> {
                                    this.regStatus.setText("Success! Loading sheet music library...");
                                }),
                        // second rectangle to black, third to blue
                        new KeyFrame(
                                Duration.seconds(3.0), // duration doesn't stack
                                e -> {
                                    stage.close();
                                    stage2.show();
                                    stage2.setTitle("MySheet Admin Library");
                                })
                );
        timeline.playFromStart();
    }

    @FXML
    void handleMouseAction() {
        if(tvUsers.getSelectionModel().getSelectedItem() != null) {
            Users user = tvUsers.getSelectionModel().getSelectedItem();
            tfName.setText(user.getName());
            tfEmail.setText(user.getEmail());
            tfUsername.setText("" + user.getUsername());
            tfPassword.setText("" + user.getPassword());
            //combolist indices
            int numType = 0; //default is User
            if(user.getType().equals("Artist")) numType = 1;
            if(user.getType().equals("Admin")) numType = 2;
            userType.getSelectionModel().select(numType);
        }
    }


    @FXML
    void userLogout(ActionEvent event) throws IOException {
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
                                e -> this.regStatus.setText("Thank you admin for your services! Logging out...")
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
                                    stage2.setTitle("MySheet Login");
                                })
                );
        timeline.playFromStart();
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


    //-------------------SQL Operators CRUD----------------------------------

    @FXML
    void userRegister(ActionEvent event) {
        if(formValidator(tfName.getText(), tfEmail.getText(), tfUsername.getText(), tfPassword.getText())) {
            String query = "INSERT INTO users VALUES (NULL, '" + tfName.getText() + "','" +
                    tfEmail.getText() + "','" +
                    tfUsername.getText() + "','" +
                    tfPassword.getText() + "','" +
                    userType.getValue() + "')";
            System.out.println(query);
            executeQuery(query);
            showUserList();
        }
    }

    @FXML
    void userUpdate(ActionEvent event) {
        if (tvUsers.getSelectionModel().getSelectedItem() != null ) {
            Users user = tvUsers.getSelectionModel().getSelectedItem();
            if(formValidator(tfName.getText(), tfEmail.getText(), tfUsername.getText(), tfPassword.getText())) {
                String query = "UPDATE users SET name = '" + tfName.getText() +
                        "', email = '" + tfEmail.getText() +
                        "', username = '" + tfUsername.getText() +
                        "', password = '" + tfPassword.getText() +
                        "', type = '" + userType.getValue() +
                        "' WHERE id = " + user.getId();
                System.out.println(query);
                executeQuery(query);
                showUserList();

                this.regStatus.setText("User updated!");
                PauseTransition delay = new PauseTransition(Duration.seconds(4));
                delay.setOnFinished(a -> this.regStatus.setText(""));
                delay.play();
            }
        } else {
            this.regStatus.setText("Select user to update");
            PauseTransition delay = new PauseTransition(Duration.seconds(4));
            delay.setOnFinished(a -> this.regStatus.setText(""));
            delay.play();
        }
    }

    @FXML
    void userDelete() {
        if (tvUsers.getSelectionModel().getSelectedItem() != null ) {
            Users user = tvUsers.getSelectionModel().getSelectedItem();
            String query = "DELETE FROM users WHERE id = " +  user.getId();
            executeQuery(query);
            showUserList();

            this.regStatus.setText("Record deleted!");
            PauseTransition delay = new PauseTransition(Duration.seconds(4));
            delay.setOnFinished(a -> this.regStatus.setText(""));
            delay.play();
        } else {
            this.regStatus.setText("Select correct record to delete!");
            PauseTransition delay = new PauseTransition(Duration.seconds(4));
            delay.setOnFinished(a -> this.regStatus.setText(""));
            delay.play();
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

    public ObservableList<Users> getUsersList() {
        ObservableList<Users> userList = FXCollections.observableArrayList();
        Connection conn = getConnection();
        String query = "SELECT * FROM users";
        Statement st;
        ResultSet rs;

        try{
            st = conn.createStatement();
            rs = st.executeQuery(query);
            Users user;
            while(rs.next()) {

                user = new Users(rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("type"));

                userList.add(user);
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return userList;
    }


    public void showUserList() {
        ObservableList<Users> list = getUsersList();
        colName.setCellValueFactory(new PropertyValueFactory<Users, String>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<Users, String>("email"));
        colType.setCellValueFactory(new PropertyValueFactory<Users, String>("type"));
        colUsername.setCellValueFactory(new PropertyValueFactory<Users, String>("username"));
        colPassword.setCellValueFactory(new PropertyValueFactory<Users, String>("password"));
        tvUsers.setItems(list);
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