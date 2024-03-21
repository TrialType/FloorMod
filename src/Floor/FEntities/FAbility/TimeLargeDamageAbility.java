package Floor.FEntities.FAbility;

import Floor.FContent.FEvents;
import arc.Events;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.entities.Units;
import mindustry.entities.abilities.Ability;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Unit;

import java.util.HashMap;
import java.util.Map;

public class TimeLargeDamageAbility extends Ability {
    private final Map<Unit, Float> unitTimes = new HashMap<>();
    private final Map<Building, Float> buildingTimes = new HashMap<>();

    public float baseDamage = 5;

    public TimeLargeDamageAbility(float damage) {
        baseDamage = damage;
    }

    public TimeLargeDamageAbility() {
    }

    public void update(Unit unit) {
        updateTimes(unit);

        Team team = unit.team;
        float x = unit.x, y = unit.y;
        Units.nearbyEnemies(team, x, y, 42, u -> {
            float timer = unitTimes.computeIfAbsent(u, uu -> 0F);
            float damage = (float) Math.pow(baseDamage, timer / 120) * baseDamage / 4;
            boolean dead = u.dead;
            u.damage(damage);
            if (dead && !u.dead) {
                Events.fire(new FEvents.UnitDestroyOtherEvent(unit, u));
            }
        });
        Units.nearbyBuildings(x, y, 42, b -> {
            if (b.team != team) {
                float timer = buildingTimes.computeIfAbsent(b, uu -> 0F);
                float damage = (float) Math.pow(baseDamage, timer / 120) * baseDamage / 4;
                boolean dead = b.dead;
                b.damage(damage);
                if (dead && !b.dead) {
                    Events.fire(new FEvents.UnitDestroyOtherEvent(unit, b));
                }
            }
        });
    }

    private void updateTimes(Unit unit) {
        Seq<Unit> units = new Seq<>();
        Seq<Building> buildings = new Seq<>();
        for (Unit u : unitTimes.keySet()) {
            if (u.dead || u.health() <= 0 || !u.within(unit, 42)) {
                units.add(u);
            }
        }
        for (Building b : buildingTimes.keySet()) {
            if (b.dead || b.health() <= 0 || !b.within(unit, 42)) {
                buildings.add(b);
            }
        }
        for (Unit u : units) {
            unitTimes.remove(u);
        }
        for (Building b : buildings) {
            buildingTimes.remove(b);
        }
        buildingTimes.replaceAll((b, f) -> f + Time.delta);
        unitTimes.replaceAll((u, f) -> f + Time.delta);
    }
}
