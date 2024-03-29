package Floor.FEntities.FBulletType;

import Floor.FTools.FDamage;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.bullet.ContinuousBulletType;
import mindustry.gen.Bullet;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.*;

public class SqrtDamageBullet extends ContinuousBulletType {
    private final Map<Bullet, Float> bulletsReload = new HashMap<>();
    public float sqrtLength = 320;
    public float halfWidth = 120;

    public void applyDamage(Bullet b) {
        bulletsReload.replaceAll((bu, f) -> f + Time.delta);
        float timer = bulletsReload.computeIfAbsent(b, f -> 0f);
        if (timer >= 6) {
            FDamage.SqrtDamage(b, b.team, damage, b.x, b.y, b.rotation(), sqrtLength, halfWidth);
            bulletsReload.put(b, 0f);
        }
    }

    @Override
    public void draw(Bullet b) {
        float rot = b.rotation();
        Draw.z(Layer.shields);
        Lines.stroke(1.5f);
        Draw.alpha(0.09f);
        Fill.rect((float) (b.x + halfWidth * 1.3 * cos(toRadians(rot))),
                (float) (b.y + halfWidth * 1.3 * sin(toRadians(rot))),
                halfWidth * 2, sqrtLength * 0.95F, rot + 90);
        Draw.reset();
    }
}
