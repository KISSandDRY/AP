package lab.droid;

import java.util.Random;

public abstract class Droid {
    protected String name;
    protected int maxHealth;
    protected int health;
    protected int damage;
    protected double accuracy; // 0..1 chance to hit
    protected int energy;
    protected Random rng = new Random();

    public Droid(String name, int maxHealth, int damage, double accuracy, int energy) {
        this.name = name;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.damage = damage;
        this.accuracy = accuracy;
        this.energy = energy;
    }

    public String getName() { return name; }
    public boolean isAlive() { return health > 0; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public int getDamage() { return damage; }
    public double getAccuracy() { return accuracy; }
    public int getEnergy() { return energy; }
    public void addEnergy(int amount) { energy += amount; }
    public void consumeEnergy(int amount) { energy = Math.max(0, energy - amount); }

    public int attack(Droid target) {
        if (!isAlive()) return 0;
        if (rng.nextDouble() >= accuracy) return 0;
        int dealt = damage;
        if (rng.nextDouble() < 0.08) dealt = (int) Math.round(dealt * 1.5);
        target.takeDamage(dealt);
        return dealt;
    }

    public void takeDamage(int d) {
        health -= d;
        if (health < 0) health = 0;
    }

    public void receiveHealing(int amount) {
        health += amount;
        if (health > maxHealth) health = maxHealth;
    }

    public void endOfRound() { addEnergy(3); }

    public String statusString() {
        return String.format("%s HP:%d/%d E:%d", name, health, maxHealth, energy);
    }

    @Override
    public String toString() {
        return String.format("%s (HP:%d, DMG:%d, ACC:%.2f, E:%d)", name, health, damage, accuracy, energy);
    }
}
