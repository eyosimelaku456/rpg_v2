package rpg.ui;

// JavaFX UI
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;

// Core systems
import rpg.core.GameEngine;
import rpg.utils.GameLogger;
import rpg.utils.ConfigLoader;

// Characters
import rpg.characters.Goblin;
import rpg.characters.GoblinChief;
import rpg.characters.Mage;
import rpg.characters.Orc;
import rpg.characters.Player;
import rpg.characters.Character;
import rpg.ai.SimpleAI;

// Inventory
import rpg.inventory.Weapon;
import rpg.inventory.Potion;

// World & Quests
import rpg.map.GameMap;
import rpg.quest.Quest;
import rpg.quest.QuestManager;
import rpg.shop.Shop;

// Java
import java.util.*;

public class MainApp extends Application {

    private GameEngine engine;
    private GameMap gameMap;
    private List<Character> enemyObjects;

    public MainApp() {
        this.engine = new GameEngine();
    }

    public MainApp(GameEngine engine) {
        this.engine = engine;
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            GameLogger.log("JavaFX app started");
            primaryStage.setTitle("RPG - OOP Project (JavaFX)");

            // Initialize game state
            gameMap = new GameMap(5, 5);
            enemyObjects = new ArrayList<>();
            initializeGame(engine, gameMap, enemyObjects);

            // Initialize scene manager
            SceneManager.init(primaryStage, engine);

            // Launch into main menu
            Scene menuScene = MainMenuScene.build(primaryStage, engine, gameMap, enemyObjects, engine.getQuestManager());
            primaryStage.setScene(menuScene);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
  

    private void initializeGame(GameEngine engine, GameMap gameMap, List<Character> enemyObjects) {
        Player player = new Player("Eyosias");
        player.setInitiative(10);
    
        engine.setPlayer(player);

        engine.getEnemies().clear();
    
        if (enemyObjects != null) {
           enemyObjects.clear();
        }

        QuestManager questManager = new QuestManager();
        engine.setQuestManager(questManager);
        questManager.addQuest(new Quest(
            "Find the Goblin",
            "Defeat a goblin in the forest",
            "Kill 1 Goblin",
            50,
            "Goblin",
            1
        ));
        Shop shop = new Shop();
        engine.setShop(shop);


        try {
            List<Map<String, String>> enemies = ConfigLoader.loadConfig("/config/enemies.txt");
            for (Map<String, String> e : enemies) {
                String type = e.getOrDefault("Type", "Goblin").toLowerCase();
                rpg.characters.Character enemy =
                 switch (type) {
                    case "orc" -> new Orc();
                    case "mage" -> new Mage("Enemy Mage");
                    case "goblinchief" -> new GoblinChief(new SimpleAI());
                    default -> new Goblin();
                };
                   switch (type) {
                    case "orc" -> enemy.setInitiative(9);
                    case "mage" -> enemy.setInitiative(11);
                    case "goblinchief" -> enemy.setInitiative(12);
                    default -> enemy.setInitiative(7); // goblin
                }

                engine.addEnemy(enemy);
                enemyObjects.add(enemy);
            }
        } catch (Exception ex) {
            System.err.println("Error loading enemies: " + ex.getMessage());
        }

        try {
            List<Map<String, String>> items = ConfigLoader.loadConfig("/config/items.txt");
            for (Map<String, String> i : items) {
                String name = i.getOrDefault("Name", "?");
                String type = i.getOrDefault("Type", "?");
                if (type.equalsIgnoreCase("Weapon")) {
                    int bonus = Integer.parseInt(i.getOrDefault("Bonus", "0"));
                    engine.getPlayer().getInventory().add(new Weapon(name, bonus));
                } else if (type.equalsIgnoreCase("Potion")) {
                    int heal = Integer.parseInt(i.getOrDefault("Heal", "0"));
                    engine.getPlayer().getInventory().add(new Potion(name, heal));
                }
            }
        } catch (Exception ex) {
            System.err.println("Error loading items: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}