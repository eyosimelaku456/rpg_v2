package rpg.characters;
import rpg.inventory.Inventory;
import rpg.ai.EnemyAI;



public class GoblinChief extends Enemy{
    public GoblinChief(EnemyAI ai) {
        super("Goblin Chief", 80, 20, 5, ai);
        setInventory(new Inventory());

        addSkill("Smash", "Deals 15 damage", 0);
        unlockSkill("Smash");

        addSkill("Goblin Rage", "Boosts attack by 10", 0);
        unlockSkill("Goblin Rage");
    }

    @Override
    public boolean useSkill(String name, Character target) {
        switch (name.toLowerCase()) {
            case "smash" -> {
                target.takeDamage(15);
                return true;
            }
            case "goblin rage" -> {
                this.attack += 10;
                return true;
            }
        }
        return super.useSkill(name, target);
    }
     @Override
    public boolean isBoss() {
        return true;
    }

}