package Floor.FEntities.FBulletType;

import Floor.FEntities.FBullet.removeSpwanBullet;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.ai.types.MissileAI;
import mindustry.entities.Damage;
import mindustry.entities.Mover;
import mindustry.entities.bullet.ContinuousLaserBulletType;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.world.blocks.ControlBlock;

import static mindustry.Vars.net;
import static mindustry.Vars.world;

public class FlyContinuousLaserBulletType extends ContinuousLaserBulletType {
    @Override
    public removeSpwanBullet create(@Nullable Entityc owner, @Nullable Entityc shooter, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data, @Nullable Mover mover, float aimX, float aimY) {
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

        //這一步是關鍵
        removeSpwanBullet bullet = new removeSpwanBullet();
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

    public removeSpwanBullet create(@Nullable Entityc owner, Team team, float x, float y, float angle, float velocityScl, float lifetimeScl) {
        return create(owner, null, team, x, y, angle, damage, velocityScl, lifetimeScl, null, null, -1, -1);
    }

    @Override
    public void draw(Bullet b) {
        removeSpwanBullet rsb = (removeSpwanBullet) b;
        if (rsb != null) {
            float realLength;
            if (!rsb.couldAgain) {
                realLength = Damage.findLength(b, length, false, pierceCap);
            } else {
                realLength = Damage.findLength(b, length, laserAbsorb, pierceCap);
            }

            float rot = b.rotation();

            for (int i = 0; i < colors.length; i++) {
                Draw.color(Tmp.c1.set(colors[i]).mul(1f + Mathf.absin(Time.time, 1f, 0.1f)));

                float colorFin = i / (float) (colors.length - 1);
                float baseStroke = Mathf.lerp(strokeFrom, strokeTo, colorFin);
                float stroke = (width + Mathf.absin(Time.time, oscScl, oscMag)) * baseStroke;
                float ellipseLenScl = Mathf.lerp(1 - i / (float) (colors.length), 1f, pointyScaling);

                Lines.stroke(stroke);
                Lines.lineAngle(b.x, b.y, rot, realLength - frontLength, false);

                //back ellipse
                Drawf.flameFront(b.x, b.y, divisions, rot + 180f, backLength, stroke / 2f);

                //front ellipse
                Tmp.v1.trnsExact(rot, realLength - frontLength);
                Drawf.flameFront(b.x + Tmp.v1.x, b.y + Tmp.v1.y, divisions, rot, frontLength * ellipseLenScl, stroke / 2f);
            }

            Tmp.v1.trns(b.rotation(), realLength * 1.1f);

            Drawf.light(b.x, b.y, b.x + Tmp.v1.x, b.y + Tmp.v1.y, lightStroke, lightColor, 0.7f);
            Draw.reset();
        }
    }
}
