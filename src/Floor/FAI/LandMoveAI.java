package Floor.FAI;

import Floor.FEntities.FUnit.F.WUGENANSMechUnit;
import Floor.FEntities.FUnitType.WUGENANSMechUnitType;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.ai.types.GroundAI;
import mindustry.content.Fx;
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
    private float landReload;
    private float timer = 0;

    @Override
    public void updateUnit() {
        if (wu != null && wut != null) {
            if (timer <= landReload) {
                timer = timer + Time.delta;
            }

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

        if(wu.changing){
            return;
        }

        unloadPayloads();

        if (upping || landing) {
            if (wu.under && !under) {
                under = true;
                landing = false;
            } else if (!wu.under && under) {
                under = false;
                timer = 0;
                upping = false;
            }
            return;
        }

        updatePowerTarget();
        if (powerTarget != null) {
            Fx.healWave.at(unit);

            if (unit.within(powerTarget, getRange * 0.9F)) {
                if (!under) {
                    moveTo(powerTarget, 1F);
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
                if (!under && timer >= landReload && !(unit.floorOn() == null || unit.floorOn().isDeep())) {
                    landing = true;
                    wu.landTimer = 0;
                } else {
                    moveTo(powerTarget, getRange * 0.9F);
                }
            }
        } else if (target != null) {
            getting = false;
            if (unit.within(target, wut.damageRadius * 0.9F)) {
                if (under) {
                    upping = true;
                    wu.outTimer = 0;
                } else {
                    moveTo(target, unit.range() * 0.9F);
                }
            } else {
                if (!under && !(unit.floorOn() == null || unit.floorOn().isDeep()) && timer >= landReload) {
                    landing = true;
                    wu.landTimer = 0;
                } else {
                    moveTo(target, unit.range() * 0.9F);
                }
            }
        } else {
            if (under) {
                wu.outTimer = 0;
                upping = true;
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
            landReload = wut.landReload;
            timer = landReload;
        }
    }
}