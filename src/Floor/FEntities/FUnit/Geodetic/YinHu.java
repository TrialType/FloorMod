package Floor.FEntities.FUnit.Geodetic;

import Floor.FAI.GeodeticAI.YinAI;
import Floor.FContent.FEvents;
import Floor.FEntities.FUnit.Override.FLegsUnit;
import arc.Events;
import arc.util.Time;
import mindustry.entities.Units;

import static java.lang.Math.*;

public class YinHu extends FLegsUnit {
    @Override
    public int classId() {
        return 121;
    }

    public static YinHu create() {
        return new YinHu();
    }

    public void update() {
        super.update();

        Units.nearbyEnemies(team, x, y, hitSize, u -> {
            if (abs(this.angleTo(u) - rotation) <= 15 && sqrt((x - u.x) * (x - u.x) + (y - u.y) * (y - u.y)) < hitSize / 1.8) {
                if (u.maxHealth > maxHealth) {
                    boolean dead = u.dead;
                    u.damage(maxHealth / 60 / Time.delta);
                    if (!dead && u.dead) {
                        Events.fire(new FEvents.UnitDestroyOtherEvent(this, u));
                    }
                } else {
                    Events.fire(new FEvents.UnitDestroyOtherEvent(this, u));
                    u.kill();
                }
            }
        });

        Units.nearbyBuildings(x, y, hitSize, b -> {
            if (b.team != team && abs(this.angleTo(b) - rotation) <= 5 &&
                    sqrt((x - b.x) * (x - b.x) + (y - b.y) * (y - b.y)) < hitSize / 1.8) {
                if (b.maxHealth > maxHealth) {
                    b.damage(maxHealth / 60 / Time.delta);
                } else {
                    b.kill();
                }
            }
        });
    }

    @Override
    public float speed() {
        float s = super.speed();
        if (controller instanceof YinAI ya) {
            if (ya.eatTarget != null) {
                float len = (float) sqrt((x - ya.eatTarget.x()) * (x - ya.eatTarget.x()) + (y - ya.eatTarget.y()) * (y - ya.eatTarget.y()));
                s = s + 3.5f * (1 - (len / range()));
            }
        }
        return s;
    }
}
