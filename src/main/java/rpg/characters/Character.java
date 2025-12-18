package rpg.characters;

import rpg.interfaces.Attackable;
import rpg.inventory.Inventory;
import rpg.inventory.Item;
import rpg.effects.StatusEffect;
import rpg.skills.Skill;

import java.io.Serializable;
import java.util.*;

public abstract class Character implements GameEntity, Attackable, Serializable {
    private static final long serialVersionUID = 1L;
    protected String name;
    protected int hp;
    protected int maxHp;
    protected int attack;
    protected int defense;
    protected Inventory inventory;
    protected List<StatusEffect> effects = new ArrayList<>();

    private int initiative;
    protected Map<String, Skill> skills = new HashMap<>();

    // Loot & rewards
    protected int goldReward = 0;                 // default gold reward
    protected List<Item> lootTable = new ArrayList<>(); // default loot list

    protected Character(String name, int maxHp, int attack, int defense) {
        this.name = name;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.attack = attack;
        this.defense = defense;
    }

    // --- Basic getters ---
    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getAttack() { return attack; }
    public int getDefense() { return defense; }
    public int getInitiative() { return initiative; }

    public void setHp(int hp) { this.hp = hp; }
    public void setInitiative(int init) { this.initiative = init; }

    // --- Combat ---
    @Override
    public void takeDamage(int amount) {
        int mitigated = Math.max(0, amount - defense);
        hp = Math.max(0, hp - mitigated);
    }

    public void attack(Character target) {
        target.takeDamage(this.attack);
    }

    public void attack(Character target, int bonus) {
        target.takeDamage(this.attack + bonus);
    }

    public void heal(int amount) {
        this.hp = Math.min(maxHp, this.hp + Math.max(0, amount));
    }

    public void takeTurn(Character target) {
        this.attack(target); // default: basic attack
    }

    // --- Inventory ---
    public Inventory getInventory() { return inventory; }
    public void setInventory(Inventory inv) { this.inventory = inv; }

    // --- Status effects ---
    public void applyEffect(StatusEffect e) { effects.add(e); }
    public void tickEffects() { for (StatusEffect e : effects) e.tick(this); }

    // --- Skills ---
    public Map<String, Skill> getSkills() { return skills; }

    public boolean hasSkill(String skillName) {
        Skill skill = skills.get(skillName);
        return skill != null && skill.isUnlocked();
    }

    public void addSkill(String name) {
        skills.put(name, new Skill(name, "", 0));
        unlockSkill(name);
    }

    public void addSkill(String name, String description, int requiredBonusPoints) {
        skills.put(name, new Skill(name, description, requiredBonusPoints));
    }

    public boolean unlockSkill(String name) {
        Skill skill = skills.get(name);
        if (skill != null && !skill.isUnlocked()) {
            skill.setUnlocked(true);
            return true;
        }
        return false;
    }

    public boolean useSkill(String name, Character target) {
        Skill skill = skills.get(name);
        if (skill != null && skill.isUnlocked()) {
            switch (skill.getName().toLowerCase()) {
                case "dark bolt" -> {
                    target.takeDamage(20);
                    return true;
                }
                case "war cry" -> {
                    this.attack += 5;
                    return true;
                }
                // Add more skills here
            }
        }
        return false;
    }

    // --- Loot & Rewards ---
    public int getGoldReward() { return goldReward; }
    public void setGoldReward(int gold) { this.goldReward = gold; }

    public List<Item> getLoot() { return lootTable; }
    public void setLoot(List<Item> loot) { this.lootTable = loot; }
}