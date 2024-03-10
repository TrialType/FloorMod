package Floor.FAI;

import Floor.FEntities.FUnit.F.WUGENANSMechUnit;
import Floor.FEntities.FUnitType.WUGENANSMechUnitType;
import arc.struct.Seq;
import mindustry.ai.types.GroundAI;
import mindustry.entities.Units;
import mindustry.gen.Building;
import mindustry.gen.Teamc;

public class LandMoveAI extends GroundAI {
    private WUGENANSMechUnit wu;
    private WUGENANSMechUnitType wut;
    private final Seq<Building> buildings = new Seq<>();
    private Teamc powerTarget;
    private boolean getting = false;
    private float powerRange;
    private float getRange;
    private boolean under;
    private boolean landing;
    private boolean upping;

    @Override
    public void updateUnit() {
        if (wu != null && wut != null) {
            if (useFallback() && (fallback != null || (fallback = fallback()) != null)) {
                fallback.unit(unit);
                fallback.updateUnit();
                return;
            }

            updateVisuals();
            updateTargeting();
            updateMovement();
        } else {
            init();
        }
    }

    @Override
    public void updateMovement() {
        unloadPayloads();

        if(upping || landing){
            return;
        }
        updatePowerTarget();
        if (powerTarget != null) {
            moveTo(powerTarget, getRange * 0.9F);
            if (unit.within(powerTarget, getRange)) {
                if(under){
                    getting = true;
                    for (Building b : buildings) {
                        wu.power += b.power.status;
                    }
                } else {
                    wu.outTimer = 0;
                    upping = true;
                }
            } else {
                getting = false;
            }
        } else if (target != null) {
            getting = false;
            if (!(unit.floorOn() == null || unit.floorOn().isDeep()) && !landing) {
                landing = true;
                wu.landTimer = 0;
            }
            if (under) {
                moveTo(target, unit.range() * 0.8F);
            }
        }
    }

    public void updatePowerTarget() {
        if (!getting) {
            powerTarget = Units.closestBuilding(unit.team, unit.x, unit.y, powerRange, b -> {
                if (b.power.graph.getBatteryCapacity() + b.power.graph.getPowerProduced() > 1000 * unit.speed()) {
                    buildings.clear();
                    Units.nearbyBuildings(b.x, b.y, getRange * 0.8F, bu -> {
                        if (b.power.graph.getBatteryCapacity() + b.power.graph.getPowerProduced() > 1000 * unit.speed()) {
                            buildings.add(bu);
                        }
                    });
                    if (buildings.size >= 4) {
                        buildings.add(b);
                        return true;
                    }
                }
                return false;
            });
            if (powerTarget == null) {
                powerTarget = unit.closestEnemyCore();
            }
        }
    }

    @Override
    public void init() {
        if (unit instanceof WUGENANSMechUnit) {
            wu = (WUGENANSMechUnit) unit;
            under = wu.under;
        }
        if (unit.type instanceof WUGENANSMechUnitType) {
            wut = (WUGENANSMechUnitType) unit.type;
            powerRange = wut.powerRange;
            getRange = wut.getRange;
        }
    }
}