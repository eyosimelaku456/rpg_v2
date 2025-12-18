package rpg.core;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import rpg.characters.Player;
import rpg.interfaces.Usable;
import rpg.inventory.Item;
import rpg.inventory.Weapon;
import rpg.inventory.Potion;
import rpg.map.GameMap;
import rpg.quest.Quest;
import rpg.quest.QuestManager;
import rpg.shop.Shop;
import rpg.utils.GameLogger;

public class GameEngine {
    private Player player;
    private GameMap map;
    private Shop shop;
    private QuestManager questManager;
    private List<rpg.characters.Character> enemies = new ArrayList<>();
    private rpg.characters.Character currentEnemy;
    // Initialize game components
    public GameEngine() {
        this.player = new Player("Hero");
        this.map = new GameMap(5, 5);
        this.shop = new Shop();
        this.questManager = new QuestManager();
    }

    // Set player instance
    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public GameMap getMap() {
        return map;
    }

    public Shop getShop() {
        return shop;
    }

    public void setQuestManager(QuestManager questManager) {
        this.questManager = questManager;
    }

    public QuestManager getQuestManager() {
        return questManager;
    }

    public void addQuest(Quest quest) {
        questManager.addQuest(quest);
    }

    public void resetGame() {
        this.player = new Player("Hero");
        this.map = new GameMap(5, 5);
        this.shop = new Shop();
        this.questManager = new QuestManager();
        this.enemies.clear();
    }

    public String getGameStatus() {
        return "Player: " + player.getName() +
               " | HP: " + player.getHp() +
               " | Gold: " + player.getGold() +
               " | Tile: " + map.getCurrentTileInfo();
    }

    public String getDetailedStatus() {
        return "Player: " + player.getName() +
               " | Level: " + player.getLevel() +
               " | HP: " + player.getHp() +
               " | Bonus Points: " + player.getBonusPoints() +
               " | Gold: " + player.getGold() +
               " | Weapon: " + (player.getEquippedWeapon() != null ? player.getEquippedWeapon().getName() : "None") +
               " | Tile: " + map.getCurrentTileInfo();
    }

    public boolean unlockPlayerSkill(String skillName) {
        boolean success = player.unlockSkill(skillName);
        System.out.println(success ? "Skill unlocked: " + skillName : "Failed to unlock skill: " + skillName);
        return success;
    }

    public boolean usePlayerSkill(String skillName, rpg.characters.Character target) {
        boolean success = player.useSkill(skillName, target);
        System.out.println(success ? "Used skill: " + skillName : "Failed to use skill: " + skillName);
        return success;
    }
      public void setShop(Shop shop) {
        this.shop = shop;
    }
    public void useItem(int index) {
        if (index < 0 || index >= player.getInventory().size()) {
            System.out.println("Invalid item index.");
            return;
        }

        Item item = player.getInventory().get(index);
        if (item instanceof Usable usable) {
            int requiredBonus = 0;
            if (item instanceof Weapon weapon) {
                requiredBonus = weapon.getRequiredBonusPoints();
            } else if (item instanceof Potion potion) {
                requiredBonus = potion.getRequiredBonusPoints();
            }

            if (player.getBonusPoints() >= requiredBonus) {
                usable.use(player, player); // using on self for now
                player.getInventory().removeAt(index);
                System.out.println("Used item: " + item.getName());
                triggerEvent("Use " + item.getName());
            } else {
                System.out.println("Not enough bonus points to use " + item.getName());
            }
        } else {
            System.out.println("Item is not usable.");
        }
    }

  
    public void addEnemy(rpg.characters.Character enemy) {
        enemies.add(enemy);
    }

     public List<rpg.characters.Character> getEnemies() {
        return enemies;
    }
  public void setCurrentEnemy(rpg.characters.Character enemy) {
        this.currentEnemy = enemy;
    }

public rpg.characters.Character getCurrentEnemy() {
        return currentEnemy;
    }
    
    public void addItemToInventory(Item item) {
        player.getInventory().add(item);
    }

    public void removeItemFromInventory(int index) {
        if (index >= 0 && index < player.getInventory().size()) {
            player.getInventory().removeAt(index);
        } else {
            System.out.println("Invalid inventory index.");
        }
    }

    public void triggerEvent(String event) {
        checkQuestProgress(event);
    }

    public void saveGame(String filename) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            SaveData data = new SaveData(player, player.getInventory(), map, questManager);
            out.writeObject(data);
            GameLogger.log("Game saved to " + filename);
        } catch (IOException e) {
            GameLogger.log("Save failed: " + e.getMessage());
        }
    }

    public void loadGame(String filename) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            SaveData data = (SaveData) in.readObject();
            this.player = data.getPlayer();
            this.map = data.getMap();
            this.questManager = data.getQuestManager();
            GameLogger.log("Game loaded from " + filename);
        } catch (IOException | ClassNotFoundException e) {
            GameLogger.log("Load failed: " + e.getMessage());
        }
    }

    public void checkQuestProgress(String event) {
        for (Quest q : questManager.getActiveQuests()) {
            if (!q.isCompleted() && q.getObjective().toLowerCase().contains(event.toLowerCase())) {
                questManager.completeQuest(q, this);
                System.out.println("Quest completed: " + q.getTitle());
            }
        }
    }
}