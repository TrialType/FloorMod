package Floor.FAI.GeodeticAI;

import arc.math.geom.Vec2;
import mindustry.Vars;
import mindustry.entities.Units;
import mindustry.entities.units.AIController;
import mindustry.gen.Teamc;
import mindustry.world.Tile;

public class YinAI extends AIController {
    public Teamc eatTarget;
    public Vec2 vec = new Vec2();
    public int pathId = -1;
    public boolean[] noFound = {false};
    @Override
    public void updateMovement() {
        updateTarget();
        if (eatTarget != null) {
            Tile tile = eatTarget.tileOn();
            if(tile != null){
                boolean move = true;
                Vec2 ecc = new Vec2(tile.worldx(), tile.worldy());
                vec.set(tile.worldx(), tile.worldy());

                if (unit.isGrounded()) {
                    move = Vars.controlPath.getPathPosition(unit, pathId, ecc, vec, noFound);
                }

                if (move) {
                    unit.lookAt(eatTarget);

                    moveTo(vec, 0, 100f, false, null, ecc.epsilonEquals(vec, 4.1f));
                }
            }
        }
    }

    public void updateTarget() {
        eatTarget = Units.closestTarget(unit.team, unit.x, unit.y, unit.range(),
                u -> u.tileOn() != null && !u.tileOn().floor().isDeep(), b -> true);
    }
}
