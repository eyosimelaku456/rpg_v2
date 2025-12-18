
package rpg.map;

public class Tile {
    private final String type;
    private boolean walkable;
    private boolean hasEnemy;

    public Tile(String type, boolean walkable, boolean hasEnemy) {
        this.type = type;
        this.walkable = walkable;
        this.hasEnemy = hasEnemy;
    }

    public String getType() { return type; }
    public boolean isWalkable() { return walkable; }


    public boolean hasEnemy() {
    return hasEnemy;
    }
    public void setHasEnemy(boolean hasEnemy) {
        this.hasEnemy = hasEnemy;
    }

    public String getSymbol() {
        if (hasEnemy) return "⚔️";
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