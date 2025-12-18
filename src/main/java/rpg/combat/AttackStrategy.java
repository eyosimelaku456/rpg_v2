package rpg.combat;
import rpg.characters.Character;
public interface AttackStrategy {
    void execute(Character attacker, Character defender);
}
