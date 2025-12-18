package rpg.inventory;

import rpg.interfaces.Usable;
import rpg.characters.Character;
import java.io.Serializable;

public abstract class Item implements Usable, Serializable {
    private static final long serialVersionUID = 1L;

    protected final String name;

    protected Item(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public void use(Character user, Character target) {
        // Default no-op
    }
}