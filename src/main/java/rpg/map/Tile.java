
package rpg.map;

import java.io.Serializable;

public class Tile implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String type;
    private boolean walkable;
    private boolean hasEnemy;
    private String questId;
    private boolean questCompleted = false;

    public Tile(String type, boolean walkable, boolean hasEnemy) {
        this.type = type;
        this.walkable = walkable;
        this.hasEnemy = hasEnemy;
        this.questId = null;
    }

    public String getType() { return type; }
    public boolean isWalkable() { return walkable; }


    public boolean hasEnemy() {
    return hasEnemy;
    }
    public void setHasEnemy(boolean hasEnemy) {
        this.hasEnemy = hasEnemy;
    }

    public void setQuestId(String questId) {
        this.questId = questId;
    }

    public String getQuestId() {
        return questId;
    }

    public boolean hasQuest() {
        return questId != null && !questCompleted;
    }

    public void completeQuest() {
        this.questCompleted = true;
    }

    public String getSymbol() {
        if (hasEnemy) return "⚔️";
        if (hasQuest()) return "❗";
        return switch (type.toLowerCase()) {
            case "grass" -> "🌿";
            case "water" -> "🌊";
            case "mountain" -> "⛰️";
            case "forest" -> "🌲";
            case "shop" -> "🏪";
            case "quest" -> "🧭";
            default -> "⬜";
        };
    }

    @Override
    public String toString() {
        return type + (hasEnemy ? " [Enemy]" : "");
    }
}