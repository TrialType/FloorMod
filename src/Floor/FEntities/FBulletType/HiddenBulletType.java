package Floor.FEntities.FBulletType;

import Floor.FEntities.FBullet.LargeNumberBullet;
import Floor.FEntities.FUnit.F.HiddenUnit;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Nullable;
import arc.util.Time;
import mindustry.ai.types.MissileAI;
import mindustry.entities.Mover;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.world.blocks.ControlBlock;

import static mindustry.Vars.net;
import static mindustry.Vars.world;

public class HiddenBulletType extends BasicBulletType {
    public void createFrags(Bullet b, float x, float y) {
        if (fragBullet != null && (fragOnAbsorb || !b.absorbed)) {
            for (int i = 0; i < fragBullets; i++) {
                float len = Mathf.random(1f, 7f);
                float a = b.rotation() + Mathf.range(fragRandomSpread / 2) + fragAngle + ((i - (float) fragBullets / 2) * fragSpread);
                if (fragBullet instanceof HiddenBulletType wmb) {
                    LargeNumberBullet bullet = wmb.create(b.owner, null, b.team, x + Angles.trnsx(a, len), y + Angles.trnsy(a, len), a, wmb.damage, Mathf.random(fragVelocityMin, fragVelocityMax), Mathf.random(fragLifeMin, fragLifeMax), null, null, -1, -1);
                    if (bullet.owner instanceof HiddenUnit wu) {
                        if (i < fragBullets / 2) {
                            wu.bullets.put(bullet, speed);
                        } else {
                            bullet.lifetime = 600;
                        }
                    }
                }
            }
        }
    }

    public LargeNumberBullet create(Entityc owner, Entityc shooter, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data, @Nullable Mover mover, float aimX, float aimY) {
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

        LargeNumberBullet bullet = LargeNumberBullet.create();
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
