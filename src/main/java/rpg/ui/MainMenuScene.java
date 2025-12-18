package rpg.ui;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import rpg.core.GameEngine;
import rpg.map.GameMap;
import rpg.quest.QuestManager;
import rpg.characters.Character;
import java.util.List;

public class MainMenuScene {

    public static Scene build (Stage primaryStage, GameEngine engine, GameMap map,
                            List<Character> enemies, QuestManager questManager) {

        // === Background Image ===
        Image bgImage = new Image(MainMenuScene.class.getResource("/images/menu_bg.jpeg").toExternalForm());
ImageView background = new ImageView(bgImage);
background.setFitWidth(900);
background.setFitHeight(800);
background.setPreserveRatio(false);
background.setOpacity(1.0); // Optional fade
        // === Foreground UI ===
        VBox menuBox = new VBox(20);
        menuBox.setPadding(new Insets(40));
        menuBox.setAlignment(Pos.CENTER);

        Label title = new Label("🧙 Welcome to Your RPG");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");

        Button startBtn = new Button("▶ Start New Game");
        Button loadBtn = new Button("📂 Load Game");
        Button exitBtn = new Button("❌ Exit");

        String btnStyle = "-fx-background-color: #555; -fx-text-fill: white; -fx-font-size: 14px;";
        startBtn.setStyle(btnStyle);
        loadBtn.setStyle(btnStyle);
        exitBtn.setStyle(btnStyle);

        TextArea logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setPrefHeight(200);

        startBtn.setOnAction(e -> {
            Scene mainScene = MainSceneBuilder.build(primaryStage, engine, map, enemies, logArea);
            primaryStage.setScene(mainScene);
        });

        loadBtn.setOnAction(e -> {
            engine.loadGame("savegame.dat");
            Scene mainScene = MainSceneBuilder.build(primaryStage, engine, engine.getMap(), enemies, logArea);
            primaryStage.setScene(mainScene);
        });

        exitBtn.setOnAction(e -> primaryStage.close());

        menuBox.getChildren().addAll(title, startBtn, loadBtn, exitBtn);


        // === Stack Layout ===
        StackPane root = new StackPane();
        root.getChildren().addAll(background, menuBox);

        return new Scene(root, 900, 800);
    }
}