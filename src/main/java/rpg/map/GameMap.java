package rpg.map;

import rpg.characters.Character;
import rpg.characters.Goblin;
import rpg.characters.Orc;

import java.util.ArrayList;
import java.util.List;

public class GameMap {
    private final int rows;
    private final int cols;
    private final Tile[][] tiles;
    private int playerRow;
    private int playerCol;
    private final List<Character> activeEnemies = new ArrayList<>();

    public GameMap(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.tiles = new Tile[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                tiles[r][c] = new Tile("grass", true, false);
            }
        }

        this.playerRow = 0;
        this.playerCol = 0;
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public int getPlayerRow() { return playerRow; }
    public int getPlayerCol() { return playerCol; }
    public Tile getPlayerTile() { return tiles[playerRow][playerCol]; }
    public Tile getTile(int row, int col) { return tiles[row][col]; }

    public boolean movePlayer(String direction) {
        int newRow = playerRow;
        int newCol = playerCol;

        switch (direction.toLowerCase()) {
            case "up" -> newRow--;
            case "down" -> newRow++;
            case "left" -> newCol--;
            case "right" -> newCol++;
        }

        if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols && tiles[newRow][newCol].isWalkable()) {
            playerRow = newRow;
            playerCol = newCol;
            return true;
        }

        return false;
    }

    public String getCurrentTileInfo() {
        Tile tile = getPlayerTile();
        return "You are standing on a " + tile.getType() +
               (tile.hasEnemy() ? " with an enemy nearby!" : "") +
               (tile.isWalkable() ? " (walkable)" : " (blocked)");
    }

    public void advanceTurn() {
        activeEnemies.clear();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Tile tile = tiles[r][c];
                if (tile.isWalkable() && Math.random() < 0.02) {
                    tile.setHasEnemy(true);
                    Character enemy = Math.random() < 0.5 ? new Goblin() : new Orc();
                    activeEnemies.add(enemy);
                }
            }
        }
    }

    public List<Character> getActiveEnemies() {
        return activeEnemies;
    }
}