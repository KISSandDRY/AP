package lab.battle;

import lab.droid.Droid;
import java.util.ArrayList;
import java.util.List;

public class Duel {
    private Droid a;
    private Droid b;
    private List<BattleEvent> events = new ArrayList<>();

    public Duel(Droid a, Droid b) {
        this.a = a;
        this.b = b;
    }

    public List<BattleEvent> run() {
        events.add(new BattleEvent(BattleEvent.Type.INFO,
                String.format("Duel start: %s vs %s", a.getName(), b.getName())));
        int round = 1;

        while (a.isAlive() && b.isAlive()) {

            events.add(new BattleEvent(BattleEvent.Type.INFO, String.format("Round %d", round)));
            // a attacks b
            int dealtA = a.attack(b);
            events.add(new BattleEvent(dealtA > 0 ? BattleEvent.Type.ATTACK : BattleEvent.Type.INFO,
                    String.format("%s attacks %s and deals %d damage. %s now HP=%d", a.getName(), b.getName(), dealtA,
                            b.getName(), b.getHealth())));

            if (!b.isAlive()) {
                events.add(new BattleEvent(BattleEvent.Type.DEATH, b.getName() + " destroyed"));
                break;
            }

            // b attacks a
            int dealtB = b.attack(a);
            events.add(new BattleEvent(dealtB > 0 ? BattleEvent.Type.ATTACK : BattleEvent.Type.INFO,
                    String.format("%s attacks %s and deals %d damage. %s now HP=%d", b.getName(), a.getName(), dealtB,
                            a.getName(), a.getHealth())));

            if (!a.isAlive()) {
                events.add(new BattleEvent(BattleEvent.Type.DEATH, a.getName() + " destroyed"));
                break;
            }

            a.endOfRound(); b.endOfRound();
            round++;
        }

        events.add(new BattleEvent(BattleEvent.Type.INFO, "Duel finished"));

        if (a.isAlive() && !b.isAlive()) {
            events.add(new BattleEvent(BattleEvent.Type.INFO, a.getName() + " wins!"));
        } else if (b.isAlive() && !a.isAlive()) {
            events.add(new BattleEvent(BattleEvent.Type.INFO, b.getName() + " wins!"));
        } else {
            events.add(new BattleEvent(BattleEvent.Type.INFO, "It's a draw!"));
        }

        return events;
    }
}
