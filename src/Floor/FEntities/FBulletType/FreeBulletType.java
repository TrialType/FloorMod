package Floor.FEntities.FBulletType;

import arc.math.geom.Geometry;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Unit;

import java.util.HashMap;
import java.util.Map;

public class FreeBulletType extends BulletType {
    private final static Map<Bullet, Float> damages = new HashMap<>();
    public boolean pointWithFrag = true;
    public boolean pointWithUnit = true;
    public boolean point = false;
    private float cdist = 0.0F;
    private Unit result;
    public float trailSpacing = 10.0F;

    public Effect damageEffect = Fx.none;
    public float damageReload = 10;

    @Override
    public void update(Bullet b) {
        float ti = damages.computeIfAbsent(b, bu -> damageReload);
        damages.put(b, damages.get(b) + Time.delta);

        super.update(b);


        if (b.isAdded()) {
            if (ti >= damageReload && damageEffect != null) {
                damageEffect.at(b.x, b.y, 0, b);
                damages.put(b, 0f);
            }
        } else {
            damages.remove(b);
        }
    }

    public void init(Bullet b) {
        super.init(b);
        if (point) {
            this.scaleLife = true;
            this.collides = false;
            this.reflectable = false;
            this.keepVelocity = false;
            this.backMove = false;

            float px = b.x + b.lifetime * b.vel.x;
            float py = b.y + b.lifetime * b.vel.y;
            float rot = b.rotation();
            Geometry.iterateLine(0.0F, b.x, b.y, px, py, this.trailSpacing, (x, y) -> this.trailEffect.at(x, y, rot));
            b.time = b.lifetime;
            b.set(px, py);
            cdist = 0.0F;
            result = null;
            float range = 1.0F;
            Units.nearbyEnemies(b.team, px - range, py - range, range * 2.0F, range * 2.0F, (e) -> {
                if (!e.dead() && e.checkTarget(this.collidesAir, this.collidesGround) && e.hittable()) {
                    e.hitbox(Tmp.r1);
                    if (Tmp.r1.contains(px, py)) {
                        float dst = e.dst(px, py) - e.hitSize;
                        if (result == null || dst < cdist) {
                            result = e;
                            cdist = dst;
                        }

                    }
                }
            });
            if (result != null) {
                b.collision(result, px, py);
            } else if (this.collidesTiles) {
                Building build = Vars.world.buildWorld(px, py);
                if (build != null && build.team != b.team) {
                    build.collision(b);
                }
            }
            if (pointWithFrag) {
                createFrags(b, b.x, b.y);
            }
            if (pointWithUnit) {
                createUnits(b, b.x, b.y);
            }

            b.remove();
            b.vel.setZero();
        }
    }
}
