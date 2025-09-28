package lab.droid;

public class SniperDroid extends Droid {
    public SniperDroid(String name) {
        // low health, high damage, high accuracy but slow energy regen
        super(name, 70, 34, 0.85, 8);
    }

    @Override
    public int attack(Droid target) {
        // Sniper has chance for one-shot headshot if accuracy roll is very high
        double roll = rng.nextDouble();

        if (roll < accuracy) {
            if (roll < 0.12) { // lucky headshot
                int headshot = (int) Math.round(damage * 3.0);
                target.takeDamage(headshot);
                return headshot;
            }

            // normal hit
            return super.attack(target);
        }

        return 0;
    }

    @Override
    public void endOfRound() {
        energy += 2; // slow regen
    }
}
