package Floor.FContent;

import Floor.FTools.FUnitUpGrade;
import arc.Events;
import mindustry.Vars;
import mindustry.entities.abilities.Ability;
import mindustry.entities.abilities.ShieldRegenFieldAbility;
import mindustry.entities.abilities.SpawnDeathAbility;
import mindustry.game.EventType;
import mindustry.gen.Building;
import mindustry.gen.Entityc;
import mindustry.gen.Healthc;
import mindustry.gen.Unit;

import java.util.Map;
import java.util.Random;


public class FEvents {
    private static final String[] list = {
            "healthy", "damage", "reload", "speed", "again", "shield"
    };
    private static final Random r = new Random();

    public static void load() {
        Events.on(GetPowerEvent.class, e -> {
            if (e.getter instanceof FUnitUpGrade) {
                if (e.full) {
                    getPower(e.getter, false, true, false);
                } else {
                    for (int i = 0; i < e.number; i++) {
                        getPower(e.getter, true, false, true);
                    }
                }
            }
        });

        Events.on(EventType.UnitCreateEvent.class, e -> getPower(e.unit, false, false, false));
        Events.on(EventType.UnitSpawnEvent.class, e -> {
            if (Vars.state.wave >= 6 && Vars.state.wave < 30) {
                getPower(e.unit, false, false, false);
            } else if (Vars.state.wave >= 30 && Vars.state.wave < 50) {
                for (int i = 0; i < 7; i++) {
                    getPower(e.unit, true, false, true);
                }
            } else if (Vars.state.wave >= 50 && Vars.state.wave < 70) {
                for (int i = 0; i < 13; i++) {
                    getPower(e.unit, true, false, true);
                }
            } else if (Vars.state.wave >= 70 && Vars.state.wave < 88) {
                for (int i = 0; i < 19; i++) {
                    getPower(e.unit, true, false, true);
                }
            } else if (Vars.state.wave >= 88 && Vars.state.wave < 100) {
                for (int i = 0; i < 25; i++) {
                    getPower(e.unit, true, false, true);
                }
            } else if (Vars.state.wave >= 100) {
                getPower(e.unit, true, true, true);
            }
        });

        Events.on(EventType.UnitBulletDestroyEvent.class, e -> {
            if (e.bullet.owner instanceof FUnitUpGrade uug && e.unit instanceof FUnitUpGrade) {
                uug.addExp(e.unit.maxHealth);
                getPower(e.bullet.owner, true, false, false);
            }
        });
        Events.on(FEvents.UnitDestroyOtherEvent.class, e -> {
            if (e.killer instanceof FUnitUpGrade uug && e.other instanceof FUnitUpGrade) {
                uug.addExp(e.other.maxHealth());
                getPower(e.killer, true, false, false);
            }
        });
    }

    private static void over() {
        String str;
        for (int i = 0; i < FEvents.list.length; i++) {
            int index = r.nextInt(FEvents.list.length);
            str = FEvents.list[index];
            FEvents.list[index] = FEvents.list[i];
            FEvents.list[i] = str;
        }
    }

    private static void getPower(Entityc e, boolean get, boolean full, boolean one) {
        if (e instanceof Unit u && e instanceof FUnitUpGrade uug) {
            if (uug.getLevel() >= 42) {
                return;
            }
            Map<String, Integer> m = uug.getMap();
            if (full) {
                uug.setLevel(42);
                Sit(u, m, null, true);
            } else if (get) {
                int number = uug.number();
                number = one ? 1 : number;
                for (int i = 0; i < number; i++) {
                    over();
                    for (String s : list) {
                        if (m.computeIfAbsent(s, k -> -1) == -1 || !(m.get(s) >= 9)) {
                            Sit(u, m, s, false);
                            return;
                        }
                    }
                }
            } else {
                int number = r.nextInt(10);
                number = number - 7;
                over();
                for (int i = 0; i < number; i++) {
                    Sit(u, m, list[i], false);
                }
            }
        } else if (e instanceof Building) {
            return;
        }
    }

    private static void Sit(Unit u, Map<String, Integer> map, String s, boolean full) {
        if (full) {
            map.put("healthy", 9);
            map.put("damage", 9);
            map.put("reload", 9);
            map.put("speed", 9);
            map.put("again", 9);
            map.put("shield", 9);
            u.apply(FStatusEffects.Health[9]);
            u.apply(FStatusEffects.Damage[9]);
            u.apply(FStatusEffects.Reload[9]);
            u.apply(FStatusEffects.Speed[9]);
            Ability[] ability = new Ability[u.abilities().length + 2];
            Ability[] CAbility = u.abilities();
            System.arraycopy(CAbility, 0, ability, 0, CAbility.length);
            ability[ability.length - 2] = new SpawnDeathAbility(u.type, 1, 0);
            ability[ability.length - 1] = new ShieldRegenFieldAbility(
                    u.maxHealth * 0.1F,
                    u.maxHealth,
                    180,
                    u.hitSize * 4
            );
            u.abilities = ability;
            u.apply(FStatusEffects.AAgain);
            return;
        }
        switch (s) {
            case "healthy": {
                int level = map.computeIfAbsent("healthy", k -> -1);
                if (level == -1) {
                    u.apply(FStatusEffects.Health[0]);
                } else {
                    u.unapply(FStatusEffects.Health[level]);
                    u.apply(FStatusEffects.Health[level + 1]);
                }
                map.put("healthy", map.get("healthy") + 1);
                break;
            }
            case "damage": {
                int level = map.computeIfAbsent("damage", k -> -1);
                if (level == -1) {
                    u.apply(FStatusEffects.ADamage);
                } else {
                    u.unapply(FStatusEffects.Damage[level]);
                    u.apply(FStatusEffects.Damage[level + 1]);
                }
                map.put("damage", map.get("damage") + 1);
                break;
            }
            case "reload": {
                int level = map.computeIfAbsent("reload", k -> -1);
                if (level == -1) {
                    u.apply(FStatusEffects.AReload);
                } else {
                    u.unapply(FStatusEffects.Reload[level]);
                    u.apply(FStatusEffects.Reload[level + 1]);
                }
                map.put("reload", map.get("reload") + 1);
                break;
            }
            case "speed": {
                int level = map.computeIfAbsent("speed", k -> -1);
                if (level == -1) {
                    u.apply(FStatusEffects.ASpeed);
                } else {
                    u.unapply(FStatusEffects.Speed[level]);
                    u.apply(FStatusEffects.Speed[level + 1]);
                }
                map.put("speed", map.get("speed") + 1);
                break;
            }
            case "again": {
                Ability[] ability = new Ability[u.abilities().length + 1];
                Ability[] CAbility = u.abilities();
                System.arraycopy(CAbility, 0, ability, 0, CAbility.length);
                ability[ability.length - 1] = new SpawnDeathAbility(u.type, 1, 0);
                u.abilities = ability;
                u.apply(FStatusEffects.AAgain);
                map.put("again", 9);
                break;
            }
            case "shield": {
                Ability[] ability = new Ability[u.abilities().length + 1];
                Ability[] CAbility = u.abilities();
                System.arraycopy(CAbility, 0, ability, 0, CAbility.length);
                ability[ability.length - 1] = new ShieldRegenFieldAbility(
                        u.maxHealth * 0.1F,
                        u.maxHealth,
                        180,
                        u.hitSize * 4
                );
                u.abilities = ability;
                map.put("shield", 9);
                break;
            }
        }
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