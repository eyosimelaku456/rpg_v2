package rpg.characters;
import rpg.ai.SimpleAI;
import rpg.inventory.Inventory;
public class Goblin extends Enemy {
    public Goblin() {
        super("Goblin", 30, 6, 1, new SimpleAI());
        setInventory(new Inventory());

    addSkill("Smash", "Deals 15 damage", 0);
    unlockSkill("Smash");

    addSkill("Goblin Heal", "Restores 10 HP", 0);
    unlockSkill("Goblin Heal");

    }
}
