package com.prologics.tools.nzip;

import java.io.File;

import com.prologics.tools.nzip.service.ParserService;
import com.prologics.tools.nzip.service.RecreatorService;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Bootstrap extends Application {

    private ParserService projectParser = new ParserService();
    private RecreatorService projectRecreator = new RecreatorService();
    
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Nzip");

        var contentBox = new VBox();
        contentBox.getStyleClass().add("root");
        contentBox.setSpacing(20);
        contentBox.setAlignment(Pos.CENTER);

        var directoryChooser = new DirectoryChooser();
        var fileChooser = new FileChooser();

        var parseButton = new Button("Project to JSON");
        parseButton.getStyleClass().add("custom-button");
        parseButton.setOnAction(event -> {
            File projectDirectory = directoryChooser.showDialog(primaryStage);
            if (projectDirectory != null) {
                File outputFileDirectory = directoryChooser.showDialog(primaryStage);
                if (outputFileDirectory != null) {
                	projectParser.parseProjectToJson(projectDirectory, outputFileDirectory);
                    showAlert("Parsing complete!");
                }
            }
        });

        var recreateButton = new Button("JSON to Project");
        recreateButton.getStyleClass().add("custom-button");
        recreateButton.setOnAction(event -> {
            File jsonFile = fileChooser.showOpenDialog(primaryStage);
            if (jsonFile != null) {
                File outputDirectory = directoryChooser.showDialog(primaryStage);
                if (outputDirectory != null) {
                	projectRecreator.recreateProjectFromJson(jsonFile, outputDirectory);
                    showAlert("Recreation complete!");
                }
            }
        });

        var descriptionText = new StringBuilder();
        descriptionText.append("Welcome to the Multi-Language Project Tool!\n");
        descriptionText.append("Parse a project's files into a JSON format.\n");
        descriptionText.append("\t\t\tOR\t\t\t\n");
        descriptionText.append("Recreate the project from a JSON file.");
        
        var descriptionLabel = new Label(descriptionText.toString());
        descriptionLabel.setStyle("description-label");
        
        var buttonsBox = new HBox(20);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.getChildren().addAll(parseButton, recreateButton);

        contentBox.getChildren().addAll(descriptionLabel, buttonsBox);

        var scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        
        var scene = new Scene(scrollPane, 400, 220);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
        
        var fadeTransition = new FadeTransition(Duration.seconds(1), contentBox);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();
    }

    private void showAlert(String message) {
    	var alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}