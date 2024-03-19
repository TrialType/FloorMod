package Floor.FContent;

import Floor.FTools.FUnitUpGrade;
import Floor.FTools.UnitUpGrade;
import arc.Events;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.Healthc;
import mindustry.gen.Unit;

import java.util.Random;


public class FEvents {
    private static final Random r = new Random();

    public static void load() {
        Events.on(GetPowerEvent.class, e -> {
            if (e.getter instanceof FUnitUpGrade uug) {
                if (e.full) {
                    UnitUpGrade.getPower(uug, 0, false, true);
                } else {
                    uug.setLevel(uug.getLevel() + e.number);
                    UnitUpGrade.getPower(uug, e.number, true, false);
                }
            }
        });

        Events.on(EventType.UnitCreateEvent.class, e -> {
            if (e.unit instanceof FUnitUpGrade uug) {
                int n = r.nextInt(6);
                uug.setLevel(uug.getLevel() + n);
                UnitUpGrade.getPower(uug, n, false, false);
            }
        });
        Events.on(EventType.UnitSpawnEvent.class, e -> {
            if (e.unit instanceof FUnitUpGrade uug) {
                if (Vars.state.wave >= 8 && Vars.state.wave < 24) {
                    int n = r.nextInt(6);
                    uug.setLevel(uug.getLevel() + n);
                    UnitUpGrade.getPower(uug, n, false, false);
                } else if (Vars.state.wave >= 24 && Vars.state.wave < 45) {
                    UnitUpGrade.getPower(uug, 7, false, false);
                } else if (Vars.state.wave >= 45 && Vars.state.wave < 68) {
                    UnitUpGrade.getPower(uug, 13, false, false);
                } else if (Vars.state.wave >= 68 && Vars.state.wave < 90) {
                    UnitUpGrade.getPower(uug, 19, false, false);
                } else if (Vars.state.wave >= 90 && Vars.state.wave < 115) {
                    UnitUpGrade.getPower(uug, 25, false, false);
                } else if (Vars.state.wave >= 115) {
                    UnitUpGrade.getPower(uug, 0, false, true);
                }
            }
        });

        Events.on(EventType.UnitBulletDestroyEvent.class, e -> {
            if (e.bullet.owner instanceof FUnitUpGrade uug && e.unit instanceof FUnitUpGrade) {
                uug.addExp(e.unit.maxHealth * Math.max(1, uug.getLevel() / 5) * Vars.state.wave / 5);
                UnitUpGrade.getPower(uug, uug.number(), true, false);
            }
        });
        Events.on(FEvents.UnitDestroyOtherEvent.class, e -> {
            if (e.killer instanceof FUnitUpGrade uug && e.other instanceof FUnitUpGrade) {
                uug.addExp(e.other.maxHealth() * Math.max(1, uug.getLevel() / 5) * Vars.state.wave / 5);
                UnitUpGrade.getPower(uug, uug.number(), true, false);
            }
        });
    }

    public static class UnitDestroyOtherEvent {
        public Unit killer;
        public Healthc other;

        public UnitDestroyOtherEvent(Unit killer, Healthc other) {
            this.killer = killer;
            this.other = other;
        }
    }

    public static class GetPowerEvent {
        Unit getter;
        int number;
        boolean full;

        public GetPowerEvent(Unit u, int n, boolean full) {
            getter = u;
            number = n;
            this.full = full;
        }
    }
}