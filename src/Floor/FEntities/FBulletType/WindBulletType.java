package Floor.FEntities.FBulletType;

import Floor.FContent.FStatusEffects;
import Floor.FTools.FDamage;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.part.DrawPart;
import mindustry.gen.Bullet;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.*;

public class WindBulletType extends BulletType {
    private final static Map<Bullet, Float> timer = new HashMap<>();
    public float effectTimer = 2;
    public boolean fillRange = true;
    public float windPower = 0.15f;
    public float bossPowerExpand = 0;
    public float windLength = 200;
    public float windWidth = 400;
    public StatusEffect applyEffect = FStatusEffects.burningV;
    public float effectTime = 240;
    public Effect windEffect = Fx.none;
    public Effect everyHit = Fx.none;

    public WindBulletType() {
        collides = absorbable = hittable = reflectable = false;
        despawnEffect = hitEffect = Fx.none;
    }

    public void applyDamage(Bullet b) {
        FDamage.triangleDamage(b, b.team, damage, (float) (b.x - windLength / 10 * cos(toRadians(b.rotation()))),
                (float) (b.y - windLength / 10 * sin(toRadians(b.rotation()))), b.rotation(),
                windLength, windWidth / 2, windPower, applyEffect, effectTime, bossPowerExpand, everyHit);
    }

    @Override
    public void draw(Bullet b) {
        float ti = timer.computeIfAbsent(b, bu -> effectTimer);
        timer.put(b, timer.get(b) + Time.delta);
        float rot = b.rotation();
        float bx = (float) (b.x - windLength / 7.5 * cos(toRadians(rot)));
        float by = (float) (b.y - windLength / 7.5 * sin(toRadians(rot)));

        if (this.parts.size > 0) {
            DrawPart.params.set(b.fin(), 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, bx, by, rot);
            DrawPart.params.life = b.fin();

            for (int i = 0; i < this.parts.size; ++i) {
                this.parts.get(i).draw(DrawPart.params);
            }
        }

        if (fillRange) {
            Draw.z(Layer.shields);
            Lines.stroke(0f);
            Draw.alpha(0.09f);
            Draw.color(trailColor);

            float x1 = (float) (bx + windLength * cos(Math.toRadians(rot)));
            float y1 = (float) (by + windLength * sin(Math.toRadians(rot)));
            float dx = (float) (windWidth * cos(toRadians(rot + 90)) / 2);
            float dy = (float) (windWidth * sin(toRadians(rot + 90)) / 2);

            Fill.tri(bx, by, x1 + dx, y1 + dy, x1 - dx, y1 - dy);
        }

        if (ti >= effectTimer && windEffect != null) {
            windEffect.at(b.x, b.y, rot, Pal.darkerGray);
            timer.put(b, 0f);
        }
        Draw.reset();
    }

    @Override
    public void update(Bullet b) {
        b.vel.setZero();
        applyDamage(b);
    }
}
