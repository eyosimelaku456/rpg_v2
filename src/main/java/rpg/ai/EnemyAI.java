package rpg.ai;
import rpg.characters.Character;
public interface EnemyAI {
    void decide(Character self, Character player);
    void takeTurn(Character self, Character target);

}
