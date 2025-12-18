package rpg.characters;
import rpg.inventory.Inventory;

public class Mage extends Character {
    public Mage(String name) {
        super(name, 70, 10, 3);
        setInventory(new Inventory());

        addSkill("Fireball", "Deals 25 damage", 0);
        unlockSkill("Fireball");

        addSkill("Mana Shield", "Boosts defense by 5", 0);
        unlockSkill("Mana Shield");
    }

    @Override
    public boolean useSkill(String name, Character target) {
        switch (name.toLowerCase()) {
            case "fireball" -> {
                target.takeDamage(25);
                return true;
            }
            case "mana shield" -> {
                this.defense += 5;
                return true;
            }
        }
        return super.useSkill(name, target);
    }
}