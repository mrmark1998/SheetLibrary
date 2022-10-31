package com.example.bugtracker;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

public class HelloController {

    @FXML
    private Button btnTestOpen;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnInsert;

    @FXML
    private Button btnUpdate;

    @FXML
    private TableColumn<Sheets, String> colComposer;

    @FXML
    private TableColumn<Sheets, Integer> colId;

    @FXML
    private TableColumn<Sheets, Integer> colPages;

    @FXML
    private TableColumn<Sheets, String> colTitle;

    @FXML
    private TableColumn<Sheets, Integer> colYear;

    @FXML
    private TextField tfComposer;

    @FXML
    private Text tfHeader;

    @FXML
    private TextField tfId;

    @FXML
    private TextField tfPages;

    @FXML
    private TextField tfTitle;

    @FXML
    private TextField tfYear;

    @FXML
    private TableView<Sheets> tvSheets;


    @FXML
    private Text tfStatus;

    @FXML
    void connectToDb(ActionEvent event) {
        this.tfStatus.setText("Database Connected!");
        showSheets();
    }
    @FXML
    void handleButtonAction(ActionEvent event) {
        if(event.getSource() == btnInsert) {
            this.tfStatus.setText("Record inserted--->");
            insertRecord();
        }
        if(event.getSource() == btnUpdate) {
            this.tfStatus.setText("Record updated!");
            updateRecord();
        }
        if(event.getSource() == btnDelete) {
            this.tfStatus.setText("Record deleted!");
            deleteRecord();
        }
    }

    public void initialize(URL url, ResourceBundle rb) {
        showSheets();
    }

    public Connection getConnection(){
        Connection conn;
        try{
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sheets", "root", "");
            System.out.println("Database Connection success");
            return conn;
        } catch(Exception ex){
            System.out.println("Error: " + ex.getMessage());
            this.tfStatus.setText("Database Error!");
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
                                    rs.getInt("pages"));
                sheetList.add(sheets);
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return sheetList;
    }

    public void showSheets() {
        ObservableList<Sheets> list = getSheetsList();

        colId.setCellValueFactory(new PropertyValueFactory<Sheets, Integer>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<Sheets, String>("title"));
        colComposer.setCellValueFactory(new PropertyValueFactory<Sheets, String>("composer"));
        colYear.setCellValueFactory(new PropertyValueFactory<Sheets, Integer>("year"));
        colPages.setCellValueFactory(new PropertyValueFactory<Sheets, Integer>("pages"));

        tvSheets.setItems(list);

    }

    //------------ SQL BUTTONS ---------------------
    private void insertRecord() {                   //the ID will auto increment
        String query = "INSERT INTO sheets VALUES (" + tfId.getText() + ",'" +
                                                    tfTitle.getText() + "','" +
                                                    tfComposer.getText() + "'," +
                                                    tfYear.getText() + "," +
                                                    tfPages.getText() + ")";
        executeQuery(query);
        showSheets();
    }

    private void updateRecord() {
    String query = "UPDATE sheets SET title = '" +   tfTitle.getText() +
                                "', composer = '" + tfComposer.getText() +
                                "', year = " +      tfYear.getText() +
                                ", pages = " +      tfPages.getText() +
                                " WHERE id = " +    tfId.getText();
    executeQuery(query);
    showSheets();
    }

    private void deleteRecord() {
        String query = "DELETE FROM sheets WHERE id = " +  tfId.getText();
        executeQuery(query);
        executeQuery("ALTER TABLE sheets AUTO_INCREMENT=1;");
        showSheets();
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
    @FXML
    public void handleMouseAction(Event event) {
        Sheets sheet = tvSheets.getSelectionModel().getSelectedItem();
        tfId.setText(""+sheet.getId());
        tfTitle.setText(sheet.getTitle());
        tfComposer.setText(sheet.getComposer());
        tfYear.setText(""+sheet.getYear());
        tfPages.setText(""+sheet.getPages());
    }

    @FXML
    void testOpen(ActionEvent event) {
        final Hyperlink hyperlink = new Hyperlink("D:\\Music\\Sheets\\Lizst - Widmung, Complete Score (S.566) (filter).pdf");

        HelloApplication.getStaticHostServices().showDocument(hyperlink.getText());

        this.tfStatus.setText("Opening Sheet Music ...");
    }
}
