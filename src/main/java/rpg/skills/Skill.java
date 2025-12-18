package rpg.skills;

public class Skill {
    private String name;
    private String description;
    private int requiredBonusPoints;
    private boolean unlocked;

    public Skill(String name, String description, int requiredBonusPoints) {
        this.name = name;
        this.description = description;
        this.requiredBonusPoints = requiredBonusPoints;
        this.unlocked = false;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getRequiredBonusPoints() {
        return requiredBonusPoints;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }
}