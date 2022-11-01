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

import java.io.IOException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;


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
    private CheckBox cbUpload;

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
    private TableColumn<Sheets, String> colPath;

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
                String pathExists = "Y";
                if(rs.getString("path").isEmpty()) pathExists = "N";
                sheets = new Sheets(rs.getInt("id"),
                                    rs.getString("title"),
                                    rs.getString("composer"),
                                    rs.getInt("year"),
                                    rs.getInt("pages"),
                                    pathExists);

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
        colPath.setCellValueFactory(new PropertyValueFactory<Sheets, String>("path"));

        tvSheets.setItems(list);

    }

    //------------ SQL BUTTONS ---------------------
    private void insertRecord() {                   //the ID will auto increment
        String query = "INSERT INTO sheets VALUES (" + tfId.getText() + ",'" +
                                                    tfTitle.getText() + "','" +
                                                    tfComposer.getText() + "'," +
                                                    tfYear.getText() + "," +
                                                    tfPages.getText() + ",'')";
        executeQuery(query);
        if (cbUpload.isSelected()) {
            uploadPdf();
            System.out.println(fileToSend[0].getName());
            query = "UPDATE sheets SET path = '" + fileToSend[0].getName() + "' WHERE id=" + tfId.getText();
            executeQuery(query);
            cbUpload.setSelected(false);
        }
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
    void uploadPdf() {
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

    //FTP Server Variables -- Edit your settings here
    final String server = "127.0.0.1";
    final int port = 21;
    final String username = "root";
    final String password = "";

    public void createRecord() {
        if(fileToSend[0] == null) {
            this.tfStatus.setText("Please choose a file first.");
        } else {
            try {
                int reply;
                FTPClient ftp = new FTPClient();

                ftp.connect(server, port);
                ftp.login(username, password);
                ftp.setFileType(FTP.BINARY_FILE_TYPE);
                System.out.println("Connected to " + server + ".");
                System.out.print(ftp.getReplyString());

                // After connection attempt, you should check the reply code to verify
                // success.
                reply = ftp.getReplyCode();

                File file = new File(fileToSend[0].getAbsolutePath());
                InputStream inputStream = new FileInputStream(file);

                System.out.println("Start uploading file");
                OutputStream outputStream = ftp.storeFileStream(fileToSend[0].getName());
                byte[] bytesIn = new byte[4096];
                int read = 0;

                while ((read = inputStream.read(bytesIn)) != -1) {
                    outputStream.write(bytesIn, 0, read);
                }
                inputStream.close();
                outputStream.close();

                boolean completed = ftp.completePendingCommand();
                if (completed) {
                    System.out.println("File is uploaded successfully.");
                }

                if (!FTPReply.isPositiveCompletion(reply)) {
                    ftp.disconnect();
                    System.err.println("FTP server refused connection.");
                    System.exit(1);
                }
                ftp.logout();
                System.out.println("FTP Connection Closed.");

            } catch (IOException e) {
                e.printStackTrace();
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



