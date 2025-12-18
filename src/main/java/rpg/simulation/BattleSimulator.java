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

        List<Character> fighters = List.of(player, enemy);

        BattleManager bm = new BattleManager();
        bm.startBattle(player, enemy);

        for (int turn = 1; turn <= turns; turn++) {
            if (!bm.isBattleActive()) {
                logArea.appendText("Battle is not active. Ending simulation.\n");
                break;
            }

            bm.nextTurn();

            String status = "Turn " + turn + ": " +
                    fighters.get(0).getName() + " HP=" + fighters.get(0).getHp() + ", " +
                    fighters.get(1).getName() + " HP=" + fighters.get(1).getHp();

            if (verbose) {
                logArea.appendText(status + "\n");
                GameLogger.log(status);
            }

            if (fighters.get(0).getHp() <= 0 || fighters.get(1).getHp() <= 0) {
                logArea.appendText("Battle ended!\n");
                GameLogger.log("Battle ended!");
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