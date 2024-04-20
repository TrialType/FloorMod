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
import mindustry.graphics.Pal;

import static arc.util.Time.delta;
import static java.lang.Math.sqrt;

public class ChouNiu extends FLegsUnit {
    public float stopTimer = 0;
    public float lastTimer = -1;
    public float boost = 1;
    public boolean hit = false;
    public static final Vec2 rv = new Vec2();
    public final static Rand rand = new Rand();
    public final static Effect f = new Effect(120, e -> {
        Draw.color(Pal.lightPyraFlame, Pal.darkPyraFlame, Color.gray, e.fin());
        Fill.circle(e.x, e.y, (1 - e.fin()));
    });
    public Effect movingEffect = new Effect(1, e -> {
        rv.set(1, 1).setAngle(e.rotation).setLength(hitSize / 3f);
        float x = ChouNiu.this.x + rv.x;
        float y = ChouNiu.this.y + rv.y;
        for (int i = 1; i <= 16; i++) {
            rand.setSeed(e.id + 100000 * i);
            float len = rand.range(hitSize) - hitSize / 2;
            float len2 = (float) sqrt((hitSize) * (hitSize) / 4 - len * len);
            float x2 = (float) (x + len * Math.cos(Math.toRadians(e.rotation + 90))),
                    y2 = (float) (y + len * Math.sin(Math.toRadians(e.rotation + 90)));
            rv.set(1, 1).setAngle(e.rotation).setLength(len2);
            f.at(x2 + rv.x, y2 + rv.y);
        }
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
            if (!this.hit && ca.hitTarget instanceof Healthc h && !(h.dead() || h.health() <= 0) && within(ca.hitTarget, 6f)) {
                this.hit = true;
            }
        }

        if (moving()) {
            if (stopTimer > 0) {
                boost = Math.max(1, stopTimer / 60);
                lastTimer = stopTimer;
                stopTimer = 0;
            }

            movingEffect.at(x, y, rotation);

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
            lastTimer = -1;
            boost = 1;
            stopTimer += delta;
        }
    }

    public float range() {
        return this.type.maxRange * Math.max(1, Math.max(lastTimer, stopTimer) / 60);
    }
}
