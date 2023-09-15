package com.example.farmaciaproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends Application {
    public static Stage mainStage;
    public static Stage getMainStage() {
        return mainStage;
    }

    @Override
    public void start(Stage stage) throws IOException {
        mainStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("hello-view.fxml"));
        Parent root = fxmlLoader.load();
        HelloController controller = fxmlLoader.getController();
        controller.setMainStage(stage);
        FXMLLoader mainMenuLoader = new FXMLLoader(Main.class.getResource("mainmenu.fxml"));
        Parent mainMenuRoot = mainMenuLoader.load();
        MainMenuController mainMenuController = mainMenuLoader.getController();
        mainMenuController.setMainStage(stage);
        // Se deshabilita el botón de maximizar
        stage.setResizable(false);
        Image icon = new Image(getClass().getResourceAsStream("/image/logo.png"));
        // Configurar el icono en el primaryStage (ventana principal)
        stage.getIcons().add(icon);
        Scene scene = new Scene(root, 1080, 720);
        stage.setTitle("Farmacia EL DIFUNTO");
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
        launch();
    }
}
