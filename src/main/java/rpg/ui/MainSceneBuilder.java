package rpg.ui;

import javafx.animation.FadeTransition;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.util.Duration;
import java.util.List;

import rpg.core.GameEngine;
import rpg.characters.Character;
import rpg.map.GameMap;
import rpg.map.Tile;
import rpg.simulation.BattleSimulator;
import rpg.utils.GameLogger;
import rpg.ui.BattleScene;

public class MainSceneBuilder {

    public static Scene build(Stage primaryStage, GameEngine engine, GameMap gameMap,
                              List<Character> enemyObjects, TextArea logArea) {

        initializeQuestTiles(gameMap, engine);

        VBox root = new VBox(10);
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #f4f4f4;");

        // HUD Panel
        HBox hud = new HBox(20);
        hud.setPadding(new Insets(10));
        hud.setAlignment(Pos.CENTER_LEFT);
        hud.setStyle("-fx-background-color: #333;");

        Label hpLabel = new Label("❤️ HP: " + engine.getPlayer().getHp());
        Label goldLabel = new Label("💰 Gold: " + engine.getPlayer().getGold());
        Label questLabel = new Label("📜 Quests: " + engine.getQuestManager().getActiveQuests().size());

        hpLabel.setStyle("-fx-text-fill: white;");
        goldLabel.setStyle("-fx-text-fill: white;");
        questLabel.setStyle("-fx-text-fill: white;");

        hud.getChildren().addAll(hpLabel, goldLabel, questLabel);
        root.getChildren().add(hud);

        root.getChildren().add(new Label("Welcome to the RPG!"));

        Label mapStatus = new Label("Current Tile: " + gameMap.getCurrentTileInfo());
        root.getChildren().add(mapStatus);

        // Map Grid
        GridPane mapGrid = new GridPane();
        mapGrid.setHgap(5);
        mapGrid.setVgap(5);
        mapGrid.setPadding(new Insets(10));
        mapGrid.setAlignment(Pos.CENTER);
        updateMapGrid(mapGrid, gameMap);
        root.getChildren().addAll(new Label("🗺️ Map View:"), mapGrid);

        // Button Style
        String btnStyle = "-fx-background-color: #444; -fx-text-fill: white; -fx-font-weight: bold;";

        // Movement Controls
        Button upBtn = new Button("↑");
        Button downBtn = new Button("↓");
        Button leftBtn = new Button("←");
        Button rightBtn = new Button("→");
        Button questLogBtn = new Button("📜 Quest Log");
        Button saveBtn = new Button("💾 Save Game");
        Button loadBtn = new Button("📂 Load Game");
        saveBtn.setStyle(btnStyle);
        loadBtn.setStyle(btnStyle);


        upBtn.setStyle(btnStyle);
        downBtn.setStyle(btnStyle);
        leftBtn.setStyle(btnStyle);
        rightBtn.setStyle(btnStyle);
        questLogBtn.setStyle(btnStyle);

        upBtn.setOnAction(e -> movePlayer(gameMap, "up", mapStatus, mapGrid, primaryStage, engine));
        downBtn.setOnAction(e -> movePlayer(gameMap, "down", mapStatus, mapGrid, primaryStage, engine));
        leftBtn.setOnAction(e -> movePlayer(gameMap, "left", mapStatus, mapGrid, primaryStage, engine));
        rightBtn.setOnAction(e -> movePlayer(gameMap, "right", mapStatus, mapGrid, primaryStage, engine));
        questLogBtn.setOnAction(e -> SceneManager.showQuestLogScene());
        saveBtn.setOnAction(e -> engine.saveGame("savegame.dat"));
        loadBtn.setOnAction(e -> {
        engine.loadGame("savegame.dat");
        updateMapGrid(mapGrid, gameMap);
        mapStatus.setText("Current Tile: " + gameMap.getCurrentTileInfo());
        });


        HBox movementRow = new HBox(10, leftBtn, downBtn, rightBtn);
        movementRow.setAlignment(Pos.CENTER);
        VBox movementBlock = new VBox(10, upBtn, movementRow);
        movementBlock.setAlignment(Pos.CENTER);

        root.getChildren().addAll(new Label("🎮 Move Player:"), movementBlock, questLogBtn);
        HBox saveLoadButtons = new HBox(10, saveBtn, loadBtn);
        saveLoadButtons.setPadding(new Insets(10));
        root.getChildren().addAll(new Label("💾 Save/Load:"), saveLoadButtons);


        // Navigation Buttons
        Button inventorySceneBtn = new Button("🎒 Inventory");
        Button shopBtn = new Button("🏪 Visit Shop");

        inventorySceneBtn.setStyle(btnStyle);
        shopBtn.setStyle(btnStyle);

        inventorySceneBtn.setOnAction(e -> SceneManager.switchToInventory());
        shopBtn.setOnAction(e -> SceneManager.showShopScene());

        HBox navigationButtons = new HBox(10, shopBtn, inventorySceneBtn);
        navigationButtons.setPadding(new Insets(10));
        root.getChildren().addAll(new Label("🧭 Navigation:"), navigationButtons);

        // Battle Buttons
        Button battleBtn = new Button("⚔️ Start Battle");
        Button demoBattleBtn = new Button("🧪 Demo Battle");
        Button nextTurnBtn = new Button("🔁 Next Turn");

        battleBtn.setStyle(btnStyle);
        demoBattleBtn.setStyle(btnStyle);
        nextTurnBtn.setStyle(btnStyle);

        battleBtn.setOnAction(e -> BattleScene.show(primaryStage, engine));
        demoBattleBtn.setOnAction(e -> BattleSimulator.runDemo(engine, enemyObjects, logArea));
        nextTurnBtn.setOnAction(e -> {
            gameMap.advanceTurn();
            updateMapGrid(mapGrid, gameMap);
            mapStatus.setText("Current Tile: " + gameMap.getCurrentTileInfo());
            GameLogger.log("Next turn triggered.");
        });

        VBox actionButtons = new VBox(10, battleBtn, demoBattleBtn, nextTurnBtn);
        actionButtons.setPadding(new Insets(10));
        root.getChildren().addAll(new Label("⚔️ Actions:"), actionButtons);

        // Battle Log
        logArea.setEditable(false);
        logArea.setPrefHeight(200);
        ScrollPane logScroll = new ScrollPane(logArea);
        logScroll.setFitToWidth(true);
        root.getChildren().addAll(new Label("📜 Battle Log:"), logScroll);

        // Scene & Keyboard Movement
        Scene scene = new Scene(root, 900, 800);
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case UP, W -> movePlayer(gameMap, "up", mapStatus, mapGrid, primaryStage, engine);
                case DOWN, S -> movePlayer(gameMap, "down", mapStatus, mapGrid, primaryStage, engine);
                case LEFT, A -> movePlayer(gameMap, "left", mapStatus, mapGrid, primaryStage, engine);
                case RIGHT, D -> movePlayer(gameMap, "right", mapStatus, mapGrid, primaryStage, engine);
            }
        });

        // Scene Fade-In
        FadeTransition fade = new FadeTransition(Duration.seconds(1), root);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();

        // Add Enter key binding for quest triggering
        scene.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) {
                Tile currentTile = gameMap.getPlayerTile();
                if (currentTile.hasQuest()) {
                    handleQuestTrigger(currentTile, engine, logArea);
                }
            }
        });

        return scene;
    }

    private static void movePlayer(GameMap map, String direction, Label statusLabel,
                                   GridPane mapGrid, Stage primaryStage, GameEngine engine) {
        boolean moved = map.movePlayer(direction);
        if (moved) {
            Tile current = map.getPlayerTile();
            statusLabel.setText("Current Tile: " + current.toString());
            GameLogger.log("Player moved " + direction + ": " + current.toString());
            updateMapGrid(mapGrid, map);

            checkQuestProgress(engine);

            if (current.hasEnemy()) {
                handleEnemyEncounter(current, primaryStage, engine);
            }

        } else {
            statusLabel.setText("Cannot move " + direction + " — blocked or out of bounds.");
        }
    }

    private static void checkQuestProgress(GameEngine engine) {
        engine.getQuestManager().checkProgress(engine);
        for (String update : engine.getQuestManager().getRecentUpdates()) {
            GameLogger.log(update);
        }
    }

    private static void handleEnemyEncounter(Tile tile, Stage stage, GameEngine engine) {
        GameLogger.log("Enemy encountered on tile!");
        tile.setHasEnemy(false);
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "An enemy appears!");
        alert.showAndWait();
        BattleScene.show(stage, engine);
    }

    private static void updateMapGrid(GridPane grid, GameMap map) {
        grid.getChildren().clear();
        for (int row = 0; row < map.getRows(); row++) {
            for (int col = 0; col < map.getCols(); col++) {
                Tile tile = map.getTile(row, col);
                Button tileBtn = new Button(tile.getSymbol());
                tileBtn.setPrefSize(40, 40);
                tileBtn.setAlignment(Pos.CENTER);

                final int finalRow = row;
                final int finalCol = col;

                String style = "-fx-border-color: black; -fx-background-color: white; -fx-font-size: 18;";
                if (tile.hasEnemy()) {
                    style = "-fx-border-color: red; -fx-background-color: #ffe6e6; -fx-font-size: 18;";
                }
                if (map.getPlayerRow() == row && map.getPlayerCol() == col) {
                    style = "-fx-border-color: gold; -fx-background-color: yellow; -fx-font-size: 18; -fx-font-weight: bold;";
                }

                tileBtn.setStyle(style);

                Tooltip tooltip = new Tooltip("Tile: " + tile.getType() +
                    (tile.hasEnemy() ? "\nEnemy present!" : "") +
                    (tile.isWalkable() ? "\nWalkable" : "\nBlocked"));
                Tooltip.install(tileBtn, tooltip);

                grid.add(tileBtn, col, row);
            }
        }
    }

    private static void initializeQuestTiles(GameMap map, GameEngine engine) {
        rpg.quest.Quest quest1 = new rpg.quest.Quest(
            "Explore the Forest",
            "Adventure awaits in the forest",
            "Explore Forest",
            50,
            "Forest Creature",
            1
        );
        map.getTile(2, 2).setQuestId("quest_forest");
        engine.addQuest(quest1);

        rpg.quest.Quest quest2 = new rpg.quest.Quest(
            "Defeat a Goblin",
            "The goblins must be dealt with",
            "Defeat Goblin",
            100,
            "Goblin",
            1
        );
        map.getTile(1, 3).setQuestId("quest_goblin");
        engine.addQuest(quest2);
    }

    private static void handleQuestTrigger(Tile tile, GameEngine engine, TextArea logArea) {
        String questId = tile.getQuestId();
        if (questId != null) {
            logArea.appendText("Quest marker found! Press ENTER again to accept.\n");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Quest Available");
            alert.setHeaderText("A quest is available on this tile!");
            alert.setContentText("Check the Quest Log for details.");
            alert.showAndWait();
            tile.completeQuest();
        }
    }
}