package rpg.ui;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import rpg.core.GameEngine;
import rpg.quest.Quest;

import java.util.List;

/**
 * QuestLogScene displays the player's active quests and their status.
 */
public class QuestLogScene {

    /**
     * Builds and returns the quest log scene.
     *
     * @param stage  the primary stage
     * @param engine the game engine
     * @return the constructed quest log scene
     */
    public static Scene build(Stage stage, GameEngine engine) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #e6f7ff, #ffffff);");

        Label title = new Label("📜 Quest Log");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");

        ListView<String> questList = new ListView<>();
        questList.setStyle("-fx-control-inner-background: #ffffff; -fx-text-fill: #333;");

        List<Quest> quests = engine.getQuestManager().getActiveQuests();
        java.util.Set<String> addedQuests = new java.util.HashSet<>();

        for (Quest q : quests) {
            String key = q.getTitle();
            if (!addedQuests.contains(key)) {
                addedQuests.add(key);
                String status = q.isCompleted() ? "✅ Completed" : "🕒 In Progress";
                String description = q.getDescription();
                String reward = "Reward: " + q.getRewardGold() + " gold";
                questList.getItems().add(q.getTitle() + " — " + status + "\n  " + description + "\n  " + reward);
            }
        }

        Button backButton = new Button("⬅️ Back to Game");
        backButton.setStyle("-fx-padding: 10; -fx-font-size: 12;");
        backButton.setOnAction(e -> SceneManager.switchToGame());

        Label activeLabel = new Label("Active Quests: " + addedQuests.size());
        activeLabel.setStyle("-fx-font-weight: bold;");

        root.getChildren().addAll(title, activeLabel, questList, new Separator(), backButton);
        return new Scene(root, 700, 600);
    }

    /**
     * Displays the quest log scene directly.
     *
     * @param stage  the primary stage
     * @param engine the game engine
     */
    public static void show(Stage stage, GameEngine engine) {
        Scene scene = build(stage, engine);
        stage.setScene(scene);
        stage.setTitle("Quest Log");
        stage.show();
    }
}