package rpg.effects;
import rpg.characters.Character;
import java.io.Serializable;

public abstract class StatusEffect implements Serializable {
    private static final long serialVersionUID = 1L;
    public abstract void tick(Character target);
}
