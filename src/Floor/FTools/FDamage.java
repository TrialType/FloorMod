package Floor.FTools;

import arc.Events;
import arc.func.Cons;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Nullable;
import mindustry.content.Fx;
import mindustry.core.World;
import mindustry.entities.Damage;
import mindustry.entities.Units;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.world.Tile;

import static java.lang.Math.*;
import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class FDamage extends Damage {
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

    public static void SqrtDamage(@Nullable Bullet bullet, Team team, float damage, float x, float y, float rotation, float length, float width) {
        float angle = rotation + 90;
        if (angle < 0) angle = angle + 360;
        Seq<Float> xs = new Seq<>();
        Seq<Float> ys = new Seq<>();
        for (float l = abs(width); l >= -abs(width); l -= 0.3F) {
            float lenX = (float) (l * Math.cos(Math.toRadians(angle)));
            float lenY = (float) (l * Math.sin(Math.toRadians(angle)));
            xs.add(x + lenX);
            ys.add(y + lenY);
        }
        for (int i = 0; i < xs.size - 1; i++) {
            float ax1 = (xs.get(i));
            float ay1 = (ys.get(i));

            float ax2 = (xs.get(i + 1));
            float ay2 = (ys.get(i + 1));
            int ii = i;
            Units.nearbyEnemies(team, x, y, length * 1.44F, u -> {
                if (u.team == team) {
                    return;
                }
                float ux = u.x, uy = u.y;
                float angle1 = Angles.angleDist(Angles.angle(ax1, ay1, ux, uy), rotation);
                float angle2 = Angles.angleDist(Angles.angle(ax2, ay2, ux, uy), rotation);
                float angle3 = Angles.angleDist(Angles.angle(ux, uy, ax1, ay1), Angles.angle(ux, uy, ax2, ay2));
                if (angle1 != 0 && angle2 != 0 && angle3 == angle1 + angle2) {
                    float len2 = (float) sqrt((ux - ax1) * (ux - ax1) + (uy - ay1) * (uy - ay1));
                    if (len2 * cos(toRadians(angle1)) <= length) {
                        boolean dead = u.dead;
                        u.damage(min(ii + 1, xs.size - ii - 2) * 2 * damage / (xs.size - 1));
                        if (!dead && u.dead) {
                            Events.fire(new EventType.UnitBulletDestroyEvent(u, bullet));
                        }

                    }
                }
            });
            Units.nearbyBuildings(x, y, length * 1.44F, u -> {
                if (u.team == team) {
                    return;
                }
                float ux = u.x, uy = u.y;
                float angle1 = Angles.angleDist(Angles.angle(ax1, ay1, ux, uy), rotation);
                float angle2 = Angles.angleDist(Angles.angle(ax2, ay2, ux, uy), rotation);
                float angle3 = Angles.angleDist(Angles.angle(ux, uy, ax1, ay1), Angles.angle(ux, uy, ax2, ay2));
                if (angle1 != 0 && angle2 != 0 && angle3 == angle1 + angle2) {
                    float len2 = (float) sqrt((ux - ax1) * (ux - ax1) + (uy - ay1) * (uy - ay1));
                    if (len2 * cos(toRadians(angle1)) <= length) {
                        boolean dead = u.dead;
                        u.damage(min(ii + 1, xs.size - ii - 2) * 2 * damage / (xs.size - 1));
                        if (!dead && u.dead) {
                            Events.fire(new EventType.BuildingBulletDestroyEvent(u, bullet));
                        }

                    }
                }
            });
        }
//        for (float l = abs(width); l >= 0; l -= 0.3F) {
//            float lenX = (float) (l * Math.cos(Math.toRadians(angle)));
//            float lenY = (float) (l * Math.sin(Math.toRadians(angle)));
//            float ax1 = (x + lenX);
//            float ay1 = (y + lenY);
//
//            float ax2 = (x - lenX);
//            float ay2 = (y - lenY);
//            Units.nearbyEnemies(team, x, y, length * 1.44F, u -> {
//                if (u.team == team) {
//                    return;
//                }
//                float ux = u.x, uy = u.y;
//                float angle1 = Angles.angleDist(Angles.angle(ax1, ay1, ux, uy), rotation);
//                float angle2 = Angles.angleDist(Angles.angle(ax2, ay2, ux, uy), rotation);
//                float angle3 = Angles.angleDist(Angles.angle(ux, uy, ax1, ay1), Angles.angle(ux, uy, ax2, ay2));
//                if (angle1 != 0 && angle2 != 0 && angle3 == angle1 + angle2) {
//                    float len2 = (float) sqrt((ux - ax1) * (ux - ax1) + (uy - ay1) * (uy - ay1));
//                    if (len2 * cos(toRadians(angle1)) <= length) {
//                        boolean dead = u.dead;
//                        u.damage(damage * 4 / (width / 0.3F));
//                        if (!dead && u.dead) {
//                            Events.fire(new EventType.UnitBulletDestroyEvent(u, bullet));
//                        }
//
//                    }
//                }
//            });
//            Units.nearbyBuildings(x, y, length * 1.44F, u -> {
//                if (u.team == team) {
//                    return;
//                }
//                float ux = u.x, uy = u.y;
//                float angle1 = Angles.angleDist(Angles.angle(ax1, ay1, ux, uy), rotation);
//                float angle2 = Angles.angleDist(Angles.angle(ax2, ay2, ux, uy), rotation);
//                float angle3 = Angles.angleDist(Angles.angle(ux, uy, ax1, ay1), Angles.angle(ux, uy, ax2, ay2));
//                if (angle1 != 0 && angle2 != 0 && angle3 == angle1 + angle2) {
//                    float len2 = (float) sqrt((ux - ax1) * (ux - ax1) + (uy - ay1) * (uy - ay1));
//                    if (len2 * cos(toRadians(angle1)) <= length) {
//                        boolean dead = u.dead;
//                        u.damage(damage / (width / 0.3F));
//                        if (!dead && u.dead) {
//                            Events.fire(new EventType.BuildingBulletDestroyEvent(u, bullet));
//                        }
//
//                    }
//                }
//            });
//        }
    }
}
