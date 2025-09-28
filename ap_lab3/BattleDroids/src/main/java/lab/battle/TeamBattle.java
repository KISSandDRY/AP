package lab.battle;

import lab.droid.*;
import java.util.ArrayList;
import java.util.List;

public class TeamBattle {
    private List<Droid> teamA;
    private List<Droid> teamB;
    private AttackStrategy stratA;
    private AttackStrategy stratB;
    private List<BattleEvent> events = new ArrayList<>();

    public TeamBattle(List<Droid> teamA, List<Droid> teamB, AttackStrategy sA, AttackStrategy sB) {
        this.teamA = teamA;
        this.teamB = teamB;
        this.stratA = sA;
        this.stratB = sB;
    }

    private boolean anyAlive(List<Droid> team) {
        for (Droid d : team)
            if (d.isAlive())
                return true;

        return false;
    }

    public List<BattleEvent> run() {
        events.add(new BattleEvent(BattleEvent.Type.INFO, "Team battle start"));
        int round = 1;

        while (anyAlive(teamA) && anyAlive(teamB)) {
            events.add(new BattleEvent(BattleEvent.Type.INFO, "Round " + round));

            // Team A acts
            for (Droid actor : teamA)
                if (actor.isAlive()) {
                    Droid target = StrategyHelper.chooseTarget(teamB, stratA);
                    if (target == null)
                        continue;

                    if (actor instanceof MedicDroid) {
                        MedicDroid m = (MedicDroid) actor;
                        Droid ally = StrategyHelper.chooseTarget(teamA, AttackStrategy.WEAKEST);
                        if (ally != null && ally.getHealth() < ally.getMaxHealth() * 0.6 && m.getEnergy() >= 6) {
                            int healed = m.heal(ally);
                            events.add(
                                    new BattleEvent(BattleEvent.Type.HEAL, String.format("%s heals %s for %d (HP %d)",
                                            m.getName(), ally.getName(), healed, ally.getHealth())));
                            continue;
                        }
                    }

                    int dealt = actor.attack(target);
                    events.add(new BattleEvent(dealt > 0 ? BattleEvent.Type.ATTACK : BattleEvent.Type.INFO,
                            String.format("%s -> %s = %d (HP %d)", actor.getName(), target.getName(), dealt,
                                    target.getHealth())));

                    if (!target.isAlive())
                        events.add(new BattleEvent(BattleEvent.Type.DEATH, target.getName() + " destroyed"));
                }

            // Team B acts
            for (Droid actor : teamB)
                if (actor.isAlive()) {
                    Droid target = StrategyHelper.chooseTarget(teamA, stratB);
                    if (target == null)
                        continue;

                    if (actor instanceof MedicDroid) {
                        MedicDroid m = (MedicDroid) actor;
                        Droid ally = StrategyHelper.chooseTarget(teamB, AttackStrategy.WEAKEST);

                        if (ally != null && ally.getHealth() < ally.getMaxHealth() * 0.6 && m.getEnergy() >= 6) {
                            int healed = m.heal(ally);
                            events.add(
                                    new BattleEvent(BattleEvent.Type.HEAL, String.format("%s heals %s for %d (HP %d)",
                                            m.getName(), ally.getName(), healed, ally.getHealth())));
                            continue;
                        }
                    }

                    int dealt = actor.attack(target);
                    events.add(new BattleEvent(dealt > 0 ? BattleEvent.Type.ATTACK : BattleEvent.Type.INFO,
                            String.format("%s -> %s = %d (HP %d)", actor.getName(), target.getName(), dealt,
                                    target.getHealth())));

                    if (!target.isAlive())
                        events.add(new BattleEvent(BattleEvent.Type.DEATH, target.getName() + " destroyed"));
                }

            teamA.forEach(Droid::endOfRound);
            teamB.forEach(Droid::endOfRound);
            round++;
        }

        events.add(new BattleEvent(BattleEvent.Type.INFO, "Team battle finished"));

        boolean aAlive = anyAlive(teamA);
        boolean bAlive = anyAlive(teamB);
        if (aAlive && !bAlive) {
            events.add(new BattleEvent(BattleEvent.Type.INFO, "Team A wins!"));
        } else if (bAlive && !aAlive) {
            events.add(new BattleEvent(BattleEvent.Type.INFO, "Team B wins!"));
        } else {
            events.add(new BattleEvent(BattleEvent.Type.INFO, "It's a draw!"));
        }

        return events;
    }
}
