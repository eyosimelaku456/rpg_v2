package rpg.inventory;

import rpg.characters.Character;

public class HealthPotion extends Item {
    public HealthPotion() {
        super("Health Potion");
    }

    @Override
    public void use(Character user, Character target) {
        user.setHp(user.getHp() + 20); // heal effect
    }
}