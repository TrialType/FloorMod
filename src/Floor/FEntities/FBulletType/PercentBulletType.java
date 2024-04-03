package Floor.FEntities.FBulletType;

import Floor.FTools.FDamage;
import arc.Events;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Tmp;
import mindustry.content.StatusEffects;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.Fires;
import mindustry.entities.Lightning;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.game.EventType;
import mindustry.gen.*;
import mindustry.world.blocks.ConstructBlock;

import static mindustry.Vars.indexer;

public class PercentBulletType extends BasicBulletType {
    public boolean WS = false, WL = false;
    public boolean firstPercent = false;
    public float percent = 10, lightningPercent = 0F;
    public long changeHel = -1L;

    @Override
    public void hitEntity(Bullet b, Hitboxc entity, float health) {
        boolean wasDead = entity instanceof Unit u && u.dead;
        if (entity instanceof Healthc h) {
            if (WS) {
                splashDamage = ((h.maxHealth()) * percent / 100);
            }
            if (WL) {
                lightningDamage = ((h.maxHealth()) * lightningPercent * 1.503F / 100);
            }
            if ((firstPercent && h.health() > changeHel) || (!firstPercent && h.health() <= changeHel)) {
                b.damage = h.health() - (h.maxHealth() * percent / 100);
                h.health(h.health() - (h.maxHealth() * percent / 100));
                h.damage(0);
                if (h.health() <= 0) {
                    h.dead(true);
                }
            } else {
                b.damage = damage * damageMultiplier(b);
                if (pierceArmor) {
                    h.damagePierce(damage);
                } else {
                    h.damage(damage);
                }
            }
        }
        if (entity instanceof Unit unit) {
            Tmp.v3.set(unit).sub(b).nor().scl(knockback * 80f);
            if (impact) Tmp.v3.setAngle(b.rotation() + (knockback < 0 ? 180f : 0f));
            unit.impulse(Tmp.v3);
            unit.apply(status, statusDuration);

            Events.fire(new EventType.UnitDamageEvent().set(unit, b));
        }
        if (!wasDead && entity instanceof Unit unit && unit.dead) {
            Events.fire(new EventType.UnitBulletDestroyEvent(unit, b));
        }
        handlePierce(b, health, entity.x(), entity.y());
        super.hit(b, b.x(), b.y());
    }

    @Override
    public void hit(Bullet b, float x, float y) {
    }

    @Override
    public void createSplashDamage(Bullet b, float x, float y) {
        if (splashDamageRadius > 0 && !b.absorbed) {
            if (WS) {
                FDamage.damage(b.team, x, y, splashDamageRadius, splashDamage * b.damageMultiplier(), splashDamagePierce, collidesAir, collidesGround, scaledSplashDamage, b);
            } else
                Damage.damage(b.team, x, y, splashDamageRadius, splashDamage * b.damageMultiplier(), splashDamagePierce, collidesAir, collidesGround, scaledSplashDamage, b);
            if (status != StatusEffects.none) {
                Damage.status(b.team, x, y, splashDamageRadius, status, statusDuration, collidesAir, collidesGround);
            }
            if (heals()) {
                indexer.eachBlock(b.team, x, y, splashDamageRadius, Building::damaged, other -> {
                    healEffect.at(other.x, other.y, 0f, healColor, other.block);
                    other.heal(healPercent / 100f * other.maxHealth() + healAmount);
                });
            }
            if (makeFire) {
                indexer.eachBlock(null, x, y, splashDamageRadius, other -> other.team != b.team, other -> Fires.create(other.tile));
            }
        }
    }

    @Override
    public void hitTile(Bullet b, Building build, float x, float y, float initialHealth, boolean direct) {
        if (WS) {
            splashDamage = ((build.maxHealth()) * percent / 100);
        }
        if (WL) {
            lightningDamage = ((build.maxHealth()) * lightningPercent * 1.503F / 100);
        }
        if (makeFire && build.team != b.team) {
            Fires.create(build.tile);
        }

        if (heals() && build.team == b.team && !(build.block instanceof ConstructBlock)) {
            healEffect.at(build.x, build.y, 0f, healColor, build.block);
            build.heal(healPercent / 100f * build.maxHealth + healAmount);
        } else if (build.team != b.team && direct) {
            hit(b);
        }

        handlePierce(b, initialHealth, x, y);
    }

    public void hit(Bullet b) {
        float x = b.x, y = b.y;
        hitEffect.at(x, y, b.rotation(), hitColor);
        hitSound.at(x, y, hitSoundPitch, hitSoundVolume);

        Effect.shake(hitShake, hitShake, b);

        if (fragOnHit) {
            createFrags(b, x, y);
        }
        createPuddles(b, x, y);
        createIncend(b, x, y);
        createUnits(b, x, y);

        if (suppressionRange > 0) {
            //bullets are pooled, require separate Vec2 instance
            Damage.applySuppression(b.team, b.x, b.y, suppressionRange, suppressionDuration, 0f, suppressionEffectChance, new Vec2(b.x, b.y));
        }

        createSplashDamage(b, x, y);

        for (int i = 0; i < lightning; i++) {
            Lightning.create(b, lightningColor, lightningDamage < 0 ? damage : lightningDamage, b.x, b.y, b.rotation() + Mathf.range(lightningCone / 2) + lightningAngle, lightningLength + Mathf.random(lightningLengthRand));
        }
    }
}