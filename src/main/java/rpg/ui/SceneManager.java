package rpg.ui;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.scene.control.TextArea;

import rpg.core.GameEngine;
import rpg.ui.MainMenuScene;
import rpg.ui.MainSceneBuilder;
import rpg.ui.ShopScene;
import rpg.ui.InventoryScene;
import rpg.ui.QuestLogScene;
import rpg.ui.SkillTreeScene;

/**
 * SceneManager handles switching between different JavaFX scenes.
 * Centralizes navigation logic for modular scene transitions.
 */
public class SceneManager {

    private static Stage primaryStage;
    private static GameEngine engine;
    private static TextArea logArea = new TextArea(); // Shared log area

    /** Initializes the SceneManager with the primary stage and game engine */
    public static void init(Stage stage, GameEngine gameEngine) {
        primaryStage = stage;
        engine = gameEngine;
    }

    /** Switches to the given scene immediately */
    public static void switchTo(Scene scene) {
        primaryStage.setScene(scene);
    }

    /** Switches to the given scene with a fade transition */
    public static void switchWithFade(Scene scene) {
        primaryStage.setScene(scene);
        FadeTransition ft = new FadeTransition(Duration.millis(500), scene.getRoot());
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
    }

    /** Switches to the main menu scene */
    public static void switchToMainMenu() {
        Scene scene = MainMenuScene.build(primaryStage, engine, engine.getMap(), engine.getEnemies(), engine.getQuestManager());
        switchWithFade(scene);
        primaryStage.setTitle("Main Menu");
    }

    /** Switches to the main game scene */
    public static void switchToGame() {
        Scene scene = MainSceneBuilder.build(primaryStage, engine, engine.getMap(), engine.getEnemies(), logArea);
        switchWithFade(scene);
        primaryStage.setTitle("Game");
    }

    /** Shows the shop scene */
    public static void showShopScene() {
        Scene shopScene = ShopScene.build(primaryStage, engine);
        switchWithFade(shopScene);
        primaryStage.setTitle("Shop");
    }

    /** Shows the inventory scene */
    public static void switchToInventory() {
    Scene scene = InventoryScene.build(primaryStage, engine);
    primaryStage.setScene(scene);
}

    /** Shows the quest log scene */
    public static void showQuestLogScene() {
        Scene questScene = QuestLogScene.build(primaryStage, engine);
        switchWithFade(questScene);
        primaryStage.setTitle("Quest Log");
    }

    /** Shows the skill tree scene */
    public static void showSkillTreeScene() {
        Scene skillScene = SkillTreeScene.build(primaryStage, engine);
        switchWithFade(skillScene);
        primaryStage.setTitle("Skill Tree");
    }
}