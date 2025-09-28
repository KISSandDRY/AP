package lab.ui;

import lab.droid.*;
import lab.battle.*;

import java.util.*;
import java.io.IOException;

public class Main {
    private static Scanner sc = new Scanner(System.in);
    private static List<Droid> droidsPoll = new ArrayList<>();

    public static void main(String[] args) {
        boolean run = true;

        while (run) {
            printMenu();
            String cmd = sc.nextLine().trim();

            switch (cmd) {
                case "1": createDroid(); break;
                case "2": listDroids(); break;
                case "3": runDuel(); break;
                case "4": runTeamBattle(); break;
                case "5": saveLastBattle(); break;
                case "6": replayBattle(); break;
                case "0": run = false; break;
                default: System.out.println("Unknown command");
            }
        }

        System.out.println("Exiting...");
    }

    private static void printMenu() {
        System.out.println("\n--- Droid Arena ---");
        System.out.println("1) Create droid");
        System.out.println("2) Show droids");
        System.out.println("3) Duel (1v1)");
        System.out.println("4) Team vs Team");
        System.out.println("5) Save last battle to file");
        System.out.println("6) Replay battle from file");
        System.out.println("0) Exit");
        System.out.print("Choose: ");
    }

    private static void createDroid() {
        System.out.println("Choose type: 1) Assault 2) Sniper 3) Medic");
        String t = sc.nextLine().trim();

        System.out.print("Enter name: ");
        String name = sc.nextLine().trim();
        
        Droid d = null;

        switch (t) {
            case "1": d = new AssaultDroid(name); break;
            case "2": d = new SniperDroid(name); break;
            case "3": d = new MedicDroid(name); break;
            default: System.out.println("Unknown type"); return;
        }

        droidsPoll.add(d);
        System.out.println("Created: " + d);
    }

    private static void listDroids() {
        if (droidsPoll.isEmpty()) {
            System.out.println("No droids created");
            return;
        }

        for (int i = 0; i < droidsPoll.size(); i++) 
            System.out.printf("%d) %s\n", i, droidsPoll.get(i));
    }

    private static List<BattleEvent> lastEvents = null;

    private static void runDuel() {
        if (droidsPoll.size() < 2) {
            System.out.println("Need at least 2 droids");
            return;
        }

        listDroids();
        System.out.print("Choose index of first: ");
        int i1 = Integer.parseInt(sc.nextLine().trim());

        System.out.print("Choose index of second: ");
        int i2 = Integer.parseInt(sc.nextLine().trim());

        if (i1 < 0 || i1 >= droidsPoll.size() || i2 < 0 || i2 >= droidsPoll.size() || i1 == i2) {
            System.out.println("Invalid indices");
            return;
        }

        // clone droids for new battle
        Droid a = cloneDroid(droidsPoll.get(i1));
        Droid b = cloneDroid(droidsPoll.get(i2));
        Duel duel = new Duel(a, b);
        lastEvents = duel.run();
        printEvents(lastEvents);
    }

    private static Droid cloneDroid(Droid src) {
        // shallow cloning by type
        if (src instanceof AssaultDroid) return new AssaultDroid(src.getName());
        if (src instanceof SniperDroid) return new SniperDroid(src.getName());
        if (src instanceof MedicDroid) return new MedicDroid(src.getName());
        // by default
        return new AssaultDroid(src.getName());
    }

    private static void runTeamBattle() {
        if (droidsPoll.size() < 2) {
            System.out.println("Need at least 2 droids");
            return;
        }

        listDroids();

        System.out.println("Create Team A: enter indices separated by space");
        List<Droid> teamA = readTeamFromInput();

        System.out.println("Create Team B: enter indices separated by space");
        List<Droid> teamB = readTeamFromInput();

        if (teamA.isEmpty() || teamB.isEmpty()) {
            System.out.println("Teams cannot be empty");
            return;
        }

        System.out.println("Choose strategy for A: 1) RANDOM 2) SEQUENTIAL 3) WEAKEST");
        AttackStrategy sA = chooseStrategy();

        System.out.println("Choose strategy for B: 1) RANDOM 2) SEQUENTIAL 3) WEAKEST");
        AttackStrategy sB = chooseStrategy();

        // clone lists
        List<Droid> aClones = new ArrayList<>();
        for (Droid d : teamA)
            aClones.add(cloneDroid(d));

        List<Droid> bClones = new ArrayList<>();
        for (Droid d : teamB)
            bClones.add(cloneDroid(d));

        TeamBattle tb = new TeamBattle(aClones, bClones, sA, sB);
        lastEvents = tb.run();
        printEvents(lastEvents);
    }

    private static List<Droid> readTeamFromInput() {
        String line = sc.nextLine().trim();
        List<Droid> team = new ArrayList<>();

        if (line.isEmpty()) return team;

        String[] parts = line.split("\\s+");

        for (String p : parts) {
            try {
                int idx = Integer.parseInt(p);
                Droid d = droidsPoll.get(idx);
                team.add(d);
            } catch (Exception e) {
                System.out.println("Skipping invalid: " + p);
            }
        }

        return team;
    }

    private static AttackStrategy chooseStrategy() {
        String in = sc.nextLine().trim();

        switch (in) {
            case "1": return AttackStrategy.RANDOM;
            case "2": return AttackStrategy.SEQUENTIAL;
            case "3": return AttackStrategy.WEAKEST;
            default:  return AttackStrategy.RANDOM;
        }
    }

    private static void printEvents(List<BattleEvent> events) {
        if (events == null) {
            System.out.println("No battle run yet");
            return;
        }

        for (BattleEvent e : events) System.out.println(e);
    }

    private static void saveLastBattle() {
        if (lastEvents == null) {
            System.out.println("No battle to save");
            return;
        }

        System.out.print("Enter filename: ");
        String f = sc.nextLine().trim();

        try {
            BattleRecorder.saveEvents(lastEvents, f);
            System.out.println("Saved to " + f);
        } catch (IOException ex) {
            System.out.println("Failed to save: " + ex.getMessage());
        }
    }

    private static void replayBattle() {
        System.out.print("Enter filename to replay: ");
        String f = sc.nextLine().trim();

        try {
            List<BattleEvent> events = BattleReplay.loadEvents(f);
            for (BattleEvent e : events) {
                System.out.println(e);
                try {
                    Thread.sleep(250);
                } catch (InterruptedException ignored) { }
            }
        } catch (IOException ex) {
            System.out.println("Failed to load: " + ex.getMessage());
        }
    }
}
