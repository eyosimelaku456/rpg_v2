package rpg.shop;

import rpg.inventory.Item;
import rpg.inventory.Weapon;
import rpg.inventory.Potion;

public class ShopItem {
    private final Item item;
    private final int price;

    public ShopItem(Item item, int price) {
        this.item = item;
        this.price = price;
    }

    public Item getItem() {
        return item;
    }

    public int getPrice() {
        return price;
    }

    public String getName() {
        return item.getName();
    }

    public String getType() {
        if (item instanceof Weapon) return "Weapon";
        if (item instanceof Potion) return "Potion";
        return "Item";
    }

    public String getIcon() {
        return switch (getType()) {
            case "Weapon" -> "🗡️";
            case "Potion" -> "🧪";
            default -> "📦";
        };
    }
}