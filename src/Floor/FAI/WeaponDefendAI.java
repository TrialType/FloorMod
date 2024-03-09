package Floor.FAI;

import mindustry.ai.types.FlyingAI;
import mindustry.entities.Units;
import mindustry.gen.Teamc;
import mindustry.gen.Unitc;
import mindustry.world.blocks.defense.turrets.Turret;

public class WeaponDefendAI extends FlyingAI {
    @Override
    public Teamc findMainTarget(float x, float y, float range, boolean air, boolean ground) {
        return Units.closestTarget(unit.team, x, y, Float.MAX_VALUE, Unitc::hasWeapons, b -> b instanceof Turret.TurretBuild);
    }
}
