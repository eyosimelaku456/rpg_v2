package rpg.inventory;
import java.io.Serializable;

import rpg.characters.Character;

public class IronSword extends Item implements  Serializable {
    private static final long serialVersionUID = 1L;
    public IronSword() {
        super("Iron Sword");
    }

    @Override
    public void use(Character user, Character target) {
        target.setHp(target.getHp() - 15); // damage effect
    }
}