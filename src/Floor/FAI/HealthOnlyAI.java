package Floor.FAI;

import Floor.FContent.FUnits;
import mindustry.entities.Units;
import mindustry.entities.units.AIController;
import mindustry.gen.Teamc;

public class HealthOnlyAI extends AIController {

    @Override
    public Teamc target(float x, float y, float range, boolean air, boolean ground) {
        Teamc t;
        t = Units.closest(unit.team, x, y, range * 25, u -> u.health < u.maxHealth && u.type.isEnemy);
        if (t == null) {
            t = Units.closest(unit.team, x, y, range * 25, u -> !(u.type == FUnits.rejuvenate || !u.type.isEnemy));
        }
        return t;
    }

    @Override
    public void updateMovement() {
        target = target(unit.x, unit.y, 10000, true, true);
        if (target != null) {
            unit.lookAt(target);
            moveTo(target, 24);
        }
    }
}
