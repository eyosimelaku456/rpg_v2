package rpg.characters;

import rpg.inventory.Inventory;

public class Orc extends Character {
    public Orc() {
        super("Orc", 120, 15, 5); // High HP and attack
        setInventory(new Inventory());

    addSkill("Smash", "Deals 15 damage", 0);
    unlockSkill("Smash");

    addSkill("Roar", "Reduces target's defense", 0);
    unlockSkill("Roar");
    }
}