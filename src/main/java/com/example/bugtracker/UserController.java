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
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.*;
import java.sql.*;

public class UserController {

    @FXML
    private Button btnDownload;

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
    private TableView<Sheets> tvFavorites;

    @FXML
    private TableView<Sheets> tvSheets;

    @FXML
    private Text txStatus;

    @FXML
    private Text txGreeting;

    @FXML
    private VBox vbFavorites;

    @FXML
    private VBox vbHome;

    @FXML
    private VBox vbSearch;

    //Logged in user info
    public String Username;
    public String Name;
    public Integer Id;
    public TableView<Sheets> currentSheet;


    void updateUsernameText(String username_text) throws SQLException {
        Username = username_text;
        Connection conn = getConnection();
        String query = "SELECT id, name FROM users WHERE username='" + username_text + "'";
        Statement st;
        ResultSet rs;
        try {
            st = conn.createStatement();
            rs = st.executeQuery(query);
            rs.next();
            Id = rs.getInt(1);
            Name = rs.getString(2);
            txGreeting.setText("Greetings " + Name + ", from MySheet!");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
        currentSheet = tvSheets;
        showSheets();
    }

    @FXML
    void showFavorites(ActionEvent event) {
        vbHome.setVisible(false);
        vbSearch.setVisible(false);
        vbFavorites.setVisible(true);
        currentSheet = tvFavorites;
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

    //---------------View/Download PDF-----------------------------------
    //FTP Server Variables -- Edit your settings here
    final String server = "127.0.0.1";
    final int port = 21;
    final String username = "root";
    final String password = "";
    final File[] fileToSend = new File[1];

    @FXML
    void openPdf() {
        if (currentSheet.getSelectionModel().getSelectedItem() == null) {
            this.txStatus.setText("Please Select Sheet to Open");
            PauseTransition delay = new PauseTransition(Duration.seconds(4));
            delay.setOnFinished(a -> this.txStatus.setText(""));
            delay.play();
        } else {
            String filename = currentSheet.getSelectionModel().getSelectedItem().getPath();
            if (!filename.isEmpty()) {
                final Hyperlink hyperlink = new Hyperlink("http://" + server + "/" + filename);
                MainApplication.getStaticHostServices().showDocument(hyperlink.getText());
                this.txStatus.setText("Sheet Music Opened!");
                PauseTransition delay = new PauseTransition(Duration.seconds(4));
                delay.setOnFinished(a -> this.txStatus.setText(""));
                delay.play();
            } else {
                this.txStatus.setText("No Sheet Music!");
                PauseTransition delay = new PauseTransition(Duration.seconds(4));
                delay.setOnFinished(a -> this.txStatus.setText(""));
                delay.play();
            }
        }
    }

    @FXML
    void downloadPdf()  {
        if (currentSheet.getSelectionModel().getSelectedItem() == null) {
            this.txStatus.setText("Please Select Sheet to Download");
            PauseTransition delay = new PauseTransition(Duration.seconds(4));
            delay.setOnFinished(a -> this.txStatus.setText(""));
            delay.play();
        } else {
            try {
                String filename = currentSheet.getSelectionModel().getSelectedItem().getPath();
                if(!filename.isEmpty()) {
                    downloadFile(currentSheet.getSelectionModel().getSelectedItem().getPath());
                } else {
                    this.txStatus.setText("No sheet music to download!");
                    PauseTransition delay = new PauseTransition(Duration.seconds(4));
                    delay.setOnFinished(a -> this.txStatus.setText(""));
                    delay.play();
                }
            } catch (Exception ex) {
                this.txStatus.setText("Sheet music NOT opened!");
                PauseTransition delay = new PauseTransition(Duration.seconds(4));
                delay.setOnFinished(a -> this.txStatus.setText(""));
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

            // *Should Implement a file chooser for the user to save it to a certain FILENAME+path
            Stage stage = new Stage();
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setInitialDirectory(new File("D:\\Music"));
            File selectedDirectory = directoryChooser.showDialog(stage);
            System.out.println(selectedDirectory.getAbsolutePath());

            String remoteFile2 = filename;
            File downloadFile2 = new File(selectedDirectory.getAbsolutePath() + "\\" + filename);
            OutputStream outputStream2 = new BufferedOutputStream(new FileOutputStream(downloadFile2));
            InputStream inputStream = ftpClient.retrieveFileStream(remoteFile2);
            byte[] bytesArray = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(bytesArray)) != -1) {
                outputStream2.write(bytesArray, 0, bytesRead);
            }
            boolean success = ftpClient.completePendingCommand();
            if (success) {
                System.out.println("File has been downloaded successfully.");
                txStatus.setText("File downloaded successfully");
                PauseTransition delay = new PauseTransition(Duration.seconds(4));
                delay.setOnFinished(a -> this.txStatus.setText(""));
                delay.play();
            } else {

                txStatus.setText("File downloaded successfully");
            }
            outputStream2.close();
            inputStream.close();

        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    //--------------FAVORITES ADD/REMOVE-------------------------------------

    @FXML
    void addFavorite(ActionEvent event) {
        if (currentSheet.getSelectionModel().getSelectedItem() == null) {
            this.txStatus.setText("Please Select Sheet to Favorite");
            PauseTransition delay = new PauseTransition(Duration.seconds(4));
            delay.setOnFinished(a -> this.txStatus.setText(""));
            delay.play();
        } else {
            try {
                Integer sheetId = currentSheet.getSelectionModel().getSelectedItem().getId()    ;
                sqlAddFavorite(Id, sheetId);
                this.txStatus.setText("Sheet music favorited!");  //need to find a way to check for duplicate
                PauseTransition delay = new PauseTransition(Duration.seconds(4));
                delay.setOnFinished(a -> this.txStatus.setText(""));
                delay.play();
            } catch (Exception ex) {
                this.txStatus.setText("Sheet music NOT favorited!");
                PauseTransition delay = new PauseTransition(Duration.seconds(4));
                delay.setOnFinished(a -> this.txStatus.setText(""));
                delay.play();
                ex.printStackTrace();
            }
        }
    }

    public void sqlAddFavorite(int userId, int sheetId) {
        String query = "INSERT INTO fave VALUES (" + userId  + ", " + sheetId + ")";
        executeQuery(query);
    }

    @FXML
    void unFavorite(ActionEvent event) {
        if (currentSheet.getSelectionModel().getSelectedItem() == null) {
            this.txStatus.setText("Please Select Sheet to Unfavorite");
            PauseTransition delay = new PauseTransition(Duration.seconds(4));
            delay.setOnFinished(a -> this.txStatus.setText(""));
            delay.play();
        } else {
            try {
                Integer sheetId = currentSheet.getSelectionModel().getSelectedItem().getId()    ;
                sqlunFavorite(Id, sheetId);
                this.txStatus.setText("Sheet music Unfavorited!");
                PauseTransition delay = new PauseTransition(Duration.seconds(4));
                delay.setOnFinished(a -> this.txStatus.setText(""));
                delay.play();
            } catch (Exception ex) {
                this.txStatus.setText("Sheet music NOT Unfavorited!");
                PauseTransition delay = new PauseTransition(Duration.seconds(4));
                delay.setOnFinished(a -> this.txStatus.setText(""));
                delay.play();
                ex.printStackTrace();
            }
        }
    }

    public void sqlunFavorite(int userId, int sheetId) {
        String query = "DELETE FROM fave WHERE user_id = " + userId  + " AND sheet_id = " + sheetId;
        executeQuery(query);
    }


}
