package Floor.FEntities.FBulletType;

import arc.Events;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Bullet;

import static arc.util.Time.delta;

public class OnePlaceBulletType extends BasicBulletType {
    public static final Vec2 vec = new Vec2();
    public float oneDamage = 100;
    public float minNumber = 3;
    public float rangePower = 1f;
    public float centerPower = 0.1f;
    public Seq<Float> dxs = new Seq<>();
    public Seq<Float> dys = new Seq<>();
    public Seq<Float> damageRange = new Seq<>();
    public Seq<Effect> rangeEffect = new Seq<>();

    public void addPlace(float x, float y, float ra) {
        addPlace(x, y, ra, Fx.none);
    }

    public void addPlace(float x, float y, float ra, Effect rf) {
        dxs.add(x);
        dys.add(y);
        damageRange.add(ra);
        rangeEffect.add(rf);
    }

    @Override
    public void hit(Bullet b, float x, float y) {
        super.hit(b, x, y);
        createDamage(b, b.rotation(), b.team, x, y, 1);
    }

    public void createDamage(Bullet b, float rotation, Team team, float x, float y, int number) {
        final boolean[] again = {false};
        for (int i = 0; i < dxs.size; i++) {
            float xx = dxs.get(i), yy = dys.get(i);
            float dx = (float) (Math.cos(Math.toRadians(rotation)) * yy),
                    dy = (float) (Math.sin(Math.toRadians(rotation)) * yy);
            float dx2 = (float) (Math.cos(Math.toRadians(rotation + 90)) * xx),
                    dy2 = (float) (Math.sin(Math.toRadians(rotation + 90)) * xx);
            float cx = x + dx + dx2, cy = y + dy + dy2;
            float len = damageRange.get(i);
            rangeEffect.get(i).at(cx, cy, 0, len);
            Units.nearbyEnemies(team, cx, cy, len * (1 + rangePower), u -> {
                if (u.within(cx, cy, len * (1 + centerPower))) {
                    if (!again[0] && u.moving()) {
                        again[0] = true;
                    }
                }
                if (u.within(cx, cy, len)) {
                    boolean dead = u.dead;
                    hitEffect.at(u.x, u.y, 0, u);
                    u.damage(oneDamage);
                    if (!dead && u.dead) {
                        Events.fire(new EventType.UnitBulletDestroyEvent(u, b));
                    }
                } else {
                    vec.set(cx - u.x, cy - u.y).setLength(len * centerPower);
                    u.vel.add(vec);
                }
            });
            Units.nearbyBuildings(x + dxs.get(i), y + dys.get(i), len, bu -> {
                if (bu.team != team) {
                    hitEffect.at(bu.x, bu.y, 0, bu);
                    bu.damage(oneDamage);
                }
            });
        }
        if (number <= minNumber || again[0]) {
            Time.run(25 * delta, () -> createDamage(b, rotation, team, x, y, number + 1));
        }
    }
}
