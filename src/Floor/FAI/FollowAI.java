package Floor.FAI;

import Floor.FTools.interfaces.OwnerSpawner;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.entities.Units;
import mindustry.entities.units.AIController;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.gen.Unitc;
import mindustry.type.Weapon;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.blocks.storage.CoreBlock;

public class FollowAI extends AIController {
    public Unit shooter;
    public Teamc defendTarget;

    @Override
    public void updateUnit() {
        if (shooter != null) {
            super.updateUnit();
        } else {
            init();
        }
    }

    @Override
    public void updateMovement() {
        if (shooter != null) {
            if (shooter.health <= shooter.health * 0.3f) {
                moveTo(shooter, unit.range() * 0.3f);
            } else {
                updateTarget();
                if (defendTarget != null) {
                    moveTo(defendTarget, unit.range() * 0.3f);
                } else {
                    CoreBlock.CoreBuild core = null;
                    float min = Float.MAX_VALUE, x = unit.x, y = unit.y;
                    for (CoreBlock.CoreBuild c : unit.team.cores()) {
                        float len = Mathf.dst2(x, y, c.x, c.y);
                        if (len < min) {
                            min = len;
                            core = c;
                        }
                    }
                    if (core != null && core.within(unit, unit.speed() * Time.delta * 10)) {
                        moveTo(core, 60);
                    }
                }
            }
        }
    }

    @Override
    public void init() {
        if (unit instanceof OwnerSpawner s) {
            shooter = s.spawner();
        }
    }

    public void updateTarget() {
        float x = unit.x, y = unit.y, range = unit.range() * 10;
        defendTarget = Units.closestTarget(unit.team, x, y, range,
                u -> u.hasWeapons() && u.mounts[0].weapon.getClass() == Weapon.class,
                b -> b instanceof Turret.TurretBuild t && t.peekAmmo().damage > 0);
    }
}
