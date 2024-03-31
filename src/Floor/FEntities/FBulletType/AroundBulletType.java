package Floor.FEntities.FBulletType;

import Floor.FEntities.FBullet.AroundBullet;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Nullable;
import arc.util.Time;
import mindustry.ai.types.MissileAI;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.entities.Mover;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.type.StatusEffect;
import mindustry.world.blocks.ControlBlock;

import static mindustry.Vars.net;
import static mindustry.Vars.world;

public class AroundBulletType extends BasicBulletType {
    public float targetRange = 100;
    public float circleRange = 40;
    public StatusEffect statusEffect = StatusEffects.wet;
    public float statusTime = 120;
    public Effect applyEffect = Fx.none;

    @Override
    public void init() {
        trailChance = 1;
        trailLength = 10;
    }

    @Override
    public void update(Bullet b) {
        super.update(b);
        if (b instanceof AroundBullet ab && ab.target != null && b.within(ab.target, circleRange + 5)) {
            Vec2 vec2 = new Vec2();
            float bx = b.x, by = b.y, tx = ab.target.x, ty = ab.target.y;
            float angle = Angles.angle(tx, ty, bx, by) + 5;
            vec2.set((float) (tx + circleRange * Math.cos(Math.toRadians(angle)) - bx), (float) (ty + circleRange * Math.sin(Math.toRadians(angle)) - by));
            vec2.setLength(speed);
            b.vel.set(vec2);
        } else if (b instanceof AroundBullet ab && ab.target != null) {
            Vec2 vec2 = new Vec2();
            vec2.set(ab.target.x - ab.x, ab.target.y - ab.y);
            vec2.setLength(speed);
            ab.move(vec2);
        }
    }

    public @Nullable AroundBullet create(Teamc owner, float x, float y, float angle) {
        return create(owner, owner.team(), x, y, angle);
    }

    public @Nullable AroundBullet create(Entityc owner, Team team, float x, float y, float angle) {
        return create(owner, team, x, y, angle, 1f);
    }

    public @Nullable AroundBullet create(Entityc owner, Team team, float x, float y, float angle, float velocityScl) {
        return create(owner, team, x, y, angle, -1, velocityScl, 1f, null);
    }

    public @Nullable AroundBullet create(Entityc owner, Team team, float x, float y, float angle, float velocityScl, float lifetimeScl) {
        return create(owner, team, x, y, angle, -1, velocityScl, lifetimeScl, null);
    }


    public @Nullable AroundBullet create(Entityc owner, Team team, float x, float y, float angle, float velocityScl, float lifetimeScl, Mover mover) {
        return create(owner, team, x, y, angle, -1, velocityScl, lifetimeScl, null, mover);
    }

    public @Nullable AroundBullet create(Bullet parent, float x, float y, float angle) {
        return create(parent.owner, parent.team, x, y, angle);
    }

    public @Nullable AroundBullet create(Bullet parent, float x, float y, float angle, float velocityScl, float lifeScale) {
        return create(parent.owner, parent.team, x, y, angle, velocityScl, lifeScale);
    }

    public @Nullable AroundBullet create(Bullet parent, float x, float y, float angle, float velocityScl) {
        return create(parent.owner(), parent.team, x, y, angle, velocityScl);
    }

    public @Nullable AroundBullet create(@Nullable Entityc owner, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data) {
        return create(owner, team, x, y, angle, damage, velocityScl, lifetimeScl, data, null);
    }

    public @Nullable AroundBullet create(@Nullable Entityc owner, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data, @Nullable Mover mover) {
        return create(owner, team, x, y, angle, damage, velocityScl, lifetimeScl, data, mover, -1f, -1f);
    }

    public @Nullable AroundBullet create(@Nullable Entityc owner, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data, @Nullable Mover mover, float aimX, float aimY) {
        return create(owner, owner, team, x, y, angle, damage, velocityScl, lifetimeScl, data, mover, aimX, aimY);
    }

    public AroundBullet create(Entityc owner, Entityc shooter, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data, @Nullable Mover mover, float aimX, float aimY) {
        if (!Mathf.chance(createChance)) return null;
        if (ignoreSpawnAngle) angle = 0;
        if (spawnUnit != null) {
            //don't spawn units clientside!
            if (!net.client()) {
                Unit spawned = spawnUnit.create(team);
                spawned.set(x, y);
                spawned.rotation = angle;
                //immediately spawn at top speed, since it was launched
                if (spawnUnit.missileAccelTime <= 0f) {
                    spawned.vel.trns(angle, spawnUnit.speed);
                }
                //assign unit owner
                if (spawned.controller() instanceof MissileAI ai) {
                    if (shooter instanceof Unit unit) {
                        ai.shooter = unit;
                    }

                    if (shooter instanceof ControlBlock control) {
                        ai.shooter = control.unit();
                    }

                }
                spawned.add();
            }
            //Since bullet init is never called, handle killing shooter here
            if (killShooter && owner instanceof Healthc h && !h.dead()) h.kill();

            //no bullet returned
            return null;
        }

        AroundBullet bullet = AroundBullet.create();
        bullet.type = this;
        bullet.owner = owner;
        bullet.team = team;
        bullet.time = 0f;
        bullet.originX = x;
        bullet.originY = y;
        if (!(aimX == -1f && aimY == -1f)) {
            bullet.aimTile = world.tileWorld(aimX, aimY);
        }
        bullet.aimX = aimX;
        bullet.aimY = aimY;

        bullet.initVel(angle, speed * velocityScl);
        if (backMove) {
            bullet.set(x - bullet.vel.x * Time.delta, y - bullet.vel.y * Time.delta);
        } else {
            bullet.set(x, y);
        }
        bullet.lifetime = lifetime * lifetimeScl;
        bullet.data = data;
        bullet.drag = drag;
        bullet.hitSize = hitSize;
        bullet.mover = mover;
        bullet.damage = (damage < 0 ? this.damage : damage) * bullet.damageMultiplier();
        //reset trail
        if (bullet.trail != null) {
            bullet.trail.clear();
        }
        bullet.add();

        if (keepVelocity && owner instanceof Velc v) bullet.vel.add(v.vel());
        return bullet;
    }
}
