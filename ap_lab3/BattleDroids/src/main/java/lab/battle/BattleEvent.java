package lab.battle;

import java.time.Instant;

public class BattleEvent {
    public enum Type {
        INFO, ATTACK, HEAL, DEATH
    }

    public final Type type;
    public final String description;
    public final long timestamp;

    public BattleEvent(Type type, String desc) {
        this.type = type;
        this.description = desc;
        this.timestamp = Instant.now().toEpochMilli();
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", type, description);
    }
}
