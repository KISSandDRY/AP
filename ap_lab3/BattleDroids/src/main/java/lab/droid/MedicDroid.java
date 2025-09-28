package lab.droid;

public class MedicDroid extends Droid {
    private int healPower;

    public MedicDroid(String name) {
        super(name, 90, 8, 0.6, 12);
        this.healPower = 18;
    }

    public int heal(Droid ally) {
        if (!isAlive()) return 0;

        if (getEnergy() >= 6) {
            consumeEnergy(6);
            int amount = healPower + rng.nextInt(6) - 2;
            ally.receiveHealing(amount);

            return amount;
        }

        return 0;
    }

    @Override
    public int attack(Droid target) {
        if (getEnergy() >= 6 && getHealth() < (getMaxHealth() * 0.6)) {
            heal(this); // heal self if low

            return 0;
        }

        return super.attack(target);
    }

    @Override
    public void endOfRound() { addEnergy(5); }
}
