package rpg.combat;
import rpg.characters.Character;
public class BasicAttack implements AttackStrategy {
    @Override
    public void execute(Character attacker, Character defender) {
        attacker.attack(defender);
    }
}
