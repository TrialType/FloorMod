package Floor.FTools;

import arc.Events;
import arc.func.Cons;
import arc.math.Mathf;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import arc.util.Nullable;
import mindustry.core.World;
import mindustry.entities.Damage;
import mindustry.entities.Units;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Bullet;
import mindustry.gen.Unit;
import mindustry.world.Tile;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class FDamage<T> extends Damage implements Cons<T> {
    private static final EventType.UnitDamageEvent bulletDamageEvent = new EventType.UnitDamageEvent();
    private static final Vec2 vec = new Vec2();
    private static final Rect rect = new Rect();

    public static void damage(Team team, float x, float y, float radius, float damage, boolean complete, boolean air, boolean ground, boolean scaled, @Nullable Bullet source) {
        Cons<Unit> cons = unit -> {
            if (unit.team == team || !unit.checkTarget(air, ground) || !unit.hittable() || !unit.within(x, y, radius + (scaled ? unit.hitSize / 2f : 0f))) {
                return;
            }
            boolean dead = unit.dead;
            float amount = calculateDamage(scaled ? Math.max(0, unit.dst(x, y) - unit.type.hitSize / 2) : unit.dst(x, y), radius, damage);
            unit.health(unit.health() - amount);
            unit.hitTime = 1.0F;
            if (unit.health() <= 0) unit.dead(true);
            if (source != null) {
                Events.fire(bulletDamageEvent.set(unit, source));
                unit.controller().hit(source);
                if (!dead && unit.dead) {
                    Events.fire(new EventType.UnitBulletDestroyEvent(unit, source));
                }
            }
            //TODO better velocity displacement
            float dst = vec.set(unit.x - x, unit.y - y).len();
            unit.vel.add(vec.setLength((1f - dst / radius) * 2f / unit.mass()));

            if (complete && damage >= 9999999f && unit.isPlayer()) {
                Events.fire(EventType.Trigger.exclusionDeath);
            }
        };
        rect.setSize(radius * 2).setCenter(x, y);
        if (team != null) {
            Units.nearbyEnemies(team, rect, cons);
        } else {
            Units.nearby(rect, cons);
        }
        if (ground) {
            if (!complete) {
                tileDamage(team, World.toTile(x), World.toTile(y), radius / tilesize, damage * (source == null ? 1f : source.type.buildingDamageMultiplier), source);
            } else {
                completeDamage(team, x, y, radius, damage);
            }
        }

    }

    private static void completeDamage(Team team, float x, float y, float radius, float damage) {

        int trad = (int) (radius / tilesize);
        for (int dx = -trad; dx <= trad; dx++) {
            for (int dy = -trad; dy <= trad; dy++) {
                Tile tile = world.tile(Math.round(x / tilesize) + dx, Math.round(y / tilesize) + dy);
                if (tile != null && tile.build != null && (team == null || team != tile.team()) && dx * dx + dy * dy <= trad * trad) {
                    tile.build.damage(team, damage);
                }
            }
        }
    }

    private static float calculateDamage(float dist, float radius, float damage) {
        float falloff = 0.4f;
        float scaled = Mathf.lerp(1f - dist / radius, 1f, falloff);
        return damage * scaled;
    }

    @Override
    public void get(T t) {

    }
}
