package Floor.FEntities.FUnitType;

import mindustry.type.UnitType;

public class WUGENANSMechUnitType extends UnitType {
    public float landTime = 60;
    public float outTime = 60;
    public float upDamage = -1;
    public float damageRadius = 20;
    public float landReload = 3600;
    public float needPower = 10000;
    public WUGENANSMechUnitType(String name) {
        super(name);
    }
}
