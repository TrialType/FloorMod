package Floor.FEntities.FUnit.F;

import Floor.FEntities.FUnit.Override.FMechUnit;
import Floor.FEntities.FUnitType.WUGENANSMechUnitType;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.entities.Damage;

public class WUGENANSMechUnit extends FMechUnit {
    public boolean under = false;
    public float outTimer = -1;
    public float landTimer = -1;
    public float reload;
    public float time1;
    public float time2;

    @Override
    public void update() {
        super.update();
        if (type instanceof WUGENANSMechUnitType wut) {
            reload = wut.landReload;
            time1 = wut.outTime;
            time2 = wut.landTime;
            if (under && outTimer >= 0) {
                outTimer += Time.delta;
                elevation = Mathf.lerpDelta(elevation, 1, Time.delta / time1);
                if (outTimer >= time1) {
                    Damage.damage(team, x, y, wut.damageRadius, wut.upDamage);
                    under = false;
                    outTimer = -1;
                }
            } else if (!under && landTimer >= 0) {
                landTimer += Time.delta;
                elevation = Mathf.lerpDelta(elevation, -1, Time.delta / time2);
                if (landTimer >= time2) {
                    under = true;
                    landTimer = -1;
                }
            }

        } else if (under) {
            under = false;
            outTimer = 0;
        }
    }
}
