package Floor.FAI.GeodeticAI;

import arc.math.geom.Vec2;
import mindustry.Vars;
import mindustry.entities.Units;
import mindustry.entities.units.AIController;
import mindustry.gen.Teamc;
import mindustry.world.Tile;

public class MaoAI extends AIController {
    public int pathId = -1;
    public Teamc lastTarget = null;
    public Teamc hideTarget;
    public boolean[] noFound = {false};

    @Override
    public void updateMovement() {
        updateTarget();
        if (lastTarget != hideTarget) {
            pathId = Vars.controlPath.nextTargetId();
        }
        lastTarget = hideTarget;
        if (hideTarget != null) {
            Tile tile = hideTarget.tileOn();
            if (tile != null) {
                boolean move = true;
                Vec2 ecc = new Vec2(tile.worldx(), tile.worldy());
                vec.set(tile.worldx(), tile.worldy());

                if (unit.isGrounded()) {
                    move = Vars.controlPath.getPathPosition(unit, pathId, ecc, vec, noFound);
                }

                if (move) {
                    unit.lookAt(hideTarget);

                    if (unit.within(hideTarget, 1000)) {
                        unit.moveAt(new Vec2(unit.x - hideTarget.x(), unit.y - hideTarget.y()).setLength(unit.speed()));
                    } else {
                        moveTo(vec, 1000, 100f, false, null, ecc.epsilonEquals(vec, 4.1f));
                    }
                }
            }
        }
    }

    public void updateTarget() {
        hideTarget = Units.closestTarget(unit.team, unit.x, unit.y, Float.MAX_VALUE, u -> true, b -> true);
    }
}
