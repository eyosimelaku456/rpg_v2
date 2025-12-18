package rpg.quest;

import rpg.utils.GameLogger;
import rpg.core.GameEngine;
import rpg.characters.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QuestManager implements Serializable {
    private static final long serialVersionUID = 1L;
    private final List<Quest> activeQuests = new ArrayList<>();
    private final List<Quest> completedQuests = new ArrayList<>();
    private final List<String> recentUpdates = new ArrayList<>();

    // Add a new quest
    public void addQuest(Quest quest) {
        activeQuests.add(quest);
        GameLogger.log("Quest added: " + quest.getTitle());
    }

    // Complete a quest and reward the player
    public void completeQuest(Quest q, GameEngine engine) {
        if (!activeQuests.contains(q)) return;

        activeQuests.remove(q);
        completedQuests.add(q);
        q.complete();

        int reward = q.getRewardGold();
        engine.getPlayer().addGold(reward);
        GameLogger.log("Player earned " + reward + " gold for completing quest: " + q.getTitle());
        recentUpdates.add("✅ Quest completed: " + q.getTitle() + " (+ " + reward + " gold)");
    }

    // Check progress and auto-complete quests
    public void checkProgress(GameEngine engine) {
        Player player = engine.getPlayer();
        for (Quest q : new ArrayList<>(activeQuests)) {
            if (!q.isCompleted() && q.checkConditions(player)) {
                completeQuest(q, engine);
            }
        }
    }

    // Get recent updates for UI feedback
    public List<String> getRecentUpdates() {
        List<String> copy = new ArrayList<>(recentUpdates);
        recentUpdates.clear();
        return copy;
    }

    public List<Quest> getActiveQuests() {
        return activeQuests;
    }

    public List<Quest> getCompletedQuests() {
        return completedQuests;
    }

    public void clearAll() {
        activeQuests.clear();
        completedQuests.clear();
        GameLogger.log("All quests cleared.");
    }

    public boolean hasQuest(String title) {
        return activeQuests.stream().anyMatch(q -> q.getTitle().equalsIgnoreCase(title)) ||
               completedQuests.stream().anyMatch(q -> q.getTitle().equalsIgnoreCase(title));
    }
}