package Floor.FEntities.FBulletType;

import Floor.FTools.FDamage;
import arc.Events;
import arc.util.Tmp;
import mindustry.content.StatusEffects;
import mindustry.entities.Damage;
import mindustry.entities.Fires;
import mindustry.entities.bullet.EmpBulletType;
import mindustry.game.EventType;
import mindustry.gen.*;

import static mindustry.Vars.indexer;

public class PercentEmpBulletType extends EmpBulletType {
    EventType.UnitDamageEvent bulletDamageEvent = new EventType.UnitDamageEvent();
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
                h.health(h.health() - (h.maxHealth() * percent / 100));
            } else {
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

            Events.fire(bulletDamageEvent.set(unit, b));
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
}