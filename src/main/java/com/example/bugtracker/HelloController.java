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
import java.sql.*;
import java.util.ResourceBundle;

public class HelloController {


    @FXML
    private Button btnConnect;

    @FXML
    private CheckBox cbUpload;

    @FXML
    private Button btnRemove;

    @FXML
    private Button btnDownload;

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

    @FXML
    void clearFields(ActionEvent event) {
        tfId.clear();
        tfTitle.clear();
        tfComposer.clear();
        tfYear.clear();
        tfPages.clear();
        cbUpload.setSelected(false);
        tvSheets.getSelectionModel().select(null);
    }

    public static boolean connected = false;
    @FXML
    void connectToDb(ActionEvent event) {
        if(!connected) {
            try {
                this.tfStatus.setText("Database Connected!");
                PauseTransition delay = new PauseTransition(Duration.seconds(4));
                delay.setOnFinished( a -> this.tfStatus.setText(""));
                delay.play();
                this.btnConnect.setText("Disconnect from Database");
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

        colId.setCellValueFactory(new PropertyValueFactory<Sheets, Integer>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<Sheets, String>("title"));
        colComposer.setCellValueFactory(new PropertyValueFactory<Sheets, String>("composer"));
        colYear.setCellValueFactory(new PropertyValueFactory<Sheets, Integer>("year"));
        colPages.setCellValueFactory(new PropertyValueFactory<Sheets, Integer>("pages"));
        colPath.setCellValueFactory(new PropertyValueFactory<Sheets, String>("path"));

        tvSheets.setItems(list);

    }

    //------------ SQL BUTTONS ---------------------
    private void insertRecord() {          //the ID will auto increment
        try {
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
            }
            showSheets();
            this.tfStatus.setText("Record inserted.");
            PauseTransition delay = new PauseTransition(Duration.seconds(4));
            delay.setOnFinished(a -> this.tfStatus.setText(""));
            delay.play();
        } catch (Exception ex) {
            this.tfStatus.setText("Record NOT inserted!");
            PauseTransition delay = new PauseTransition(Duration.seconds(4));
            delay.setOnFinished(a -> this.tfStatus.setText(""));
            delay.play();
            ex.printStackTrace();
        }
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

    final File[] fileToSend = new File[1];

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


    //------------- Modify Sheet Music PDF Functions -----------------------------
    @FXML
    void uploadPdf() {
        if (tvSheets.getSelectionModel().getSelectedItem() == null) {
            this.tfStatus.setText("Please Select Sheet to Replace!");
            PauseTransition delay = new PauseTransition(Duration.seconds(4));
            delay.setOnFinished(a -> this.tfStatus.setText(""));
            delay.play();
        } else {
            try {
                uploadFile();
                String query = "UPDATE sheets SET path = '" + fileToSend[0].getName() + "' WHERE id=" + tfId.getText();
                executeQuery(query);
                if(!fileToSend[0].getName().isEmpty()) this.tfStatus.setText("File uploaded!");
                showSheets();
                PauseTransition delay = new PauseTransition(Duration.seconds(4));
                delay.setOnFinished(a -> this.tfStatus.setText(""));
                delay.play();
            } catch (Exception ex) {
                this.tfStatus.setText("File NOT Uploaded!");
                PauseTransition delay = new PauseTransition(Duration.seconds(4));
                delay.setOnFinished(a -> this.tfStatus.setText(""));
                delay.play();
                ex.printStackTrace();
            }
        }
    }

    @FXML
    void uploadFile() {
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

    @FXML
    void downloadPdf()  {
        if (tvSheets.getSelectionModel().getSelectedItem() == null) {
            this.tfStatus.setText("Please Select Sheet to Download");
            PauseTransition delay = new PauseTransition(Duration.seconds(4));
            delay.setOnFinished(a -> this.tfStatus.setText(""));
            delay.play();
        } else {
            try {
                String filename = tvSheets.getSelectionModel().getSelectedItem().getPath();
                System.out.println("Opening " + filename);
                downloadFile(tvSheets.getSelectionModel().getSelectedItem().getPath());

                this.tfStatus.setText("Sheet music downloading...");
                showSheets();
                PauseTransition delay = new PauseTransition(Duration.seconds(4));
                delay.setOnFinished(a -> this.tfStatus.setText(""));
                delay.play();
            } catch (Exception ex) {
                this.tfStatus.setText("Sheet music NOT opened!");
                PauseTransition delay = new PauseTransition(Duration.seconds(4));
                delay.setOnFinished(a -> this.tfStatus.setText(""));
                delay.play();
                ex.printStackTrace();
            }
        }
    }

    public void downloadFile(String filename) {
        try {
            FTPClient ftpClient = new FTPClient();

            ftpClient.connect(server, port);
            ftpClient.login(username, password);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            // APPROACH #2: using InputStream retrieveFileStream(String)
            String remoteFile2 = filename;
            File downloadFile2 = new File("D:\\Music\\" + filename);
            OutputStream outputStream2 = new BufferedOutputStream(new FileOutputStream(downloadFile2));
            InputStream inputStream = ftpClient.retrieveFileStream(remoteFile2);
            byte[] bytesArray = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(bytesArray)) != -1) {
                outputStream2.write(bytesArray, 0, bytesRead);
            }

            boolean success = ftpClient.completePendingCommand();
            if (success) {
                System.out.println("File #2 has been downloaded successfully.");
            }
            outputStream2.close();
            inputStream.close();


            final Hyperlink hyperlink = new Hyperlink("D:\\Music\\" + filename);
            HelloApplication.getStaticHostServices().showDocument(hyperlink.getText());

        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @FXML
    void openPdf() {
        if (tvSheets.getSelectionModel().getSelectedItem() == null) {
            this.tfStatus.setText("Please Select Sheet to Open");
            PauseTransition delay = new PauseTransition(Duration.seconds(4));
            delay.setOnFinished(a -> this.tfStatus.setText(""));
            delay.play();
        } else {
            String filename = tvSheets.getSelectionModel().getSelectedItem().getPath();
            final Hyperlink hyperlink = new Hyperlink("http://" + server + "/" + filename);
            HelloApplication.getStaticHostServices().showDocument(hyperlink.getText());
            this.tfStatus.setText("Sheet Music Opened!");
            PauseTransition delay = new PauseTransition(Duration.seconds(4));
            delay.setOnFinished(a -> this.tfStatus.setText(""));
            delay.play();
        }
    }

    @FXML
    void removePdf(ActionEvent event) {
        if (tvSheets.getSelectionModel().getSelectedItem() == null) {
            this.tfStatus.setText("Please Select Sheet to Remove!");
            PauseTransition delay = new PauseTransition(Duration.seconds(4));
            delay.setOnFinished(a -> this.tfStatus.setText(""));
            delay.play();
        } else {
            try {
                String filename = tvSheets.getSelectionModel().getSelectedItem().getPath();
                System.out.println("Deleting " + filename);
                deleteFile(tvSheets.getSelectionModel().getSelectedItem().getPath());
                String query = "UPDATE sheets SET path = '' WHERE id=" + tfId.getText();
                executeQuery(query);

                this.tfStatus.setText("Sheet music removed successfully");
                showSheets();
                PauseTransition delay = new PauseTransition(Duration.seconds(4));
                delay.setOnFinished(a -> this.tfStatus.setText(""));
                delay.play();
            } catch (Exception ex) {
                this.tfStatus.setText("Sheet music NOT removed!");
                PauseTransition delay = new PauseTransition(Duration.seconds(4));
                delay.setOnFinished(a -> this.tfStatus.setText(""));
                delay.play();
                ex.printStackTrace();
            }
        }
    }
    public void deleteFile(String filename) {
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(server, port);
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                System.out.println("Connect failed");
                return;
            }

            boolean success = ftpClient.login(username, password);

            if (!success) {
                System.out.println("Could not login to the server");
                return;
            }

            boolean deleted = ftpClient.deleteFile(filename);
            if (deleted) {
                System.out.println("The file was deleted successfully.");
            } else {
                System.out.println("Could not delete the  file, it may not exist.");
            }

        } catch (IOException ex) {
            System.out.println("Oh no, there was an error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}



