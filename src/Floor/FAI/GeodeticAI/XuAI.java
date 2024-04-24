package Floor.FAI.GeodeticAI;

import arc.math.geom.Vec2;
import mindustry.Vars;
import mindustry.entities.Units;
import mindustry.entities.units.AIController;
import mindustry.gen.Teamc;
import mindustry.world.Tile;
import mindustry.world.modules.ItemModule;
import mindustry.world.modules.PowerModule;

public class XuAI extends AIController {
    public float mm = 0;
    public Teamc takeTarget;
    public Teamc lastTarget = null;
    public Vec2 vec = new Vec2();
    public int pathId = -1;
    public boolean[] noFound = {false};

    @Override
    public void updateUnit() {
        updateTarget();
        super.updateUnit();
    }

    @Override
    public void updateMovement() {
        if (lastTarget != takeTarget) {
            pathId = Vars.controlPath.nextTargetId();
        }
        lastTarget = takeTarget;
        if (takeTarget != null) {
            Tile tile = takeTarget.tileOn();
            if (tile != null) {
                boolean move = true;
                Vec2 ecc = new Vec2(tile.worldx(), tile.worldy());
                vec.set(tile.worldx(), tile.worldy());

                if (unit.isGrounded()) {
                    move = Vars.controlPath.getPathPosition(unit, pathId, ecc, vec, noFound);
                }

                if (move) {
                    unit.lookAt(takeTarget);

                    moveTo(vec, 0, 100f, false, null, ecc.epsilonEquals(vec, 4.1f));
                }
            }
        }
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
