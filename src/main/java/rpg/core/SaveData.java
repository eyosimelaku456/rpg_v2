package rpg.core;

import java.io.Serializable;
import rpg.characters.Player;
import rpg.inventory.Inventory;
import rpg.map.GameMap;
import rpg.quest.QuestManager;

public class SaveData implements Serializable {
    private Player player;
    private Inventory inventory;
    private GameMap map;
    private QuestManager questManager;

    public SaveData(Player player, Inventory inventory, GameMap map, QuestManager questManager) {
        this.player = player;
        this.inventory = inventory;
        this.map = map;
        this.questManager = questManager;
    }

    public Player getPlayer() { return player; }
    public Inventory getInventory() { return inventory; }
    public GameMap getMap() { return map; }
    public QuestManager getQuestManager() { return questManager; }
}