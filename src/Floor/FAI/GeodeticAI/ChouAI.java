package Floor.FAI.GeodeticAI;

import Floor.FEntities.FUnit.Geodetic.ChouNiu;
import arc.math.Angles;
import mindustry.ai.Pathfinder;
import mindustry.entities.Units;
import mindustry.entities.units.AIController;
import mindustry.gen.Flyingc;
import mindustry.gen.Healthc;
import mindustry.gen.Teamc;
import mindustry.world.Tile;

public class ChouAI extends AIController {
    public ChouNiu cn;
    public Teamc hitTarget = null;

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
            unit.rotation(Angles.angle(hitTarget.x() - unit.x, hitTarget.y() - unit.y));
            pathfind(hitTarget.tileOn());
        }
    }

    @Override
    public void init() {
        cn = (ChouNiu) unit;
    }

    public void updateTarget() {
        hitTarget = Units.closestTarget(unit.team, unit.x, unit.y, unit.range(), Flyingc::isGrounded, b -> true);
    }

    public void pathfind(Tile tile) {
        Tile targetTile = unit.tileOn();
        if (tile == null || targetTile == null) {
            return;
        }

        int costType = unit.pathType();

        if (tile == targetTile || costType == Pathfinder.costNaval && !tile.floor().isLiquid) {
            return;
        }

        if (!cn.hit) {
            unit.movePref(vec.trns(unit.angleTo(tile.worldx(), tile.worldy()), unit.speed()));
        } else {
            if (unit.vel.len() < 12f) {
                cn.hit = false;
            }
        }
    }
}
