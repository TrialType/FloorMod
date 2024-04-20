package Floor.FEntities.FUnit.Geodetic;

import Floor.FAI.GeodeticAI.ChouAI;
import Floor.FContent.FEvents;
import Floor.FContent.FStatusEffects;
import Floor.FEntities.FUnit.Override.FLegsUnit;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Rand;
import arc.math.geom.Vec2;
import mindustry.entities.Effect;
import mindustry.entities.Fires;
import mindustry.entities.Units;
import mindustry.gen.Healthc;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;

import static arc.util.Time.delta;
import static java.lang.Math.abs;

public class ChouNiu extends FLegsUnit {
    public float stopTimer = 0;
    public float boost = 1;
    public boolean hit = false;
    public final static Vec2 ev = new Vec2();
    public final static Rand rand = new Rand();
    public Effect movingEffect = new Effect(120, e -> {
        Draw.color(Pal.lightPyraFlame, Pal.darkPyraFlame, Color.gray, e.fin());
        Unit cn = (Unit) e.data;
        ev.rotate(e.rotation).setLength(cn.hitSize);
        rand.setSeed(e.id);
        Fill.circle(cn.x + ev.x, cn.y + ev.y, (1 - e.fin()));
    });

    public Effect hitEffect = new Effect();

    @Override
    public int classId() {
        return 120;
    }

    public static ChouNiu create() {
        return new ChouNiu();
    }

    @Override
    public void update() {
        super.update();

        if (controller instanceof ChouAI ca) {
            hit = ca.hitTarget != null && !hit && within(ca.hitTarget, hitSize * 1.1f) &&
                    (ca.hitTarget instanceof Healthc h && !h.dead());
        }

        if (moving()) {

            if (stopTimer > 0) {
                boost = Math.max(1, stopTimer / 60);
                stopTimer = 0;
            }

            if (abs(vel.angle() - rotation) < 5) {
                movingEffect.at(x, y, vel.angle(), this);
            }

            Units.nearbyEnemies(team, x, y, hitSize * 1.1f, u -> {
                boolean dead = u.dead;
                u.damage(20 * boost * delta * damageMultiplier);
                hitEffect.at(u);
                u.apply(FStatusEffects.onePercent, 600);
                u.apply(FStatusEffects.burningV, 600);
                if (!dead && u.dead) {
                    Events.fire(new FEvents.UnitDestroyOtherEvent(this, u));
                }
            });

            Units.nearbyBuildings(x, y, hitSize * 1.1f, b -> {
                if (b.team != team) {
                    b.damage(20 * boost * delta * damageMultiplier);
                    hitEffect.at(b);
                    b.applySlowdown(0.01f, 600);
                    Fires.create(b.tile);
                }
            });

        } else if (!(controller instanceof ChouAI ca && ca.hitTarget != null)) {
            boost = 1;
            stopTimer += delta;
        }
    }

    public float range() {
        return this.type.maxRange * Math.max(1, stopTimer / 60);
    }
}
