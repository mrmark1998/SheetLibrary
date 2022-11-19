package com.example.bugtracker;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

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
    private TableColumn<Sheets, String> colComposer;

    @FXML
    private TableColumn<Sheets, String> colComposer1;

    @FXML
    private TableColumn<Sheets, Integer> colPages;

    @FXML
    private TableColumn<Sheets, Integer> colPages1;

    @FXML
    private TableColumn<Sheets, String> colTitle;

    @FXML
    private TableColumn<Sheets, String> colTitle1;

    @FXML
    private TableColumn<Sheets, Integer> colYear;

    @FXML
    private TableColumn<Sheets, Integer> colYear1;

    @FXML
    private TextField tfSearch;

    @FXML
    private TableView<?> tvFavorites;

    @FXML
    private TableView<Sheets> tvSheets;

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
                                    this.txStatus.setText("Success! Bringing you back to login.");
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
        showSheets();
    }

    @FXML
    void showFavorites(ActionEvent event) {
        vbHome.setVisible(false);
        vbSearch.setVisible(false);
        vbFavorites.setVisible(true);
    }

    //-------------DB necessities-------------------------
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

    public Connection getConnection(){
        Connection conn;
        try{
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sheets", "root", "");
            return conn;
        } catch(Exception ex){
            System.out.println("Error: " + ex.getMessage());
            this.txStatus.setText("Database Error!");
            return null;
        }
    }

    public ObservableList<Sheets> getSheetsList() {
        ObservableList<Sheets> sheetList = FXCollections.observableArrayList();
        Connection conn = getConnection();
        String query = "SELECT * FROM sheets";
        Statement st;
        ResultSet rs;

        try{
            st = conn.createStatement();
            rs = st.executeQuery(query);
            Sheets sheets;
            while(rs.next()) {

                sheets = new Sheets(rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("composer"),
                        rs.getInt("year"),
                        rs.getInt("pages"),
                        rs.getString("path"));

                sheetList.add(sheets);
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return sheetList;
    }

    public void showSheets() {
        ObservableList<Sheets> list = getSheetsList();
        colTitle.setCellValueFactory(new PropertyValueFactory<Sheets, String>("title"));
        colComposer.setCellValueFactory(new PropertyValueFactory<Sheets, String>("composer"));
        colYear.setCellValueFactory(new PropertyValueFactory<Sheets, Integer>("year"));
        colPages.setCellValueFactory(new PropertyValueFactory<Sheets, Integer>("pages"));

        tvSheets.setItems(list);
    }

}
