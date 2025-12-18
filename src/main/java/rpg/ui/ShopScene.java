package rpg.ui;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import rpg.core.GameEngine;
import rpg.shop.ShopItem;

/**
 * ShopScene builds the shop interface where players can buy items.
 * Supports both modular scene construction and direct display.
 */
public class ShopScene {

    /**
     * Builds and returns the shop scene.
     *
     * @param stage  the primary stage
     * @param engine the game engine
     * @return the constructed shop scene
     */
    public static Scene build(Stage stage, GameEngine engine) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #fffbe6;");

        Label title = new Label("🛍️ Welcome to the Shop");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label goldLabel = new Label("Gold: " + engine.getPlayer().getGold());

        ListView<String> shopList = new ListView<>();
        for (ShopItem item : engine.getShop().getItems()) {
            shopList.getItems().add(item.getName() + " - " + item.getPrice() + "g");
        }

        Button buyBtn = new Button("🛒 Buy Selected Item");
        buyBtn.setOnAction(e -> {
            int index = shopList.getSelectionModel().getSelectedIndex();
            if (index >= 0) {
                boolean success = engine.getShop().buy(index, engine.getPlayer());
                if (success) {
                    goldLabel.setText("Gold: " + engine.getPlayer().getGold());
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Item purchased!");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Not enough gold or invalid item.");
                    alert.showAndWait();
                }
            }
        });

        Button backBtn = new Button("⬅️ Back to Game");
        backBtn.setOnAction(e -> SceneManager.switchToGame());

        root.getChildren().addAll(
            title,
            new Label("Shop Items:"), shopList,
            goldLabel,
            buyBtn,
            new Separator(),
            backBtn
        );

        return new Scene(root, 600, 600);
    }

    /**
     * Displays the shop scene directly.
     *
     * @param stage  the primary stage
     * @param engine the game engine
     */
    public static void show(Stage stage, GameEngine engine) {
        Scene scene = build(stage, engine);
        stage.setScene(scene);
        stage.setTitle("Shop");
        stage.show();
    }
}