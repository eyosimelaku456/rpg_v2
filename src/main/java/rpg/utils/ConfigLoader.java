package rpg.utils;

import java.io.*;
import java.util.*;

/**
 * Simple block-based TXT config loader.
 * Format:
 * Enemy
 * Name=Goblin
 * HP=30
 * ATK=5
 * ---
 */
public class ConfigLoader {

    public static List<Map<String, String>> loadConfig(String resourcePath) throws IOException {
        List<Map<String, String>> entries = new ArrayList<>();
        Map<String, String> cur = new HashMap<>();

        // Load from classpath
        InputStream in = ConfigLoader.class.getResourceAsStream(resourcePath);
        if (in == null) {
            throw new FileNotFoundException("Could not find resource: " + resourcePath);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                if (line.equals("---")) {
                    if (!cur.isEmpty()) {
                        entries.add(new HashMap<>(cur));
                        cur.clear();
                    }
                } else if (line.contains("=")) {
                    String[] p = line.split("=", 2);
                    cur.put(p[0].trim(), p[1].trim());
                } else {
                    cur.put("Type", line);
                }
            }
        }

        if (!cur.isEmpty()) entries.add(cur);
        return entries;
    }
}