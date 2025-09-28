package lab.battle;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class BattleRecorder {
    public static final String REPLAY_FILE_EXTENSION = ".rpl";
    
    public static void saveEvents(List<BattleEvent> events, String filename) throws IOException {
        filename += REPLAY_FILE_EXTENSION;

        try (BufferedWriter w = new BufferedWriter(new FileWriter(filename))) {
            for (BattleEvent e : events) {
                // format: timestamp|TYPE|description
                String line = String.format("%d|%s|%s\n", e.timestamp, e.type.name(), e.description.replace("\n", " "));
                w.write(line);
            }
        }
    }
}
