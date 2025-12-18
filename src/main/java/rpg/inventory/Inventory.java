package rpg.inventory;

import rpg.characters.Character;
import java.io.Serializable;
import java.util.*;

public class Inventory implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<Item> items = new ArrayList<>();

    public void add(Item item) {
        if (item != null) items.add(item);
    }

    public boolean remove(Item item) {
        return items.remove(item);
    }

    public boolean hasItem(String itemName) {
        return items.stream().anyMatch(i -> i.getName().equalsIgnoreCase(itemName));
    }

    public boolean useItem(String itemName, Character target) {
        for (Item item : items) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                item.use(target, target);
                items.remove(item);
                return true;
            }
        }
        return false;
    }

    public boolean removeAt(int index) {
        if (index >= 0 && index < items.size()) {
            items.remove(index);
            return true;
        }
        return false;
    }

    public Item get(int index) {
        if (index >= 0 && index < items.size()) {
            return items.get(index);
        }
        return null;
    }

    public boolean set(int index, Item item) {
        if (index >= 0 && index < items.size() && item != null) {
            items.set(index, item);
            return true;
        }
        return false;
    }

    public List<Item> getAll() {
        return Collections.unmodifiableList(items);
    }

    public int size() {
        return items.size();
    }

    public void clear() {
        items.clear();
    }
}