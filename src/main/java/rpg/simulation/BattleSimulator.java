package rpg.simulation;

import javafx.scene.control.TextArea;
import rpg.characters.Character;
import rpg.characters.Goblin;
import rpg.core.GameEngine;
import rpg.combat.BattleManager;
import rpg.utils.GameLogger;

import java.util.List;

/**
 * BattleSimulator runs demo battles between the player and enemies.
 * Used for testing combat logic, balance, and turn-based mechanics.
 */
public class BattleSimulator {

    /**
     * Runs a demo battle for a fixed number of turns.
     *
     * @param engine        the game engine containing the player
     * @param enemyObjects  list of available enemies
     * @param logArea       the UI text area to display battle logs
     * @param turns         number of turns to simulate
     * @param verbose       whether to log each action in detail
     */
    public static void runDemo(GameEngine engine, List<Character> enemyObjects, TextArea logArea,
                               int turns, boolean verbose) {

        if (logArea == null) return;
        logArea.clear();

        if (engine == null) {
            logArea.appendText("No game engine provided.\n");
            return;
        }

        Character player = engine.getPlayer();
        if (player == null) {
            logArea.appendText("No player found in engine.\n");
            return;
        }

        Character enemy = (enemyObjects == null || enemyObjects.isEmpty()) ? new Goblin() : enemyObjects.get(0);
        Character enemy2 = new Goblin();
        enemy2.setHp(enemy2.getHp());

        BattleManager bm = new BattleManager();
        bm.setOnTurnLog(msg -> logArea.appendText(msg + "\n"));
        bm.startBattle(player, enemy2);

        logArea.appendText("⚔️ DEMO BATTLE START\n");
        logArea.appendText("=" + "=".repeat(49) + "\n");

        for (int turn = 1; turn <= turns; turn++) {
            if (!bm.isBattleActive()) {
                logArea.appendText("\n✅ Battle concluded after turn " + (turn - 1) + "\n");
                break;
            }

            bm.nextTurn();

            String status = String.format("Turn %d: %s (HP=%d) vs %s (HP=%d)",
                    turn, player.getName(), player.getHp(), enemy2.getName(), enemy2.getHp());

            if (verbose) {
                logArea.appendText(status + "\n");
            }

            if (player.getHp() <= 0 || enemy2.getHp() <= 0) {
                logArea.appendText("\n" + "=".repeat(50) + "\n");
                if (player.getHp() > 0) {
                    logArea.appendText("🎉 VICTORY! Player wins!\n");
                } else {
                    logArea.appendText("💀 DEFEAT! Enemy wins!\n");
                }
                GameLogger.log("Demo battle ended!");
                break;
            }
        }
    }

    /**
     * Convenience method for 5-turn demo with logging.
     */
    public static void runDemo(GameEngine engine, List<Character> enemyObjects, TextArea logArea) {
        runDemo(engine, enemyObjects, logArea, 5, true);
    }
}