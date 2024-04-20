package Floor.FAI.GeodeticAI;

import Floor.FEntities.FUnit.Geodetic.ChouNiu;
import arc.math.geom.Vec2;
import mindustry.Vars;
import mindustry.entities.Units;
import mindustry.entities.units.AIController;
import mindustry.gen.Healthc;
import mindustry.gen.Teamc;
import mindustry.world.Tile;

public class ChouAI extends AIController {
    public ChouNiu cn;
    public Teamc hitTarget = null;
    public Vec2 vec = new Vec2();
    public Tile last = null;
    public int pathId = -1;
    public boolean[] noFound = {false};

    @Override
    public void updateUnit() {
        if (cn != null) {
            super.updateUnit();
        } else {
            cn = (ChouNiu) unit;
        }
    }

    @Override
    public void updateMovement() {
        if (hitTarget == null || (hitTarget instanceof Healthc h && (h.dead() || h.health() <= 0))) {
            hitTarget = null;
            updateTarget();
        }

        if (hitTarget != null) {
            if (hitTarget.tileOn() != last) {
                pathId = Vars.controlPath.nextTargetId();
            }
            last = hitTarget.tileOn();
            if (last != null) {
                pathFind(last);
            }
        }
    }

    @Override
    public void init() {
        cn = (ChouNiu) unit;


    }

    public void updateTarget() {
        hitTarget = Units.closestTarget(unit.team, unit.x, unit.y, unit.range(), u -> u.tileOn() != null, b -> true);
    }

    public void pathFind(Tile tile) {

        boolean move = true;
        Vec2 ecc = new Vec2(tile.worldx(), tile.worldy());
        vec.set(tile.worldx(), tile.worldy());

        if (unit.isGrounded()) {
            move = Vars.controlPath.getPathPosition(unit, pathId, ecc, vec, noFound);
        }

        float engageRange = unit.range() - 10f;

        if (move) {
            moveTo(vec, unit.within(ecc, engageRange) ? engageRange : 0, 100f, false, null, ecc.epsilonEquals(vec, 4.1f));
        }
    }
}
