package rpg.ui;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import rpg.core.GameEngine;
import rpg.shop.ShopItem;
import rpg.inventory.Weapon;
import rpg.inventory.Potion;

public class ShopScene {

    public static Scene build(Stage stage, GameEngine engine) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #fffbe6, #fff9c4);");

        Label title = new Label("🛍️ Welcome to the Shop");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #f57f17;");

        HBox statusBar = new HBox(20);
        statusBar.setStyle("-fx-background-color: #fff8e1; -fx-padding: 10; -fx-border-color: #fbc02d; -fx-border-width: 1;");
        Label goldLabel = new Label("💰 Gold: " + engine.getPlayer().getGold());
        goldLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");
        statusBar.getChildren().add(goldLabel);

        HBox contentBox = new HBox(20);
        contentBox.setPadding(new Insets(10));

        VBox shopList = new VBox(10);
        shopList.setPrefWidth(300);
        shopList.setStyle("-fx-border-color: #fbc02d; -fx-border-width: 1; -fx-padding: 10;");
        Label listTitle = new Label("Available Items:");
        listTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 12;");
        shopList.getChildren().add(listTitle);

        ScrollPane listScroll = new ScrollPane();
        VBox listContainer = new VBox(5);
        listContainer.setPadding(new Insets(5));

        Label detailsLabel = new Label("Select an item to view details");
        detailsLabel.setWrapText(true);
        detailsLabel.setStyle("-fx-text-fill: #333;");

        VBox detailsBox = new VBox(15);
        detailsBox.setPrefWidth(350);
        detailsBox.setStyle("-fx-border-color: #4caf50; -fx-border-width: 2; -fx-padding: 15; -fx-background-color: #f1f8e9;");
        Label detailsTitle = new Label("Item Details");
        detailsTitle.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #2e7d32;");
        detailsBox.getChildren().addAll(detailsTitle, detailsLabel);

        for (int i = 0; i < engine.getShop().getItems().size(); i++) {
            ShopItem item = engine.getShop().getItems().get(i);
            Button itemBtn = new Button(item.getIcon() + " " + item.getName() + " - " + item.getPrice() + "g");
            itemBtn.setStyle("-fx-padding: 10; -fx-font-size: 11; -fx-text-alignment: left;");
            itemBtn.setPrefWidth(280);
            itemBtn.setAlignment(Pos.CENTER_LEFT);

            final int index = i;
            itemBtn.setOnAction(e -> {
                ShopItem selected = engine.getShop().getItems().get(index);
                String details = selected.getIcon() + " " + selected.getName() + "\n";
                details += "Type: " + selected.getType() + "\n";
                details += "Price: " + selected.getPrice() + " gold\n\n";
                details += "Description:\n" + selected.getItem().getDescription() + "\n\n";

                if (selected.getItem() instanceof Weapon w) {
                    details += "Bonus Damage: +" + w.getBonusDamage();
                } else if (selected.getItem() instanceof Potion p) {
                    details += "Healing Power: " + p.getHealAmount() + " HP";
                }
                detailsLabel.setText(details);
            });

            listContainer.getChildren().add(itemBtn);
        }

        listScroll.setContent(listContainer);
        listScroll.setFitToWidth(true);
        shopList.getChildren().add(listScroll);
        VBox.setVgrow(listScroll, Priority.ALWAYS);

        contentBox.getChildren().addAll(shopList, detailsBox);
        HBox.setHgrow(shopList, Priority.ALWAYS);
        HBox.setHgrow(detailsBox, Priority.ALWAYS);

        Button buyBtn = new Button("🛒 Buy Selected Item");
        buyBtn.setStyle("-fx-padding: 10 20; -fx-font-size: 12; -fx-background-color: #4caf50; -fx-text-fill: white; -fx-font-weight: bold;");
        buyBtn.setOnAction(e -> {
            String selectedText = detailsLabel.getText();
            if (!selectedText.equals("Select an item to view details")) {
                for (int i = 0; i < engine.getShop().getItems().size(); i++) {
                    ShopItem item = engine.getShop().getItems().get(i);
                    if (selectedText.contains(item.getName())) {
                        boolean success = engine.getShop().buy(i, engine.getPlayer());
                        if (success) {
                            goldLabel.setText("💰 Gold: " + engine.getPlayer().getGold());
                            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Item purchased! 🎉");
                            alert.showAndWait();
                            detailsLabel.setText("Select an item to view details");
                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Not enough gold!");
                            alert.showAndWait();
                        }
                        break;
                    }
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please select an item first!");
                alert.showAndWait();
            }
        });

        Button backBtn = new Button("⬅️ Back to Game");
        backBtn.setStyle("-fx-padding: 10 20; -fx-font-size: 12; -fx-background-color: #ff9800; -fx-text-fill: white; -fx-font-weight: bold;");
        backBtn.setOnAction(e -> SceneManager.switchToGame());

        HBox buttonBox = new HBox(10, buyBtn, backBtn);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10));

        root.getChildren().addAll(title, statusBar, contentBox, buttonBox);

        return new Scene(root, 900, 700);
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