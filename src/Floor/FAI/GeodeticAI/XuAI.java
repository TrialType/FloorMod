package Floor.FAI.GeodeticAI;

import mindustry.entities.Units;
import mindustry.entities.units.AIController;
import mindustry.gen.Teamc;
import mindustry.world.modules.ItemModule;
import mindustry.world.modules.PowerModule;

public class XuAI extends AIController {
    public float mm = 0;
    public Teamc takeTarget;

    @Override
    public void updateUnit() {
        updateTarget();
        super.updateUnit();
    }

    public void updateTarget() {
        mm = 0f;
        Units.nearbyBuildings(unit.x, unit.y, 2000, b -> {
            if (b.team != unit.team) {
                ItemModule im;
                PowerModule pm = null;
                if ((im = b.items) != null || (pm = b.power) != null) {
                    float max = Math.max(pm == null ? 0 : pm.graph.getBatteryCapacity() * pm.status,
                            im == null ? 0 : im.total() * 3f);
                    if (max > mm) {
                        takeTarget = b;
                        mm = max;
                    }
                }
            }
        });
        if (takeTarget == null) {
            Units.nearbyEnemies(unit.team, unit.x, unit.y, 2000, u -> {
                if (u.stack != null && u.stack.amount > mm) {
                    takeTarget = u;
                    mm = u.stack.amount;
                }
            });
        }
    }
}
