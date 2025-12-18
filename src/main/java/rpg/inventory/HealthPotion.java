package rpg.inventory;

import rpg.characters.Character;

public class HealthPotion extends Item {
    public HealthPotion() {
        super("Health Potion");
        this.description = "Restores 20 HP when used. Essential for survival in combat.";
    }

    @Override
    public void use(Character user, Character target) {
        user.setHp(user.getHp() + 20); // heal effect
    }
}