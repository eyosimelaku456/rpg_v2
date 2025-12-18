package rpg.characters;

import rpg.inventory.Inventory;
import rpg.inventory.Item;
import rpg.inventory.Weapon;
import rpg.skills.Skill;
import rpg.utils.GameLogger;
import java.util.stream.Collectors;


import java.util.*;

public class Player extends Character {
    private int level = 1;
    private int xp = 0;
    private int bonusPoints = 0;
    private int gold = 100;
    private Weapon equippedWeapon;
    private Inventory inventory = new Inventory();

    public Player(String name) {
        super(name, 100, 10, 5);
        setInventory(new Inventory());

        addSkill("Dark Bolt", "Deals 20 damage to target", 0);
        unlockSkill("Dark Bolt");

        addSkill("War Cry", "Boosts your attack by 5", 0);
        unlockSkill("War Cry");
    }
    public List<String> getUnlockedSkillNames() {
    return skills.values().stream()
                 .filter(Skill::isUnlocked)
                 .map(Skill::getName)
                 .collect(Collectors.toList());
        }

   

    // Weapon equip logic
    public void equip(Weapon weapon) {
        this.equippedWeapon = weapon;
    }

    public Weapon getEquippedWeapon() {
        return equippedWeapon;
    }

    public boolean equipWeapon(int index) {
        Item item = inventory.get(index);
        if (item instanceof Weapon weapon) {
            if (bonusPoints >= weapon.getRequiredBonusPoints()) {
                equippedWeapon = weapon;
                GameLogger.log(name + " equipped " + weapon.getName() +
                        " (Bonus Damage: " + weapon.getBonusDamage() + ")");
                return true;
            } else {
                GameLogger.log(name + " lacks required bonus points to equip " + weapon.getName());
                return false;
            }
        }
        return false;
    }

    // XP and leveling
    public void addXp(int amount) {
        xp += Math.max(0, amount);
        if (xp >= level * 100) levelUp();
    }

    private void levelUp() {
        level++;
        xp = 0;
        maxHp += 10;
        attack += 2;
        defense += 1;
        hp = maxHp;
        bonusPoints += 5;
        GameLogger.log(name + " leveled up to " + level + " and earned 5 bonus points!");
    }

    public int getLevel() { return level; }
    public int getXp() { return xp; }

    // Bonus points
    public int getBonusPoints() { return bonusPoints; }

    public void addBonusPoints(int amount) {
        bonusPoints += Math.max(0, amount);
    }

    public boolean spendBonusPoints(int amount) {
        if (bonusPoints >= amount) {
            bonusPoints -= amount;
            return true;
        }
        return false;
    }

    // Stat upgrade
    public boolean upgradeStat(String stat) {
        if (bonusPoints < 1) return false;
        switch (stat.toLowerCase()) {
            case "attack" -> attack += 1;
            case "defense" -> defense += 1;
            case "hp" -> {
                maxHp += 5;
                hp = maxHp;
            }
            default -> { return false; }
        }
        bonusPoints--;
        return true;
    }

    // Gold
    public int getGold() { return gold; }

    public void addGold(int amount) {
        gold += amount;
    }

    public boolean spendGold(int amount) {
        if (gold >= amount) {
            gold -= amount;
            return true;
        }
        return false;
    }

    // Inventory
    public Inventory getInventory() { return inventory; }

    public void setInventory(Inventory newInventory) {
        if (newInventory != null) {
            this.inventory = newInventory;
            GameLogger.log(name + " switched to a new inventory.");
        }
    }

    // Skill unlock with bonus point check
    @Override
    public boolean unlockSkill(String name) {
        Skill skill = skills.get(name);
        if (skill != null && !skill.isUnlocked() && bonusPoints >= skill.getRequiredBonusPoints()) {
            skill.setUnlocked(true);
            bonusPoints -= skill.getRequiredBonusPoints();
            GameLogger.log(this.name + " unlocked skill: " + skill.getName());
            return true;
        }
        return false;
    }

    // Combat
    @Override
    public void attack(Character target) {
        int totalDamage = attack;
        if (equippedWeapon != null) {
            totalDamage += equippedWeapon.getBonusDamage();
        }
        target.takeDamage(totalDamage);
        GameLogger.log(name + " attacked " + target.getName() + " for " + totalDamage + " damage");
    }
}