package Floor.FEntities.FBulletType;

import arc.math.Angles;
import arc.util.Time;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.gen.Teamc;

public class ownerBulletType extends BulletType {
    public ownerBulletType(float speed, float damage) {
        super(speed, damage);
    }

    public void updateHoming(Bullet b) {
        if (homingPower > 0.0001f && b.time >= homingDelay) {
            float realAimX = b.aimX < 0 ? b.x : b.aimX;
            float realAimY = b.aimY < 0 ? b.y : b.aimY;

            Teamc target;
            //home in on allies if possible
            if (heals()) {
                target = Units.closestTarget(null, realAimX, realAimY, homingRange,
                        e -> e.checkTarget(collidesAir, collidesGround) && e.team != b.team && !b.hasCollided(e.id),
                        t -> collidesGround && (t.team != b.team || t.damaged()) && !b.hasCollided(t.id)
                );
            } else {
                if (b.aimTile != null && b.aimTile.build != null && b.aimTile.build.team != b.team && collidesGround && !b.hasCollided(b.aimTile.build.id)) {
                    target = b.aimTile.build;
                } else {
                    target = Units.closestTarget(b.team, realAimX, realAimY, homingRange,
                            e -> e != null && e.checkTarget(collidesAir, collidesGround) && !b.hasCollided(e.id) && e != b.owner,
                            t -> t != null && collidesGround && !b.hasCollided(t.id) && t != b.owner);
                }
            }

            if (target != null) {
                b.vel.setAngle(Angles.moveToward(b.rotation(), b.angleTo(target), homingPower * Time.delta * 50f));
            }
        }
    }
}
