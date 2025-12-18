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
        root.setStyle("-fx-background-color: #e6f7ff;");

        Label title = new Label("📜 Quest Log");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        ListView<String> questList = new ListView<>();
        List<Quest> quests = engine.getQuestManager().getActiveQuests();

        for (Quest q : quests) {
            String status = q.isCompleted() ? "✅ Completed" : "🕒 In Progress";
            questList.getItems().add(q.getTitle() + " — " + status);
        }

        Button backButton = new Button("⬅️ Back to Game");
       backButton.setOnAction(e -> SceneManager.switchToGame());


        root.getChildren().addAll(title, questList, new Separator(), backButton);
        return new Scene(root, 600, 500);
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