package Floor.FAI;

import Floor.FEntities.FUnit.F.LongUnitTogether;
import arc.math.geom.Vec2;
import mindustry.Vars;
import mindustry.entities.units.AIController;
import mindustry.gen.Teamc;
import mindustry.world.Tile;

public class TogetherAi extends AIController {
    public LongUnitTogether lut = null;
    public LongUnitTogether front = null;
    public Teamc lastTarget = null;
    public Vec2 vec = new Vec2();
    public int pathId = -1;
    public boolean[] noFound = {false};
    @Override
    public void updateUnit() {
        if (lut != null) {
            super.updateUnit();
        } else {
            init();
        }
    }
    @Override
    public void updateMovement() {
        if (front == null) {
            if (lastTarget != target) {
                pathId = Vars.controlPath.nextTargetId();
            }
            lastTarget = target;
            if (target != null) {
                Tile tile = target.tileOn();
                if (tile != null) {
                    boolean move = true;
                    Vec2 ecc = new Vec2(tile.worldx(), tile.worldy());
                    vec.set(tile.worldx(), tile.worldy());

                    if (unit.isGrounded()) {
                        move = Vars.controlPath.getPathPosition(unit, pathId, ecc, vec, noFound);
                    }

                    if (move) {
                        unit.lookAt(target);

                        moveTo(vec, 0, 100f, false, null, ecc.epsilonEquals(vec, 4.1f));
                    }
                }
            }
        } else {
            Vec2 vec = new Vec2();
            vec.set(front.x - unit.x, front.y - unit.y);
            vec.setLength(Math.min(vec.len(), lut.first().speed()));
        }
    }
    @Override
    public void init() {
        lut = (LongUnitTogether) unit;
        if (lut != null) {
            front = lut.front;
        }
    }
}
