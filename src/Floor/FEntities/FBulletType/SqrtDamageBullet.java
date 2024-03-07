package Floor.FEntities.FBulletType;

import Floor.FTools.FDamage;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.bullet.ContinuousBulletType;
import mindustry.gen.Bullet;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;

public class SqrtDamageBullet extends ContinuousBulletType {
    public float sqrtLength;
    public float halfWidth;
    private float timer = 12;

    public void applyDamage(Bullet b) {
        timer = timer + Time.delta;
        if (timer >= 6) {
            FDamage.SqrtDamage(b, b.team, damage, b.x, b.y, b.rotation(), sqrtLength, halfWidth);
            timer = 0;
        }
    }

//    @Override
//    public void draw(Bullet b) {
//        float rot = b.rotation();
//        Draw.color(Tmp.c1.set(new Color(Color.rgba8888(1,1,1,1))).mul(1f + Mathf.absin(Time.time, 1f, 0.1f)));
//
//        float stroke = halfWidth * 2;
//
//        Lines.stroke(stroke);
//        Lines.lineAngle(b.x, b.y, rot, sqrtLength, false);
//
//        Tmp.v1.trnsExact(rot, sqrtLength);
//        Drawf.flameFront(b.x + Tmp.v1.x, b.y + Tmp.v1.y, 1, rot, sqrtLength, stroke / 2f);
//        Tmp.v1.trns(b.rotation(), sqrtLength);
//
//        Drawf.light(b.x, b.y, b.x + Tmp.v1.x, b.y + Tmp.v1.y, 0, lightColor, 0.7f);
//        Draw.reset();
//    }
}
