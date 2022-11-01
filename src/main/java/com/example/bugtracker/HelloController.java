package com.example.bugtracker;

import javafx.animation.PauseTransition;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

public class HelloController {


    @FXML
    private Button btnConnect;

    @FXML
    private Button btnClear;

    @FXML
    private Button btnOpen;

    @FXML
    private Button btnUpload;

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

    public static boolean connected = false;
    @FXML
    void connectToDb(ActionEvent event) {
        if(!connected) {
            try {
                this.tfStatus.setText("Database Connected!");
                PauseTransition delay = new PauseTransition(Duration.seconds(4));
                delay.setOnFinished( a -> this.tfStatus.setText(""));
                delay.play();
                this.btnConnect.setText("Disconnect from DB");
                System.out.println("Database Connection Success");
                showSheets();
                connected = true;
            } catch (Exception ex) {
                this.tfStatus.setText("Database Error!");
            }
        } else {
            this.btnConnect.setText("Connect to Database");
            this.tfStatus.setText("Database Disconnected!");
            PauseTransition delay = new PauseTransition(Duration.seconds(4));
            delay.setOnFinished( a -> this.tfStatus.setText("Please Connect to Database"));
            delay.play();
            System.out.println("Database Disconnected");
            tvSheets.getItems().clear();
            connected = false;
        }

    }
    @FXML
    void handleButtonAction(ActionEvent event) {
        if(connected) {
            if (event.getSource() == btnInsert) {
                this.tfStatus.setText("Record inserted--->");
                PauseTransition delay = new PauseTransition(Duration.seconds(4));
                delay.setOnFinished( a -> this.tfStatus.setText(""));
                delay.play();
                insertRecord();
            }
            if (event.getSource() == btnUpdate) {
                this.tfStatus.setText("Record updated!");
                PauseTransition delay = new PauseTransition(Duration.seconds(4));
                delay.setOnFinished( a -> this.tfStatus.setText(""));
                delay.play();
                updateRecord();
            }
            if (event.getSource() == btnDelete) {
                this.tfStatus.setText("Record deleted!");
                PauseTransition delay = new PauseTransition(Duration.seconds(4));
                delay.setOnFinished( a -> this.tfStatus.setText(""));
                delay.play();
                deleteRecord();
            }
        }
    }

    public void initialize(URL url, ResourceBundle rb) {
        showSheets();
    }

    public Connection getConnection(){
        Connection conn;
        try{
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sheets", "root", "");
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

    public void disconnect() {

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
        if(tvSheets.getSelectionModel().getSelectedItem() != null) {
            Sheets sheet = tvSheets.getSelectionModel().getSelectedItem();
            tfId.setText("" + sheet.getId());
            tfTitle.setText(sheet.getTitle());
            tfComposer.setText(sheet.getComposer());
            tfYear.setText("" + sheet.getYear());
            tfPages.setText("" + sheet.getPages());
        }
    }

    @FXML
    void openPdf(ActionEvent event) throws Exception {

        final Hyperlink hyperlink = new Hyperlink("D:\\Music\\Sheets\\Liszt_-_S566_Widmung_Liebeslied_(peters).pdf");

        HelloApplication.getStaticHostServices().showDocument(hyperlink.getText());

        this.tfStatus.setText("Opening Sheet Music ...");
    }

    final File[] fileToSend = new File[1];

    @FXML
    void uploadPdf(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Open Sheet Music File");
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Files", "*.pdf"));
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Files", "*.jpg"));
        File defaultDirectory = new File("D:\\Music\\Sheets");
        fc.setInitialDirectory(defaultDirectory);
        File selectedFile = fc.showOpenDialog(null);
        if(selectedFile != null) {
            String url = selectedFile.toURI().toString();
            fileToSend[0] = selectedFile;
            Alert a = new Alert(Alert.AlertType.NONE);
            a.setAlertType(Alert.AlertType.CONFIRMATION);
            a.setContentText("You have uploaded " + fileToSend[0].getName());
            a.show();
            createRecord();
        }
    }

    public void createRecord() {
        if(fileToSend[0] == null) {
            this.tfStatus.setText("Please choose a file first.");
        } else {
            try {
                System.out.println("Creating record...");
                FileInputStream fileInputStream = new FileInputStream(fileToSend[0].getAbsolutePath());
                Socket socket = new Socket("127.0.0.1", 8080);
                System.out.println("Is socket connected? " + socket.isConnected());
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

                String fileName = fileToSend[0].getName();
                byte[] fileNameBytes = fileName.getBytes();

                byte[] fileContentBytes = new byte[(int) fileToSend[0].length()];
                fileInputStream.read(fileContentBytes);


                System.out.println("Sending Data: " + fileToSend[0].getAbsolutePath());
                dataOutputStream.writeInt(fileNameBytes.length);
                dataOutputStream.write(fileNameBytes);
                dataOutputStream.writeInt(fileContentBytes.length);
                dataOutputStream.write(fileContentBytes);
                this.tfStatus.setText("Sheet has been uploaded.");
                System.out.println("Sheet uploaded");

                /*---------new variation attempt--------
                BufferedInputStream fileIn = new BufferedInputStream(new FileInputStream(fileToSend[0]));
                BufferedOutputStream out =  new BufferedOutputStream(socket.getOutputStream());
                byte[] buffer = new byte[1024];
                int numRead;
                //Checking if bytes available to read to the buffer.
                while( (numRead = fileIn.read(buffer)) >= 0)
                {
                    // Writes bytes to Output Stream from 0 to total number of bytes
                    out.write(buffer, 0, numRead);
                }
                System.out.println("Writing bytes to Output Stream");

                // Flush - send file
                out.flush();

                // close OutputStream
                out.close();
                fileIn.close();
                //-------------- end variation ----------*/
            } catch (IOException error) {
                error.printStackTrace();
            }
        }
    }



    @FXML
    void clearFields(ActionEvent event) {
        tfId.clear();
        tfTitle.clear();
        tfComposer.clear();
        tfYear.clear();
        tfPages.clear();
    }
}



