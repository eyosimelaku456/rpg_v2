package rpg.inventory;

import rpg.interfaces.Usable;
import rpg.characters.Character;
import java.io.Serializable;

public abstract class Item implements Usable, Serializable {
    private static final long serialVersionUID = 1L;

    protected final String name;
    protected String description;

    protected Item(String name) {
        this.name = name;
        this.description = "No description available.";
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void use(Character user, Character target) {
        // Default no-op
    }
}