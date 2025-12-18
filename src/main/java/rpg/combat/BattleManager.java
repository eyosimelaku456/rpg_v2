package rpg.combat;

import rpg.characters.Character;
import rpg.characters.Player;
import rpg.inventory.Item;
import rpg.utils.GameLogger;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class BattleManager {
    private Queue<Character> turnOrder = new LinkedList<>();
    private AttackStrategy strategy = new BasicAttack();

    private Character player;
    private Character enemy;
    private boolean active = false;

    private BiConsumer<Character, Character> onBattleEnd;
    private Consumer<String> onTurnLog;

    // Callbacks
    public void setOnTurnLog(Consumer<String> callback) { this.onTurnLog = callback; }
    public void setOnBattleEnd(BiConsumer<Character, Character> cb) { this.onBattleEnd = cb; }

    // Start battle
    public void startBattle(Character player, Character enemy) {
        if (player == null || enemy == null) throw new IllegalArgumentException("Player or enemy is null!");
        this.player = player;
        this.enemy = enemy;
        turnOrder.clear();
        turnOrder.offer(player);
        turnOrder.offer(enemy);
        sortByInitiative();
        active = true;
        log("⚔️ Battle started: " + player.getName() + " vs " + enemy.getName());
    }

    // Sort turn order by initiative
    private void sortByInitiative() {
        List<Character> chars = new ArrayList<>(turnOrder);
        chars.sort((a, b) -> Integer.compare(b.getInitiative(), a.getInitiative()));
        turnOrder.clear();
        turnOrder.addAll(chars);
        log("Turn order: " + String.join(" -> ", chars.stream().map(Character::getName).toList()));
    }

    // Next turn
    public void nextTurn() {
        if (!active) return;
        Character actor = turnOrder.poll();
        if (actor == null) { active = false; return; }

        Character target = (actor instanceof Player) ? enemy : player;
        if (target == null) { active = false; return; }

        String actionLog = (actor instanceof Player)
                ? playerAttack(actor, target)
                : enemyTurn(actor, target);

        log(actionLog);

        if (target.getHp() <= 0) {
            log("💀 " + target.getName() + " has been defeated!");
            active = false;

            // Loot drop if player wins
            if (actor instanceof Player) {
                handleLootDrop(target, (Player) actor);
            }

            if (onBattleEnd != null) onBattleEnd.accept(actor, target);
            turnOrder.clear();
        } else {
            turnOrder.offer(actor); // only re‑enqueue if battle continues
        }
    }

    // Player attack
    private String playerAttack(Character actor, Character target) {
        strategy.execute(actor, target);
        int damage = Math.max(1, actor.getAttack() - (target.getDefense() / 2));
        target.setHp(target.getHp() - damage);
        return actor.getName() + " attacked " + target.getName() + " for " + damage + " damage!";
    }

    // Player uses skill
    public void playerUseSkill(Player player, String skillName, Character target) {
        boolean success = player.useSkill(skillName, target);
        String actionLog = success
                ? "✨ " + player.getName() + " used skill: " + skillName + " on " + target.getName() + "!"
                : "⚠️ " + player.getName() + " failed to use skill: " + skillName;

        log(actionLog);

        if (success && target.getHp() <= 0) {
            log("💀 " + target.getName() + " has been defeated by skill!");
            active = false;

            // Loot drop if player wins
            handleLootDrop(target, player);

            if (onBattleEnd != null) onBattleEnd.accept(player, target);
            turnOrder.clear();
        } else {
            turnOrder.offer(player);
        }
    }

    // Enemy AI turn
    private String enemyTurn(Character enemy, Character target) {
        // Try any unlocked skill first
        for (var entry : enemy.getSkills().entrySet()) {
            if (entry.getValue().isUnlocked()) {
                boolean success = enemy.useSkill(entry.getKey(), target);
                if (success) {
                    return enemy.getName() + " used " + entry.getKey() + " on " + target.getName() + "!";
                }
            }
        }
        // Fallback to basic attack
        strategy.execute(enemy, target);
        int damage = Math.max(1, enemy.getAttack() - (target.getDefense() / 2));
        target.setHp(target.getHp() - damage);
        return enemy.getName() + " attacked " + target.getName() + " for " + damage + " damage!";
    }

    // Loot drop logic
    private void handleLootDrop(Character defeatedEnemy, Player player) {
        // Example: gold reward
        int goldReward = defeatedEnemy.getGoldReward(); // define in Character subclasses
        player.addGold(goldReward);
        log("💰 " + player.getName() + " received " + goldReward + " gold!");

        // Example: item drops
        List<Item> loot = defeatedEnemy.getLoot(); // define getLoot() in enemy subclasses
        if (loot != null && !loot.isEmpty()) {
            for (Item item : loot) {
                player.getInventory().add(item);
                log("📦 " + player.getName() + " looted: " + item.getName());
            }
        } else {
            log("📭 No loot dropped.");
        }
    }

    // Logging helper
    private void log(String msg) {
        GameLogger.log(msg);
        if (onTurnLog != null) onTurnLog.accept(msg);
    }

    // Accessors
    public boolean isBattleActive() { return active; }
    public Character getPlayer() { return player; }
    public Character getEnemy() { return enemy; }
    public List<Character> getTurnOrder() { return new ArrayList<>(turnOrder); }
    public void setStrategy(AttackStrategy s) { this.strategy = s; }
}