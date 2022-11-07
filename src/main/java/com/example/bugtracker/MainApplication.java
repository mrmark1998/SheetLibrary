package com.example.bugtracker;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {

    private static HostServices hostServices ;

    public static HostServices getStaticHostServices() {
        return hostServices ;
    }

    private static Stage stg;

    @Override
    public void start(Stage stage) throws IOException {
        hostServices = getHostServices();

        stg = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("login-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
        stage.setTitle("MySheet Login");
    }

    //quick change scene function, but keeps window size same
    public void changeScene(String fxml) throws IOException {
        Parent pane = FXMLLoader.load(getClass().getResource(fxml));
        stg.getScene().setRoot(pane);
    }

    public static void main(String[] args) {
        launch();
    }
}