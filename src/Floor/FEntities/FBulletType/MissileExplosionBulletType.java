package Floor.FEntities.FBulletType;

import Floor.FTools.FDamage;
import mindustry.ai.types.MissileAI;
import mindustry.content.StatusEffects;
import mindustry.entities.Damage;
import mindustry.entities.Fires;
import mindustry.entities.bullet.ExplosionBulletType;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Unit;

import static mindustry.Vars.indexer;

public class MissileExplosionBulletType extends ExplosionBulletType {
    public MissileExplosionBulletType(float splashDamage, float splashDamageRadius) {
        super(splashDamage, splashDamageRadius);
    }

    @Override
    public void createSplashDamage(Bullet b, float x, float y) {
        if (splashDamageRadius > 0 && !b.absorbed) {
            if (b.owner instanceof Unit u && u.controller() instanceof MissileAI) {
                FDamage.EventMissileDamage(b.team, x, y, splashDamageRadius, splashDamage * b.damageMultiplier(), splashDamagePierce, collidesAir, collidesGround, scaledSplashDamage, u);
            } else {
                Damage.damage(b.team, x, y, splashDamageRadius, splashDamage * b.damageMultiplier(), splashDamagePierce, collidesAir, collidesGround, scaledSplashDamage, b);
            }


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
