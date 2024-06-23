package Floor.FAI;

import Floor.FTools.interfaces.OwnerSpawner;
import arc.math.Angles;
import arc.math.Mathf;
import mindustry.entities.units.AIController;
import mindustry.gen.Unit;

public class FollowAI extends AIController {
    public Unit shooter;

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
            moveTo(shooter, unit.range() * 0.8f);
            if (unit.within(shooter, unit.range())) {
                float dest = Mathf.range(unit.range() * 0.02f);
                float angle = Angles.angle(unit.x, unit.y, shooter.x, shooter.y) + 90;
                unit.vel.x += (float) (Math.cos(Math.toRadians(angle)) * dest);
                unit.vel.y += (float) (Math.sin(Math.toRadians(angle)) * dest);
            }
        }
    }

    @Override
    public void init() {
        if (unit instanceof OwnerSpawner s) {
            shooter = s.spawner();
        }
    }
}
