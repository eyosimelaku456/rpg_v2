package rpg.rules;
public class DefaultRules implements RuleSet {
    @Override public int calculateDamage(int baseAttack,int defense) {
        return Math.max(0, baseAttack - defense);
    }
}
