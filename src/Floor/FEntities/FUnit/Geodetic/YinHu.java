package Floor.FEntities.FUnit.Geodetic;

import Floor.FContent.FEvents;
import Floor.FEntities.FUnit.Override.FLegsUnit;
import arc.Events;
import mindustry.entities.Units;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

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
                Events.fire(new FEvents.UnitDestroyOtherEvent(this, u));
                u.kill();
            }
        });

        Units.nearbyBuildings(x, y, hitSize, b -> {
            if (b.team != team && abs(this.angleTo(b) - rotation) <= 5 &&
                    sqrt((x - b.x) * (x - b.x) + (y - b.y) * (y - b.y)) < hitSize / 1.9) {
                b.kill();
            }
        });
    }
}
