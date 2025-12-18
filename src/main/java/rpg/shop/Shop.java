package rpg.shop;

import rpg.characters.Player;
import rpg.inventory.Item;
import rpg.inventory.Weapon;
import rpg.inventory.Potion;
import rpg.utils.ConfigLoader;

import java.util.*;

public class Shop {
    private final List<ShopItem> items = new ArrayList<>();

    public Shop() {
        try {
            List<Map<String, String>> config = ConfigLoader.loadConfig("/config/shop.txt");
            for (Map<String, String> entry : config) {
                String name = entry.getOrDefault("Name", "?");
                String type = entry.getOrDefault("Type", "?");
                int price = Integer.parseInt(entry.getOrDefault("Price", "10"));

                Item item = switch (type.toLowerCase()) {
                    case "weapon" -> new Weapon(name, Integer.parseInt(entry.getOrDefault("Bonus", "0")));
                    case "potion" -> new Potion(name, Integer.parseInt(entry.getOrDefault("Heal", "0")));
                    default -> null;
                };

                if (item != null) items.add(new ShopItem(item, price));
            }
        } catch (Exception e) {
            System.out.println("Failed to load shop items: " + e.getMessage());
        }
    }

    public List<ShopItem> getItems() {
        return items;
    }

    public boolean buy(int index, Player player) {
        if (index < 0 || index >= items.size()) return false;

        ShopItem shopItem = items.get(index);
        int price = shopItem.getPrice();

        if (player.spendGold(price)) {
            player.getInventory().add(shopItem.getItem());
            return true;
        }
        return false;
    }
}