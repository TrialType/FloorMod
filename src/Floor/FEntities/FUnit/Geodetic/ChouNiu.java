package Floor.FEntities.FUnit.Geodetic;

import Floor.FAI.GeodeticAI.ChouAI;
import Floor.FContent.FEvents;
import Floor.FContent.FStatusEffects;
import Floor.FEntities.FUnit.Override.FLegsUnit;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import mindustry.entities.Effect;
import mindustry.entities.Fires;
import mindustry.entities.Units;
import mindustry.graphics.Pal;

import static arc.math.Angles.randLenVectors;
import static arc.util.Time.delta;

public class ChouNiu extends FLegsUnit {
    public float stopTimer = 0;
    public float boost = 1;
    public boolean hit = false;
    public Effect movingEffect = new Effect(120, e -> {
        Draw.color(Pal.lightPyraFlame, Pal.darkPyraFlame, Color.gray, e.fin());
        randLenVectors(e.id, 3, hitSize * 3, e.rotation, 50,
                (x, y) -> Fill.circle(e.x + x, e.y + y, 1 * (1 - e.fin()))
        );
    });

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

        if (controller instanceof ChouAI ca && ca.hitTarget != null && !hit && within(ca.hitTarget, hitSize * 1.1f)) {
            hit = true;
        }

        if (moving()) {

            if (stopTimer > 0) {
                boost = Math.max(1, stopTimer / 60);
                stopTimer = 0;
            }

            movingEffect.at(x, y, 90 + rotation);

            Units.nearbyEnemies(team, x, y, hitSize * 1.1f, u -> {
                boolean dead = u.dead;
                u.damage(20 * boost * delta * damageMultiplier);
                u.apply(FStatusEffects.onePercent, 600);
                u.apply(FStatusEffects.burningV, 600);
                if (!dead && u.dead) {
                    Events.fire(new FEvents.UnitDestroyOtherEvent(this, u));
                }
            });

            Units.nearbyBuildings(x, y, hitSize * 1.1f, b -> {
                if (b.team != team) {
                    b.damage(20 * boost * delta * damageMultiplier);
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
