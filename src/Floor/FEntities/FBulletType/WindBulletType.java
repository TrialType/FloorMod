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

import static java.lang.Math.toRadians;

public class WindBulletType extends BulletType {
    public float windPower = 0.15f;
    public float windLength = 200;
    public float windWidth = 400;
    public StatusEffect applyEffect = FStatusEffects.burningV;
    public float effectTime = 240;
    public Effect windEffect = Fx.none;

    public void applyDamage(Bullet b) {
        FDamage.triangleDamage(b, b.team, damage, b.x, b.y, b.rotation(), windLength, windWidth / 2, windPower, applyEffect, effectTime);
    }

    @Override
    public void draw(Bullet b) {
        float rot = b.rotation();
        Draw.z(Layer.shields);
        Lines.stroke(1.5f);
        Draw.alpha(0.09f);
        float bx = b.x;
        float by = b.y;
        float x1 = (float) (bx + windLength * Math.cos(Math.toRadians(rot)));
        float y1 = (float) (by + windLength * Math.sin(Math.toRadians(rot)));
        float dx = (float) (windWidth * Math.cos(toRadians(rot + 90)) / 2);
        float dy = (float) (windWidth * Math.sin(toRadians(rot + 90)) / 2);
        Fill.tri(bx, by, x1 + dx, y1 + dy, x1 - dx, y1 - dy);
        Draw.reset();
    }

    @Override
    public void update(Bullet b) {
        b.vel.setZero();
        applyDamage(b);
    }
}
