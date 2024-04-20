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
    public Teamc lastTarget = null;
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
        updateTarget();
        if (lastTarget != hitTarget) {
            cn.hit = false;
        }
        lastTarget = hitTarget;

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
        hitTarget = Units.closestTarget(unit.team, unit.x, unit.y, unit.range(),
                u -> u.tileOn() != null && !u.tileOn().floor().isDeep(), b -> true);
    }

    public void pathFind(Tile tile) {
        if (!cn.hit) {
            boolean move = true;
            Vec2 ecc = new Vec2(tile.worldx(), tile.worldy());
            vec.set(tile.worldx(), tile.worldy());

            if (unit.isGrounded()) {
                move = Vars.controlPath.getPathPosition(unit, pathId, ecc, vec, noFound);
            }

            if (move) {
                unit.lookAt(hitTarget);

                moveTo(vec, 0, 100f, false, null, ecc.epsilonEquals(vec, 4.1f));
            }
        } else {
            if (!unit.within(hitTarget, 17) || !unit.moving() || (hitTarget instanceof Healthc h && (h.dead() || h.health() <= 0))) {
                cn.hit = false;
            }
        }
    }
}
