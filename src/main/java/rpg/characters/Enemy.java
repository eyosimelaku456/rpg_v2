package rpg.characters;

import rpg.ai.EnemyAI;
import rpg.inventory.Inventory;
import rpg.characters.Character;

/**
 * Base class for all AI-controlled enemies.
 * Extends Character and includes an EnemyAI strategy.
 */
public abstract class Enemy extends Character {
    protected EnemyAI ai;

    /**
     * Constructs an enemy with stats and AI behavior.
     *
     * @param name  Enemy name
     * @param hp    Max HP
     * @param atk   Attack power
     * @param def   Defense value
     * @param ai    AI behavior strategy
     */
    protected Enemy(String name, int hp, int atk, int def, EnemyAI ai) {
        super(name, hp, atk, def);
        this.ai = ai;
        this.inventory = new Inventory(); // Ensure inventory is initialized
    }

    /**
     * Returns the AI controller for this enemy.
     */
    public EnemyAI getAi() {
        return ai;
    }

    /**
     * Executes the enemy's turn using its AI.
     * Overrides Character's default behavior.
     */
    @Override
    public void takeTurn(Character target) {
        if (ai != null) {
            ai.takeTurn(this, target);
        } else {
            super.takeTurn(target); // fallback to basic attack
        }
    }

    /**
     * Optional override for boss enemies.
     */
    public boolean isBoss() {
        return false;
    }
}