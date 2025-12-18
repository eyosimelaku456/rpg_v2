package rpg.quest;

import rpg.characters.Player;
import java.io.Serializable;

public class Quest implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String title;
    private final String description;
    private final String objective;
    private final int rewardGold;
    private boolean completed;

    private final String targetType;     // e.g. "Goblin"
    private final int requiredKills;     // e.g. 1
    private int currentKills = 0;        // starts at 0

    public Quest(String title, String description, String objective, int rewardGold,
                 String targetType, int requiredKills) {
        this.title = title;
        this.description = description;
        this.objective = objective;
        this.rewardGold = rewardGold;
        this.targetType = targetType;
        this.requiredKills = requiredKills;
        this.completed = false;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getObjective() { return objective; }
    public int getRewardGold() { return rewardGold; }
    public boolean isCompleted() { return completed; }

    public void complete() {
        this.completed = true;
    }

    // ✅ Fix: check if quest is ready to complete
    public boolean checkConditions(Player player) {
        return !completed && currentKills >= requiredKills;
    }

    public void recordKill(String enemyType) {
        if (!completed && enemyType.equalsIgnoreCase(targetType)) {
            currentKills++;
            if (currentKills >= requiredKills) {
                complete();
            }
        }
    }

    public String getProgress() {
        return currentKills + "/" + requiredKills + " " + targetType + " defeated";
    }

    @Override
    public String toString() {
        return title + " - " + (completed ? "✅ Completed" : "🕒 Active") +
               "\nObjective: " + objective +
               "\nProgress: " + getProgress() +
               "\nReward: " + rewardGold + " gold";
    }
}