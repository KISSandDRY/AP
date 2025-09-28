package lab.battle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BattleReplay {
    public static List<BattleEvent> loadEvents(String filename) throws IOException {
        List<BattleEvent> events = new ArrayList<>();

        filename += BattleRecorder.REPLAY_FILE_EXTENSION;

        try (BufferedReader r = new BufferedReader(new FileReader(filename))) {
            String line;

            while ((line = r.readLine()) != null) {
                String[] parts = line.split("\\|", 3);
                if (parts.length < 3)
                    continue;

                // parts[1] = TYPE, parts[2] = description
                BattleEvent.Type t = BattleEvent.Type.valueOf(parts[1]);
                String desc = parts[2];

                events.add(new BattleEvent(t, desc));
            }
        }

        return events;
    }
}
