package rpg.ai;

import rpg.characters.Character;

public class SimpleAI implements EnemyAI {
    @Override
    public void decide(Character self, Character player) {
        // Optional: legacy or alternate behavior
        self.attack(player);
    }

    @Override
    public void takeTurn(Character self, Character target) {
        // Main AI logic used in Enemy.takeTurn()
        self.attack(target);
    }
}