package Floor.FAI;

import arc.math.Mathf;
import mindustry.ai.types.MissileAI;
import mindustry.entities.Units;
import mindustry.gen.TimedKillc;

public class MissileAI_II extends MissileAI {
    @Override
    public void updateMovement() {
        unloadPayloads();

        float time = unit instanceof TimedKillc t ? t.time() : 1000000f;

        target = Units.closestTarget(unit.team, unit.x, unit.y, unit.range(), u -> !u.spawnedByCore() && u.hasWeapons());
        if (time >= unit.type.homingDelay && target != null) {
            unit.lookAt(target.x(), target.y());
        }

        //move forward forever
        unit.moveAt(vec.trns(unit.rotation, unit.type.missileAccelTime <= 0f ? unit.speed() : Mathf.pow(Math.min(time / unit.type.missileAccelTime, 1f), 2f) * unit.speed()));

        var build = unit.buildOn();

        //kill instantly on enemy building contact
        if (build != null && build.team != unit.team && (build == target || !build.block.underBullets)) {
            unit.kill();
        }
    }

}
