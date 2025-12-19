package rpg.utils;

import rpg.characters.Player;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class PlayerManager {
    private static final String PLAYERS_DIR = "players";

    static {
        try {
            Files.createDirectories(Paths.get(PLAYERS_DIR));
        } catch (IOException e) {
            GameLogger.log("Failed to create players directory");
        }
    }

    public static void savePlayer(Player player, String playerName) {
        try {
            String filename = PLAYERS_DIR + File.separator + playerName + ".dat";
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename));
            out.writeObject(player);
            out.close();
            GameLogger.log("Player saved: " + playerName);
        } catch (IOException e) {
            GameLogger.log("Failed to save player: " + e.getMessage());
        }
    }

    public static Player loadPlayer(String playerName) {
        try {
            String filename = PLAYERS_DIR + File.separator + playerName + ".dat";
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename));
            Player player = (Player) in.readObject();
            in.close();
            GameLogger.log("Player loaded: " + playerName);
            return player;
        } catch (IOException | ClassNotFoundException e) {
            GameLogger.log("Failed to load player: " + e.getMessage());
            return null;
        }
    }

    public static List<String> getAvailablePlayerNames() {
        List<String> names = new ArrayList<>();
        try {
            File dir = new File(PLAYERS_DIR);
            if (dir.exists() && dir.isDirectory()) {
                File[] files = dir.listFiles((d, name) -> name.endsWith(".dat"));
                if (files != null) {
                    for (File file : files) {
                        String playerName = file.getName();
                        playerName = playerName.substring(0, playerName.length() - 4);
                        names.add(playerName);
                    }
                }
            }
        } catch (Exception e) {
            GameLogger.log("Error reading player files: " + e.getMessage());
        }
        return names;
    }

    public static boolean playerExists(String playerName) {
        return new File(PLAYERS_DIR + File.separator + playerName + ".dat").exists();
    }

    public static void deletePlayer(String playerName) {
        try {
            String filename = PLAYERS_DIR + File.separator + playerName + ".dat";
            Files.delete(Paths.get(filename));
            GameLogger.log("Player deleted: " + playerName);
        } catch (IOException e) {
            GameLogger.log("Failed to delete player: " + e.getMessage());
        }
    }
}
