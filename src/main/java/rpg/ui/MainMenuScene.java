package rpg.ui;

import javafx.scene.Scene;
import javafx.scene.control.*;
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
import rpg.characters.Player;
import rpg.utils.PlayerManager;
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
        background.setOpacity(0.6);

        // === Foreground UI ===
        VBox menuBox = new VBox(30);
        menuBox.setPadding(new Insets(60));
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4);");

        Label title = new Label("🧙 Welcome to Your RPG");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 36px; -fx-font-weight: bold; "
                + "-fx-effect: dropshadow(gaussian, black, 5, 0.7, 2, 2);");

        String btnStyle = "-fx-background-color: linear-gradient(to right, #4CAF50, #45a049); "
                + "-fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 15 40; "
                + "-fx-border-radius: 5; -fx-background-radius: 5; -fx-font-weight: bold;";
        String hoverStyle = "-fx-background-color: linear-gradient(to right, #45a049, #3d8b40);";

        Button startBtn = new Button("▶ Start New Game");
        Button loadBtn = new Button("📂 Load Game");
        Button exitBtn = new Button("❌ Exit");

        startBtn.setStyle(btnStyle);
        loadBtn.setStyle(btnStyle);
        exitBtn.setStyle(btnStyle);

        for (Button btn : java.util.Arrays.asList(startBtn, loadBtn, exitBtn)) {
            btn.setOnMouseEntered(evt -> btn.setStyle(btnStyle + hoverStyle));
            btn.setOnMouseExited(evt -> btn.setStyle(btnStyle));
            btn.setPrefWidth(200);
        }

        TextArea logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setPrefHeight(200);

        startBtn.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Create New Player");
            dialog.setHeaderText("Enter your player name:");
            dialog.setContentText("Player name:");
            dialog.showAndWait().ifPresent(playerName -> {
                if (!playerName.trim().isEmpty()) {
                    if (PlayerManager.playerExists(playerName)) {
                        Alert alert = new Alert(Alert.AlertType.WARNING, "Player already exists! Use Load Game instead.");
                        alert.showAndWait();
                    } else {
                        engine.setPlayer(new Player(playerName));
                        PlayerManager.savePlayer(engine.getPlayer(), playerName);
                        Scene mainScene = MainSceneBuilder.build(primaryStage, engine, map, enemies, logArea);
                        primaryStage.setScene(mainScene);
                    }
                }
            });
        });

        loadBtn.setOnAction(e -> {
            List<String> players = PlayerManager.getAvailablePlayerNames();
            if (players.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "No saved players found.");
                alert.showAndWait();
                return;
            }

            ChoiceDialog<String> dialog = new ChoiceDialog<>(players.get(0), players);
            dialog.setTitle("Load Player");
            dialog.setHeaderText("Select a player to load:");
            dialog.setContentText("Player:");
            dialog.showAndWait().ifPresent(playerName -> {
                Player loaded = PlayerManager.loadPlayer(playerName);
                if (loaded != null) {
                    engine.setPlayer(loaded);
                    engine.loadGame("players" + java.io.File.separator + playerName + "_game.dat");
                    Scene mainScene = MainSceneBuilder.build(primaryStage, engine, engine.getMap(), enemies, logArea);
                    primaryStage.setScene(mainScene);
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load player.");
                    alert.showAndWait();
                }
            });
        });

        exitBtn.setOnAction(e -> primaryStage.close());

        menuBox.getChildren().addAll(title, startBtn, loadBtn, exitBtn);

        // === Stack Layout ===
        StackPane root = new StackPane();
        root.getChildren().addAll(background, menuBox);

        return new Scene(root, 900, 800);
    }
}