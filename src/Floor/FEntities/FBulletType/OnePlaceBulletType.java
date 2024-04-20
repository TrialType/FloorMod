package Floor.FEntities.FBulletType;

import arc.Events;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.game.EventType;
import mindustry.gen.Bullet;

import static arc.util.Time.delta;

public class OnePlaceBulletType extends BasicBulletType {
    public float minNumber = 3;
    public Seq<Float> dxs = new Seq<>();
    public Seq<Float> dys = new Seq<>();
    public Seq<Float> damageRange = new Seq<>();
    public Seq<Effect> rangeEffect = new Seq<>();

    @Override
    public void hit(Bullet b, float x, float y) {
        super.hit(b, x, y);
        createDamage(b, x, y, 1);
    }

    public void createDamage(Bullet b, float x, float y, int number) {
        final boolean[] again = {false};
        for (int i = 0; i < dxs.size; i++) {
            rangeEffect.get(i).at(x + dxs.get(i), y + dys.get(i));
            Units.nearby(b.team, x + dxs.get(i), y + dys.get(i), damageRange.get(i), u -> {
                boolean dead = u.dead;
                hitEffect.at(u.x, u.y, 0, u);
                u.damage(damage);
                if (!dead && u.dead) {
                    Events.fire(new EventType.UnitBulletDestroyEvent(u, b));
                }
                if (!again[0] && u.moving()) {
                    again[0] = true;
                }
            });
        }
        if (number <= minNumber || again[0]) {
            Time.run(45 * delta, () -> createDamage(b, x, y, number + 1));
        }
    }
}
