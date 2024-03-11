package Floor.FEntities.FUnitType;

import mindustry.type.UnitType;

public class WUGENANSMechUnitType extends UnitType {
    public float landTime = 60;
    public float outTime = 60;
    public float upDamage = -1;
    public float damageRadius = 60;
    public float landReload = 3600;
    public float needPower = 10000;
    public float powerRange = 1000;
    public float getRange = 100;
    public WUGENANSMechUnitType(String name) {
        super(name);
    }
}
