package rpg.inventory;
import java.io.Serializable;

public class Weapon extends Item implements Serializable{
    private static final long serialVersionUID = 1L;
    private int bonusDamage;
    private int requiredBonusPoints;

    public Weapon(String name, int bonusDamage) {
        super(name);
        this.bonusDamage = bonusDamage;
        this.requiredBonusPoints = 0; // default: no requirement
    }

    public Weapon(String name, int bonusDamage, int requiredBonusPoints) {
        super(name);
        this.bonusDamage = bonusDamage;
        this.requiredBonusPoints = requiredBonusPoints;
    }

    public int getBonusDamage() {
        return bonusDamage;
    }

    public int getRequiredBonusPoints() {
        return requiredBonusPoints;
    }
}