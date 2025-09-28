package lab.droid;

public class AssaultDroid extends Droid {
    public AssaultDroid(String name) {
        // high health, moderate damage, good accuracy
        super(name, 120, 18, 0.75, 10);
    }

    @Override
    public int attack(Droid target) {
        // Assault has a chance to perform a power shot that costs energy
        if (energy >= 5 && rng.nextDouble() < 0.25) {
            energy -= 5;
            int power = (int) Math.round(getDamage() * 2.2);
            target.takeDamage(power);
            return power;
        }

        return super.attack(target);
    }

    @Override
    public void endOfRound() {
        // better energy regen
        energy += 6;
        if (energy > 50) energy = 50;
    }
}
