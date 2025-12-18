package rpg.ui;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import rpg.core.GameEngine;
import rpg.skills.Skill;

public class SkillTreeScene {

    /** ✅ Added for SceneManager compatibility */
    public static Scene build(Stage stage, GameEngine engine) {
        VBox root = new VBox(10);
        root.setStyle("-fx-padding: 20;");

        Label title = new Label("Skill Tree");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        root.getChildren().add(title);

        Label bonusLabel = new Label("Bonus Points: " + engine.getPlayer().getBonusPoints());
        root.getChildren().add(bonusLabel);

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);

        int row = 0;
        int col = 0;

        for (Skill skill : engine.getPlayer().getSkills().values()) {
            VBox card = new VBox(5);
            card.setStyle("-fx-border-color: gray; -fx-padding: 10;");

            Label nameLabel = new Label(skill.getName());
            Tooltip tooltip = new Tooltip(skill.getDescription());
            Tooltip.install(nameLabel, tooltip);

            Label statusLabel = new Label(skill.isUnlocked()
                    ? "Unlocked"
                    : "Locked (" + skill.getRequiredBonusPoints() + " pts)");

            Button unlockBtn = new Button("Unlock");
            unlockBtn.setDisable(skill.isUnlocked());

            unlockBtn.setOnAction(e -> {
                boolean success = engine.unlockPlayerSkill(skill.getName());
                bonusLabel.setText("Bonus Points: " + engine.getPlayer().getBonusPoints());
                if (success) {
                    statusLabel.setText("Unlocked");
                    unlockBtn.setDisable(true);
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING,
                            "Not enough bonus points or already unlocked.");
                    alert.showAndWait();
                }
            });

            card.getChildren().addAll(nameLabel, statusLabel, unlockBtn);
            grid.add(card, col, row);

            col++;
            if (col > 2) {
                col = 0;
                row++;
            }
        }

        Button backBtn = new Button("⬅️ Back to Game");
        backBtn.setOnAction(e -> SceneManager.switchToGame());

        root.getChildren().addAll(grid, backBtn);
        return new Scene(root, 600, 400);
    }

    /** Optional direct display method */
    public static void show(Stage stage, GameEngine engine) {
        Scene scene = build(stage, engine);
        stage.setScene(scene);
        stage.setTitle("Skill Tree");
        stage.show();
    }
}