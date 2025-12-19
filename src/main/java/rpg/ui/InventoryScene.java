package rpg.ui;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;

import rpg.core.GameEngine;
import rpg.characters.Character;
import rpg.characters.Goblin;
import rpg.inventory.Item;
import rpg.inventory.Potion;
import rpg.inventory.Weapon;
import rpg.ui.SkillTreeScene;

import java.util.List;
import java.util.Objects;

public class InventoryScene {

    /** Entry point for SceneManager */
    public static Scene build(Stage stage, GameEngine engine) {
        TextArea logArea = new TextArea();
        logArea.setPrefHeight(120);

        // Prefer real enemies if available; fallback to a Goblin
        List<Character> enemies = engine.getEnemies();
        if (enemies == null || enemies.isEmpty()) {
            enemies = List.of(new Goblin());
        }

        VBox root = new VBox();
        Scene scene = new Scene(root, 700, 800);
        show(stage, engine, logArea, enemies, root);
        return scene;
    }

    /** Populates the inventory scene layout */
    public static void show(Stage stage, GameEngine engine, TextArea logArea, List<Character> enemyObjects, VBox root) {
        root.getChildren().clear();
        root.setSpacing(16);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #eceff1, #ffffff);");

        // Header
        Label header = new Label("🎒 Inventory");
        header.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Player status
        Label goldLabel = new Label("Gold: " + safeGold(engine));
        Label equippedLabel = new Label("Equipped: " +
            (engine.getPlayer() != null && engine.getPlayer().getEquippedWeapon() != null
                ? engine.getPlayer().getEquippedWeapon().getName()
                : "None"));

        // Inventory list + details
        Label inventoryHeader = new Label("Inventory");
        inventoryHeader.setStyle("-fx-font-weight: bold;");
        ListView<String> inventoryView = new ListView<>();
        updateInventoryList(inventoryView, engine);

        Label itemDetails = new Label("Select an item to see details");
        itemDetails.setWrapText(true);
        inventoryView.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            int index = newVal.intValue();
            if (index >= 0 && engine.getPlayer() != null && engine.getPlayer().getInventory() != null) {
                Item item = engine.getPlayer().getInventory().get(index);
                String details = "";
                if (item instanceof Weapon weapon) {
                    details = "🗡️ Weapon: " + weapon.getName() + "\nBonus Damage: " + weapon.getBonusDamage();
                } else if (item instanceof Potion potion) {
                    details = "🧪 Potion: " + potion.getName() + "\nHeal Amount: " + potion.getHealAmount();
                } else {
                    details = "📦 Item: " + item.getName();
                }
                details += "\n\nDescription:\n" + item.getDescription();
                itemDetails.setText(details);
            }
        });

        // Dropdowns
        ComboBox<String> itemDropdown = new ComboBox<>();
        itemDropdown.setPromptText("Select item to use");
        itemDropdown.setMinWidth(220);
        updateItemDropdown(itemDropdown, engine);

        ComboBox<String> skillDropdown = new ComboBox<>();
        skillDropdown.setPromptText("Select skill to use");
        updateSkillDropdown(skillDropdown, engine);

        ComboBox<String> unlockDropdown = new ComboBox<>();
        unlockDropdown.setPromptText("Unlockable skills");
        if (engine.getPlayer() != null) {
            for (var entry : engine.getPlayer().getSkills().entrySet()) {
                if (!entry.getValue().isUnlocked()) {
                    String costStr = " (Costs: " + entry.getValue().getRequiredBonusPoints() + " BP)";
                    unlockDropdown.getItems().add(entry.getKey() + costStr);
                }
            }
        }

        ComboBox<String> statDropdown = new ComboBox<>();
        statDropdown.setPromptText("Choose stat");
        statDropdown.getItems().setAll("attack", "defense", "hp");

        // Buttons
        Button equipBtn = new Button("🗡️ Equip Selected (from list)");
        Button unequipBtn = new Button("Unequip Weapon");
        Button useItemBtn = new Button("🧪 Use Selected Item");
        Button useSkillBtn = new Button("🧠 Use Selected Skill");
        Button unlockSkillBtn = new Button("🔓 Unlock Selected Skill");
        Button upgradeStatBtn = new Button("📈 Upgrade Selected Stat");
        Button skillTreeBtn = new Button("🌳 View Skill Tree");
        Button backButton = new Button("⬅️ Back to Game");

        // Tooltips
        equipBtn.setTooltip(new Tooltip("Equip a weapon selected in the inventory list"));
        unequipBtn.setTooltip(new Tooltip("Remove currently equipped weapon"));
        useItemBtn.setTooltip(new Tooltip("Use the item selected in the dropdown"));
        useSkillBtn.setTooltip(new Tooltip("Use the skill selected in the dropdown"));
        unlockSkillBtn.setTooltip(new Tooltip("Unlock the selected skill"));
        upgradeStatBtn.setTooltip(new Tooltip("Upgrade the selected player stat"));
        skillTreeBtn.setTooltip(new Tooltip("View your skill tree"));
        backButton.setTooltip(new Tooltip("Return to the main game"));

        // Actions
        equipBtn.setOnAction(e -> {
            int index = inventoryView.getSelectionModel().getSelectedIndex();
            if (index >= 0 && engine.getPlayer() != null && engine.getPlayer().equipWeapon(index)) {
                Weapon equipped = engine.getPlayer().getEquippedWeapon();
                equippedLabel.setText("Equipped: " + (equipped != null ? equipped.getName() : "None"));
                logArea.appendText("Equipped: " + (equipped != null ? equipped.getName() : "None") + "\n");
            } else {
                logArea.appendText("Select a weapon from the list to equip.\n");
            }
        });

        unequipBtn.setOnAction(e -> {
            if (engine.getPlayer() == null) {
                logArea.appendText("No player found.\n");
                return;
            }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Unequip current weapon?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    engine.getPlayer().equip(null);
                    equippedLabel.setText("Equipped: None");
                    logArea.appendText("Weapon unequipped.\n");
                }
            });
        });

        useItemBtn.setOnAction(e -> {
            String selected = itemDropdown.getValue();
            if (selected == null) {
                logArea.appendText("No item selected in dropdown.\n");
                return;
            }
            boolean success = useItemByName(engine, selected);
            updateInventoryList(inventoryView, engine);
            updateItemDropdown(itemDropdown, engine);
            equippedLabel.setText("Equipped: " +
                (engine.getPlayer() != null && engine.getPlayer().getEquippedWeapon() != null
                    ? engine.getPlayer().getEquippedWeapon().getName()
                    : "None"));
            logArea.appendText(success ? "Used item: " + selected + "\n" : "Failed to use item: " + selected + "\n");
        });

        useSkillBtn.setOnAction(e -> {
            String skillName = skillDropdown.getValue();
            if (skillName == null) {
                logArea.appendText("No skill selected in dropdown.\n");
                return;
            }
            Character target = enemyObjects.stream()
                .filter(ch -> engine.getPlayer() == null || !Objects.equals(ch.getName(), engine.getPlayer().getName()))
                .findFirst()
                .orElse(new Goblin());
            boolean success = engine.getPlayer() != null && engine.getPlayer().useSkill(skillName, target);
            logArea.appendText(success ? "Used skill: " + skillName + "\n" : "Failed to use skill: " + skillName + "\n");
        });

        unlockSkillBtn.setOnAction(e -> {
            String skillNameFull = unlockDropdown.getValue();
            if (skillNameFull == null) {
                logArea.appendText("No skill selected to unlock.\n");
                return;
            }
            String skillName = skillNameFull.split(" \\(")[0];
            boolean unlocked = engine.unlockPlayerSkill(skillName);
            logArea.appendText(unlocked ? "Unlocked skill: " + skillName + "\n" : "Failed to unlock: " + skillName + "\n");
            updateSkillDropdown(skillDropdown, engine);
        });

        upgradeStatBtn.setOnAction(e -> {
            String stat = statDropdown.getValue();
            if (stat == null) {
                logArea.appendText("No stat selected to upgrade.\n");
                return;
            }
            boolean upgraded = engine.getPlayer() != null && engine.getPlayer().upgradeStat(stat);
            logArea.appendText(upgraded ? "Upgraded stat: " + stat + "\n" : "Failed to upgrade stat: " + stat + "\n");
            goldLabel.setText("Gold: " + safeGold(engine)); // In case upgrade costs gold
        });

        skillTreeBtn.setOnAction(e -> SkillTreeScene.show(stage, engine));
        backButton.setOnAction(e -> SceneManager.switchToGame());

        // Sections
        VBox statusSection = new VBox(6, header, goldLabel, equippedLabel);
        VBox inventorySection = new VBox(10, inventoryHeader, inventoryView, itemDetails);
        HBox dropdownRow1 = new HBox(12, itemDropdown, useItemBtn);
        HBox dropdownRow2 = new HBox(12, skillDropdown, useSkillBtn);
        HBox dropdownRow3 = new HBox(12, unlockDropdown, unlockSkillBtn, statDropdown, upgradeStatBtn);
        VBox actionsSection = new VBox(10,
            new Label("Actions"),
            equipBtn, unequipBtn,
            new Separator(),
            dropdownRow1,
            dropdownRow2,
            dropdownRow3,
            new Separator(),
            skillTreeBtn,
            backButton
        );
        VBox logSection = new VBox(6, new Label("Log"), logArea);

        statusSection.setPadding(new Insets(10));
        inventorySection.setPadding(new Insets(10));
        actionsSection.setPadding(new Insets(10));
        logSection.setPadding(new Insets(10));

        root.getChildren().addAll(
            statusSection,
            new Separator(),
            inventorySection,
            new Separator(),
            actionsSection,
            new Separator(),
            logSection
        );

        // Diagnostics for empty inventory
        int invSize = engine.getPlayer() != null && engine.getPlayer().getInventory() != null
            ? engine.getPlayer().getInventory().getAll().size() : -1;
        System.out.println("[InventoryScene] Inventory size: " + invSize);
        if (invSize <= 0) {
            logArea.appendText("Inventory is empty. Ensure items are added during initialization or loaded from save.\n");
        }
    }

    /** Update inventory ListView */
    private static void updateInventoryList(ListView<String> listView, GameEngine engine) {
        listView.getItems().clear();
        if (engine.getPlayer() == null || engine.getPlayer().getInventory() == null) {
            System.out.println("[InventoryScene] Player or Inventory is null.");
            return;
        }
        List<Item> items = engine.getPlayer().getInventory().getAll();
        for (Item item : items) {
            String label = item instanceof Weapon ? "🗡️ " :
                           item instanceof Potion ? "🧪 " : "📦 ";
            listView.getItems().add(label + item.getName());
        }
    }

    /** Update item dropdown with item names */
    private static void updateItemDropdown(ComboBox<String> dropdown, GameEngine engine) {
        dropdown.getItems().clear();
        if (engine.getPlayer() == null || engine.getPlayer().getInventory() == null) return;
        for (Item item : engine.getPlayer().getInventory().getAll()) {
            dropdown.getItems().add(item.getName());
        }
    }

    /** Update skill dropdown with unlocked skills */
    private static void updateSkillDropdown(ComboBox<String> dropdown, GameEngine engine) {
        dropdown.getItems().clear();
        if (engine.getPlayer() == null) return;
        dropdown.getItems().addAll(engine.getPlayer().getUnlockedSkillNames());
    }

    /** Use item by name helper (find first match by name) */
    private static boolean useItemByName(GameEngine engine, String itemName) {
        if (engine.getPlayer() == null || engine.getPlayer().getInventory() == null) return false;
        List<Item> items = engine.getPlayer().getInventory().getAll();
        for (int i = 0; i < items.size(); i++) {
            Item it = items.get(i);
            if (Objects.equals(it.getName(), itemName)) {
                engine.useItem(i);
                return true;
            }
        }
        return false;
    }

    /** Guard for gold read */
    private static int safeGold(GameEngine engine) {
        return engine.getPlayer() != null ? engine.getPlayer().getGold() : 0;
    }
}