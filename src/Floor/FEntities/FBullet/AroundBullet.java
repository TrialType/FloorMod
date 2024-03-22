package Floor.FEntities.FBullet;

import Floor.FEntities.FBulletType.AroundBulletType;
import arc.Events;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.Vars;
import mindustry.core.World;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.game.EventType;
import mindustry.gen.*;

import java.util.HashMap;
import java.util.Map;

public class AroundBullet extends Bullet {
    private static final Seq<AroundBullet> freeBulletPool = new Seq<>();
    private final static Map<AroundBullet, Unit> units = new HashMap<>();
    private final static Map<Unit, Boolean> lastStatus = new HashMap<>();
    public Unit target;
    public AroundBulletType abt;
    public Effect e;

    protected AroundBullet() {
        super();
        target = null;
        abt = null;
        units.remove(this);
    }

    public static AroundBullet create() {
        if (freeBulletPool.size >= 1) {
            AroundBullet ab = freeBulletPool.get(0);
            freeBulletPool.remove(0);
            return ab;
        } else {
            return new AroundBullet();
        }
    }

    @Override
    public int classId() {
        return 97;
    }

    @Override
    public void update() {
        if (abt == null && type instanceof AroundBulletType) {
            abt = (AroundBulletType) type;
            e = abt.applyEffect;
        }

        if (abt != null) {
            float time = abt.statusTime;

            updateMaps();

            if (target == null) {
                updateTarget();
            }

            if (target != null) {
                if (!target.hasEffect(abt.statusEffect) && within(target, abt.circleRange + 5) && !lastStatus.get(target)) {

                    Units.nearbyEnemies(team, target.x, target.y, abt.circleRange, u -> {
                        e.at(x, y, 0, Color.valueOf("06172699"), u);

                        boolean dead = u.dead;
                        u.damage(abt.damage);
                        if (!dead && u.dead) {
                            Events.fire(new EventType.UnitBulletDestroyEvent(u, this));
                            return;
                        }

                        u.apply(abt.statusEffect, time);

                        if (lastStatus.get(u) != null) {
                            lastStatus.put(u, true);
                        }
                    });

                    if (!target.dead) {
                        lastStatus.put(target, true);
                    } else {
                        lastStatus.remove(target);
                    }

                    remove();
                }
            }
        }


        if (!Vars.net.client() || this.isLocal()) {
            float px = this.x;
            float py = this.y;
            this.move(this.vel.x * Time.delta, this.vel.y * Time.delta);
            if (Mathf.equal(px, this.x)) {
                this.vel.x = 0.0F;
            }

            if (Mathf.equal(py, this.y)) {
                this.vel.y = 0.0F;
            }

            this.vel.scl(Math.max(1.0F - this.drag * Time.delta, 0.0F));
        }

        if (this.mover != null) {
            this.mover.move(this);
        }

        this.type.update(this);
        if (this.type.collidesTiles && this.type.collides && this.type.collidesGround) {
            this.tileRaycast(World.toTile(this.lastX), World.toTile(this.lastY), this.tileX(), this.tileY());
        }

        if (this.type.removeAfterPierce && this.type.pierceCap != -1 && this.collided.size >= this.type.pierceCap) {
            this.hit = true;
            this.remove();
        }

        if (this.keepAlive) {
            this.time -= Time.delta;
            this.keepAlive = false;
        }

        this.time = Math.min(this.time + Time.delta, this.lifetime);
        if (this.time >= this.lifetime) {
            type.hit(this, x, y);
            this.remove();
        }
    }

    public void updateMaps() {
        if (target == null || target.dead || target.health <= 0) {
            lastStatus.remove(target);
            units.remove(this);
            target = null;
        } else {
            if (target.hasEffect(abt.statusEffect)) {
                lastStatus.put(target, false);
            }
        }
    }

    public void updateTarget() {
        target = Units.closestEnemy(team, x, y, abt.targetRange, u -> u.hasWeapons() && !find(u));
        if (target == null) {
            target = Units.closestEnemy(team, x, y, abt.targetRange, Unitc::hasWeapons);
        }
        if (target != null) {
            units.put(this, target);
            lastStatus.put(target, lastStatus.computeIfAbsent(target, t -> false));
        }
    }

    public boolean find(Unit u) {
        for (AroundBullet ab : units.keySet()) {
            if (units.get(ab) == u) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void remove() {
        if (this.added) {
            Groups.all.removeIndex(this, this.index__all);
            this.index__all = -1;
            Groups.bullet.removeIndex(this, this.index__bullet);
            this.index__bullet = -1;
            Groups.draw.removeIndex(this, this.index__draw);
            this.index__draw = -1;
            if (!Groups.isClearing) {
                if (!this.hit) {
                    this.type.despawned(this);
                }

                this.type.removed(this);
                this.collided.clear();
            }
            this.added = false;

            units.remove(this);
            boolean b = true;
            for (AroundBullet ab : units.keySet()) {
                if (ab != this && units.get(ab) == target) {
                    b = false;
                    break;
                }
            }
            if (b) {
                lastStatus.remove(target);
            }
            target = null;
            abt = null;

            freeBulletPool.add(this);
            Groups.queueFree(this);
        }
    }
}