package rpg.ui;

import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import rpg.characters.Character;
import rpg.characters.Goblin;
import rpg.characters.Player;
import rpg.core.GameEngine;
import rpg.combat.BattleManager;
import rpg.inventory.HealthPotion;
import rpg.inventory.IronSword;
import rpg.inventory.Item;
import rpg.characters.GoblinChief;
import rpg.characters.Mage;

import javafx.util.Duration;

import java.util.*;

public class BattleScene {
    public static void show(Stage stage, GameEngine engine) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        try {
            Image bgImage = new Image(BattleScene.class.getResourceAsStream("/images/menu_bg.jpeg"));
            BackgroundImage bg = new BackgroundImage(bgImage, BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                    new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true));
            root.setBackground(new Background(bg));
        } catch (Exception ignored) {
            root.setStyle("-fx-background-color: #2b2b2b;");
        }

        Label title = new Label("⚔️ Battle Arena");
        title.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: white;");

        TextArea battleLog = new TextArea();
        battleLog.setEditable(false);
        battleLog.setPrefHeight(350);
        battleLog.setStyle("-fx-padding: 10; -fx-font-size: 12; -fx-text-fill: #00ff00; -fx-control-inner-background: #1a1a1a;");
        battleLog.setPrefWidth(600);

        // Mode toggle: manual vs random enemy selection
        ToggleGroup modeToggle = new ToggleGroup();
        RadioButton manualMode = new RadioButton("Manual Selection");
        RadioButton randomMode = new RadioButton("Random Encounter");
        manualMode.setToggleGroup(modeToggle);
        randomMode.setToggleGroup(modeToggle);
        manualMode.setSelected(true);
        

        // Enemy stats display
        Label enemyStats = new Label("Enemy: (none selected)");
        Label playerStats = new Label("Player: (none)");
        Character[] enemyHolder = new Character[1];

        // Difficulty selector
        ComboBox<String> difficultySelector = new ComboBox<>();
        difficultySelector.getItems().addAll("Easy", "Medium", "Hard");
        difficultySelector.setValue("Medium");

        // Enemy selector
        ComboBox<String> enemySelector = new ComboBox<>();
        enemySelector.setPrefWidth(220);
        
        List<rpg.characters.Character> engineEnemies = engine.getEnemies();
        if (engineEnemies != null && !engineEnemies.isEmpty()) {
            for (rpg.characters.Character c : engineEnemies) {
                enemySelector.getItems().add(c.getName());
            }
        } else {
            enemySelector.getItems().add("Goblin");
        }
        enemySelector.setPromptText("Choose your enemy");
        
        enemySelector.setOnAction(evt -> {
            String selectedName = enemySelector.getValue();
            if (selectedName == null) return;
            List<rpg.characters.Character> enemies = engine.getEnemies();
            if (enemies == null) return;

            for (rpg.characters.Character c : enemies) {
                if (selectedName.equals(c.getName())) {
                    enemyHolder[0] = c;
                    updateEnemyStats(enemyStats, c);
                    engine.setCurrentEnemy(c);
                    break;
                }
            }
        });

        // Button styling
        String buttonStyle = "-fx-font-size: 12; -fx-padding: 10 20; -fx-font-weight: bold; "
                + "-fx-background-color: linear-gradient(to right, #ff6b6b, #ee5a6f); "
                + "-fx-text-fill: white; -fx-border-radius: 5; -fx-background-radius: 5;";
        String hoverStyle = "-fx-background-color: linear-gradient(to right, #ff8787, #ff6b6b);";

        // Buttons
        Button startBattleBtn = new Button("⚔️ Start Battle");
        Button attackBtn = new Button("🗡️ Attack");
        Button useSkillBtn = new Button("✨ Use Skill");
        Button useItemBtn = new Button("🧪 Use Item");
        Button nextTurnBtn = new Button("🔁 Next Turn");
        Button viewInventoryBtn = new Button("🎒 View Inventory");
        Button backBtn = new Button("← Back to Main");

        for (Button btn : Arrays.asList(startBattleBtn, attackBtn, useSkillBtn, useItemBtn, nextTurnBtn, viewInventoryBtn, backBtn)) {
            btn.setStyle(buttonStyle);
            btn.setOnMouseEntered(evt -> {
                btn.setStyle(buttonStyle + hoverStyle);
                btn.setEffect(new Glow(0.8));
            });
            btn.setOnMouseExited(evt -> {
                btn.setStyle(buttonStyle);
                btn.setEffect(null);
            });
        }

        // Battle manager reference
        BattleManager[] bm = new BattleManager[1];

        // Start battle logic
        startBattleBtn.setOnAction(e -> {
            Character enemy = null;

            if (randomMode.isSelected()) {
                List<Character> enemies = engine.getEnemies();
                if (enemies != null && !enemies.isEmpty()) {
                    List<Character> tmp = new ArrayList<>(enemies);
                    Collections.shuffle(tmp);
                    enemy = tmp.get(0);
                } else {
                    enemy = new Goblin();
                }
            } else {
                String selectedName = enemySelector.getValue();
                if (selectedName != null && !selectedName.isBlank()) {
                    List<Character> enemies = engine.getEnemies();
                    if (enemies != null) {
                        for (Character c : enemies) {
                            if (selectedName.equals(c.getName())) {
                                enemy = c;
                                break;
                            }
                        }
                    }
                    if (enemy == null) {
                        enemy = new Goblin();
                    }
                } else {
                    battleLog.appendText("No enemy selected. Defaulting to Goblin.\n");
                    enemy = new Goblin();
                }
            }

            enemyHolder[0] = enemy;
            engine.setCurrentEnemy(enemy);

            String difficulty = difficultySelector.getValue();
            int hpBoost = switch (difficulty) {
                case "Easy" -> 0;
                case "Medium" -> 20;
                case "Hard" -> 50;
                default -> 0;
            };
            enemy.setHp(enemy.getHp() + hpBoost);
            battleLog.appendText("Difficulty [" + difficulty + "] applied. Enemy HP boosted by " + hpBoost + ".\n");

            enemyStats.setText("Enemy: " + enemy.getName() + " (HP=" + enemy.getHp() + ")");
            battleLog.appendText("Battle started against " + enemy.getName() + " [" + difficulty + "]\n");

            bm[0] = new BattleManager();
            bm[0].startBattle(engine.getPlayer(), enemy);
            battleLog.appendText("Turn order: " + bm[0].getTurnOrder().stream()
                .map(Character::getName)
                .reduce((a, b) -> a + " -> " + b)
                .orElse("") + "\n");

            bm[0].setOnTurnLog(msg -> Platform.runLater(() -> battleLog.appendText(msg + "\n")));

            // Set up end-of-battle callback
            bm[0].setOnBattleEnd((winner, loser) -> {
                Platform.runLater(() -> {
                    battleLog.appendText(loser.getName() + " was defeated by " + winner.getName() + "!\n");
                    // Cleanup engine state
                    if (engine.getEnemies() != null) {
                        engine.getEnemies().remove(loser);
                    }
                    engine.setCurrentEnemy(null);
                    updateEnemyStats(enemyStats, null);
                    updatePlayerStats(playerStats, engine.getPlayer());
                });
            });
        });

        // Player attack
        attackBtn.setOnAction(e -> {
            Character enemy = enemyHolder[0];
            if (enemy == null) {
                battleLog.appendText("No enemy selected.\n");
                return;
            }

            engine.getPlayer().attack(enemy);
            battleLog.appendText("You attacked " + enemy.getName() + "!\n");
            updateEnemyStats(enemyStats, enemy);
            updatePlayerStats(playerStats, engine.getPlayer());
            checkBattleEnd(engine.getPlayer(), enemy, battleLog, engine, stage);
        });

        // Player uses skill
        useSkillBtn.setOnAction(e -> {
            Character enemy = enemyHolder[0];
            if (enemy == null) {
                battleLog.appendText("No enemy selected.\n");
                return;
            }

            List<String> skills = engine.getPlayer().getUnlockedSkillNames();
            if (skills.isEmpty()) {
                battleLog.appendText("No unlocked skills available.\n");
                return;
            }

            ChoiceDialog<String> dialog = new ChoiceDialog<>(skills.get(0), skills);
            dialog.setTitle("Use Skill");
            dialog.setHeaderText("Choose a skill to use:");
            dialog.setContentText("Skill:");
            dialog.showAndWait().ifPresent(skillName -> {
                boolean success = engine.getPlayer().useSkill(skillName, enemy);
                battleLog.appendText(success ? "✨ Used skill: " + skillName + "\n"
                        : "⚠️ Failed to use skill: " + skillName + "\n");
                updateEnemyStats(enemyStats, enemy);
                updatePlayerStats(playerStats, engine.getPlayer());
                checkBattleEnd(engine.getPlayer(), enemy, battleLog, engine, stage);
            });
        });

        // Player uses item
        useItemBtn.setOnAction(e -> {
            Character player = engine.getPlayer();
            if (player.getInventory().hasItem("Health Potion")) {
                boolean used = player.getInventory().useItem("Health Potion", player);
                battleLog.appendText(used
                    ? player.getName() + " used a Health Potion!\n"
                    : "Failed to use Health Potion.\n");
                updatePlayerStats(playerStats, player);
            } else {
                battleLog.appendText("No Health Potion available.\n");
            }
        });

        // View inventory
        viewInventoryBtn.setOnAction(e -> {
            Character player = engine.getPlayer();
            List<Item> items = player.getInventory().getAll();
            battleLog.appendText("Inventory:\n");
            if (items.isEmpty()) {
                battleLog.appendText("- (empty)\n");
            } else {
                for (Item item : items) {
                    battleLog.appendText("- " + item.getName() + "\n");
                }
            }
        });
        // Next turn logic
        nextTurnBtn.setOnAction(e -> {
            Character enemy = enemyHolder[0];
            if (enemy == null || bm[0] == null || !bm[0].isBattleActive()) {
                battleLog.appendText("No active battle.\n");
                return;
            }

            bm[0].nextTurn();
            updateEnemyStats(enemyStats, enemy);
            updatePlayerStats(playerStats, engine.getPlayer());
            checkBattleEnd(engine.getPlayer(), enemy, battleLog, engine, stage);
        });

        // Return to main game scene
        backBtn.setOnAction(e -> SceneManager.switchToGame());

        // Layout
        HBox actionButtons = new HBox(10, attackBtn, useSkillBtn, useItemBtn, nextTurnBtn);
        actionButtons.setPadding(new Insets(10));

        ScrollPane logScroll = new ScrollPane(battleLog);
        logScroll.setFitToWidth(true);
        VBox.setVgrow(logScroll, javafx.scene.layout.Priority.ALWAYS);

        root.getChildren().addAll(
            title,
            manualMode, randomMode,
            difficultySelector,
            enemySelector,
            startBattleBtn,
            playerStats,
            enemyStats,
            actionButtons,
            viewInventoryBtn,
            backBtn,
            logScroll
        );

        stage.setScene(new Scene(root, 700, 900));
        stage.show();
    }

    private static void updatePlayerStats(Label label, Character player) {
        if (player == null) {
            label.setText("Player: (none)");
        } else {
            label.setText("Player: " + player.getName() + " (HP=" + player.getHp() + ")");
        }
    }

    private static void updateEnemyStats(Label label, Character enemy) {
        if (enemy == null) {
            label.setText("Enemy: (none selected)");
        } else {
            label.setText("Enemy: " + enemy.getName() + " (HP=" + enemy.getHp() + ")");
        }
    }

    private static void checkBattleEnd(Character player, Character enemy, TextArea log, GameEngine engine, Stage stage) {
        if (enemy == null || player == null) return;

        if (enemy.getHp() <= 0) {
            log.appendText("You defeated " + enemy.getName() + "!\n");

            if (engine.getQuestManager() != null) {
                engine.getQuestManager().checkProgress(engine);
                for (String update : engine.getQuestManager().getRecentUpdates()) {
                    log.appendText(update + "\n");
                }
            }

            if (player instanceof Player p) {
                p.addXp(50);
                p.addGold(20);
                log.appendText("Earned 50 XP and 20 gold.\n");

                // Loot drop
                Item[] loot = {
                    new IronSword(),
                    new HealthPotion(),
                    new Item("Gold Ring") {
                        @Override
                        public void use(Character user, Character target) {
                            // No effect
                        }
                    }
                };
                Item reward = loot[new Random().nextInt(loot.length)];
                p.getInventory().add(reward);
                log.appendText("Loot drop: " + reward.getName() + "\n");
            }

            // Cleanup engine state
            try {
                if (engine.getEnemies() != null) engine.getEnemies().remove(enemy);
            } catch (Exception ignored) {}
            try {
                engine.setCurrentEnemy(null);
            } catch (Exception ignored) {}

            // Show alert and return to game
            Alert endAlert = new Alert(Alert.AlertType.INFORMATION, "Victory! Returning to game.");
            endAlert.showAndWait();
            SceneManager.switchToGame();

        } else if (player.getHp() <= 0) {
            log.appendText("You were defeated!\n");
            Alert defeatAlert = new Alert(Alert.AlertType.WARNING, "You have been defeated by " + enemy.getName() + "!");
            defeatAlert.showAndWait();
            SceneManager.switchToGame();
        }
    }
}