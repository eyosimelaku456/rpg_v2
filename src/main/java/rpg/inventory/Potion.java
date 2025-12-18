package rpg.inventory;

import rpg.characters.Character;
import rpg.interfaces.Usable;
import java.io.Serializable;

public class Potion extends Item implements Serializable {
    private static final long serialVersionUID = 1L;
 
    private int healAmount;
    private int requiredBonusPoints;

    public Potion(String name, int healAmount) {
        super(name);
        this.healAmount = healAmount;
        this.requiredBonusPoints = 0; // default: no requirement
    }

    public Potion(String name, int healAmount, int requiredBonusPoints) {
        super(name);
        this.healAmount = healAmount;
        this.requiredBonusPoints = requiredBonusPoints;
    }

    public int getHealAmount() {
        return healAmount;
    }

    public int getRequiredBonusPoints() {
        return requiredBonusPoints;
    }
@Override
    public void use(Character user, Character target) {
    target.heal(healAmount);
    System.out.println(user.getName() + " used " + name + " on " + target.getName() +
                       ", healing for " + healAmount + " HP.");
    }
   }