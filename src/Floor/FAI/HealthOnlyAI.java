package Floor.FAI;

import mindustry.entities.Units;
import mindustry.entities.units.AIController;
import mindustry.gen.Teamc;

public class HealthOnlyAI extends AIController {
    private Teamc t;

    @Override
    public Teamc target(float x, float y, float range, boolean air, boolean ground) {
        t = null;
        Units.nearby(unit.team, x, y, range, u -> {
            if (u.health <= u.maxHealth * 0.85f) {
                t = u;
            }
        });
        if (t == null) {
            Units.nearby(unit.team, x, y, range, u -> {
                if (u.health < u.maxHealth) {
                    t = u;
                }
            });
            return t;
        } else {
            return t;
        }
    }

    @Override
    public void updateMovement() {
        if (target != null) {
            unit.lookAt(target);
            moveTo(target, 24);
        } else {
            target(unit.x, unit.y, 10000, true, true);
        }
    }
}
