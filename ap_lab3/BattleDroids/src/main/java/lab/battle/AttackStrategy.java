package lab.battle;

import lab.droid.Droid;
import java.util.List;

public enum AttackStrategy {
    RANDOM,
    SEQUENTIAL,
    WEAKEST
}

class StrategyHelper {
    public static Droid chooseTarget(List<Droid> opponents, AttackStrategy strat) {
        if (opponents == null || opponents.isEmpty())
            return null;

        switch (strat) {
            case SEQUENTIAL:
                for (Droid d : opponents)
                    if (d.isAlive())
                        return d;

                return null;

            case WEAKEST:
                Droid min = null;
                for (Droid d : opponents)
                    if (d.isAlive()) 
                        if (min == null || d.getHealth() < min.getHealth())
                            min = d;

                return min;

            case RANDOM:
            default:
                java.util.List<Droid> alive = new java.util.ArrayList<>();

                for (Droid d : opponents)
                    if (d.isAlive())
                        alive.add(d);

                if (alive.isEmpty())
                    return null;

                java.util.Random rng = new java.util.Random();

                return alive.get(rng.nextInt(alive.size()));
        }
    }
}
