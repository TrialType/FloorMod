package Floor.FContent;

import Floor.FTools.FUnitUpGrade;
import Floor.FTools.UnitUpGrade;
import Floor.FTools.UpGradeTime;
import arc.Events;
import mindustry.Vars;
import mindustry.ai.types.MissileAI;
import mindustry.game.EventType;
import mindustry.gen.Healthc;
import mindustry.gen.Unit;

import java.util.Random;

import static java.lang.Math.max;
import static java.lang.Math.min;


public class FEvents {
    private static final Random r = new Random();

    public static void load() {
        Events.on(GetPowerEvent.class, e -> {
            if (e.getter instanceof FUnitUpGrade uug) {
                if (e.full) {
                    UnitUpGrade.getPower(uug, 0, false, true);
                } else {
                    int num = min(60 - uug.baseLevel(), e.number);
                    uug.setLevel(uug.getLevel() + num);
                    UnitUpGrade.getPower(uug, num, true, false);
                }
            }
        });

        Events.on(EventType.UnitCreateEvent.class, e -> {
            if (e.unit instanceof FUnitUpGrade uug) {
                int n = r.nextInt(6);
                uug.setLevel(uug.getLevel() + n);
                UnitUpGrade.getPower(uug, n, true, false);
            }
        });
        Events.on(EventType.UnitSpawnEvent.class, e -> {
            if (e.unit instanceof FUnitUpGrade uug) {
                if (Vars.state.wave >= 8 && Vars.state.wave < 24) {
                    int n = r.nextInt(6);
                    uug.setLevel(n);
                    UnitUpGrade.getPower(uug, n, true, false);
                } else if (Vars.state.wave >= 24 && Vars.state.wave < 45) {
                    uug.setLevel(7);
                    UnitUpGrade.getPower(uug, 7, true, false);
                } else if (Vars.state.wave >= 45 && Vars.state.wave < 68) {
                    uug.setLevel(13);
                    UnitUpGrade.getPower(uug, 13, true, false);
                } else if (Vars.state.wave >= 68 && Vars.state.wave < 90) {
                    uug.setLevel(19);
                    UnitUpGrade.getPower(uug, 19, true, false);
                } else if (Vars.state.wave >= 90 && Vars.state.wave < 115) {
                    uug.setLevel(25);
                    UnitUpGrade.getPower(uug, 25, true, false);
                } else if (Vars.state.wave >= 115 && Vars.state.wave <= 150) {
                    UnitUpGrade.getPower(uug, 0, false, true);
                } else if (Vars.state.wave > 150) {
                    UnitUpGrade.getPower(uug, 0, false, true);
                    uug.setLevel(60 + 5 * (Vars.state.wave - 150));
                }
            }
        });

        Events.on(EventType.UnitBulletDestroyEvent.class, e -> {
            if (e.bullet.owner instanceof FUnitUpGrade uug && (e.unit instanceof FUnitUpGrade || e.unit.maxHealth() >= 1000)) {
                uug.addExp(e.unit.maxHealth * Math.max(1, uug.getLevel() / 15) * max(Vars.state.wave / 15, 1));
                int n = uug.number();
                int min = min(60 - uug.baseLevel(), n);
                UnitUpGrade.getPower(uug, min, true, false);
            } else if (e.bullet.owner instanceof Unit u && u.controller() instanceof MissileAI ai && ai.shooter instanceof FUnitUpGrade uug) {
                uug.addExp(e.unit.maxHealth() * Math.max(1, uug.getLevel() / 15) * max(Vars.state.wave / 15, 1));
                int n = uug.number();
                int min = min(60 - uug.baseLevel(), n);
                UnitUpGrade.getPower(uug, min, true, false);
            }
            if (e.bullet.owner instanceof Unit u) {
                if (u.controller() instanceof MissileAI ai && ai.shooter instanceof UpGradeTime ugt) {
                    ugt.add(1);
                }
            }
        });
        Events.on(FEvents.UnitDestroyOtherEvent.class, e -> {
            if (e.other instanceof FUnitUpGrade || e.other.maxHealth() >= 1000) {
                if (e.killer instanceof FUnitUpGrade uug) {
                    uug.addExp(e.other.maxHealth() * Math.max(1, uug.getLevel() / 15) * max(Vars.state.wave / 15, 1));
                    int n = uug.number();
                    int min = min(60 - uug.baseLevel(), n);
                    UnitUpGrade.getPower(uug, min, true, false);
                } else if (e.killer.controller() instanceof MissileAI ai && ai.shooter instanceof FUnitUpGrade uug) {
                    uug.addExp(e.other.maxHealth() * Math.max(1, uug.getLevel() / 15) * max(Vars.state.wave / 15, 1));
                    int n = uug.number();
                    int min = min(60 - uug.baseLevel(), n);
                    UnitUpGrade.getPower(uug, min, true, false);
                }
                if (e.killer.controller() instanceof MissileAI ai && ai.shooter instanceof UpGradeTime ugt) {
                    ugt.add(1);
                }
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