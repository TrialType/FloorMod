package Floor.FEntities.FBulletType;

import Floor.FContent.FStatusEffects;
import Floor.FTools.FDamage;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.graphics.Layer;
import mindustry.type.StatusEffect;

import static java.lang.Math.*;

public class WindBulletType extends BulletType {
    public float windPower = 0.15f;
    public float bossPowerExpand = 0;
    public float windLength = 200;
    public float windWidth = 400;
    public StatusEffect applyEffect = FStatusEffects.burningV;
    public float effectTime = 240;
    public Effect windEffect = Fx.none;

    public WindBulletType() {
        despawnEffect = hitEffect = Fx.none;
    }

    public void applyDamage(Bullet b) {
        FDamage.triangleDamage(b, b.team, damage, (float) (b.x - windLength / 10 * cos(toRadians(b.rotation()))),
                (float) (b.y - windLength / 10 * sin(toRadians(b.rotation()))), b.rotation(),
                windLength, windWidth / 2, windPower, applyEffect, effectTime,bossPowerExpand);
    }

    @Override
    public void draw(Bullet b) {
        float rot = b.rotation();
        Draw.z(Layer.shields);
        Lines.stroke(0.01f);
        Draw.alpha(0.09f);
        Draw.color(trailColor);
        float bx = (float) (b.x - windLength / 10 * cos(toRadians(rot)));
        float by = (float) (b.y - windLength / 10 * sin(toRadians(rot)));
        float x1 = (float) (bx + windLength * cos(Math.toRadians(rot)));
        float y1 = (float) (by + windLength * sin(Math.toRadians(rot)));
        float dx = (float) (windWidth * cos(toRadians(rot + 90)) / 2);
        float dy = (float) (windWidth * sin(toRadians(rot + 90)) / 2);
        Fill.tri(bx, by, x1 + dx, y1 + dy, x1 - dx, y1 - dy);
        Draw.reset();
    }

    @Override
    public void update(Bullet b) {
        b.vel.setZero();
        applyDamage(b);
    }
}
