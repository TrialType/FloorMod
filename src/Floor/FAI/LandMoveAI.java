package Floor.FAI;

import Floor.FEntities.FUnit.F.WUGENANSMechUnit;
import Floor.FEntities.FUnitType.WUGENANSMechUnitType;
import arc.util.Time;
import mindustry.ai.types.GroundAI;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.effect.ExplosionEffect;
import mindustry.gen.Healthc;
import mindustry.gen.Teamc;
import mindustry.world.blocks.storage.CoreBlock;

import static java.lang.Math.min;

public class LandMoveAI extends GroundAI {
    private WUGENANSMechUnit wu;
    private WUGENANSMechUnitType wut;
    private Teamc powerTarget;
    private float powerRange;
    private float getRange;
    private boolean under;
    private boolean landing;
    private boolean upping;
    private float landReload;
    private float timer = 0;
    private final Effect walkEffect = new ExplosionEffect() {{
        smokes = 1;
        waveLife = 0;
        sparks = 0;
        lifetime = 20;
    }};

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

        if (wu.changing) {
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
            if (unit.within(powerTarget, getRange * 0.2F)) {
                if (!under) {
                    moveTo(powerTarget, getRange * 0.1F);

                    Units.nearbyBuildings(unit.x, unit.y, getRange, b -> {
                        if (b.team != unit.team && b.power != null) {
                            float capacity = b.power.graph.getLastPowerStored();
                            if (capacity > 0) {
                                wu.power += min(wut.needPower / 6000, capacity / 10);
                                b.power.status -= min(wut.needPower / 6000, capacity) / capacity;
                            }
                        }
                    });

                } else {
                    wu.outTimer = 0;
                    upping = true;
                }
            } else {
                if (!under && timer >= landReload && !(unit.floorOn() == null || unit.floorOn().isDeep())) {
                    landing = true;
                    wu.landTimer = 0;
                } else {
                    moveTo(powerTarget, getRange * 0.7F);
                    walkEffect.at(unit);
                }
            }
        } else if (target != null) {

            if (target instanceof CoreBlock.CoreBuild || target instanceof Healthc h && h.maxHealth() > unit.maxHealth) {
                CoreBlock.CoreBuild core = (CoreBlock.CoreBuild) target;
                if (unit.within(target, wut.damageRadius * 0.9F + core.hitSize())) {
                    if (under) {
                        upping = true;
                        wu.outTimer = 0;
                    } else {
                        moveTo(target, wut.damageRadius * 0.7F + core.hitSize());
                    }
                } else {
                    if (!under && !(unit.floorOn() == null || unit.floorOn().isDeep()) && timer >= landReload) {
                        landing = true;
                        wu.landTimer = 0;
                    } else {
                        moveTo(target, wut.damageRadius * 0.7F + core.hitSize());
                        walkEffect.at(unit);
                    }
                }
            } else {
                if (unit.within(target, wut.damageRadius * 0.9F)) {
                    if (under) {
                        upping = true;
                        wu.outTimer = 0;
                    } else {
                        moveTo(target, wut.damageRadius * 0.7F);
                    }
                } else {
                    if (!under && !(unit.floorOn() == null || unit.floorOn().isDeep()) && timer >= landReload) {
                        landing = true;
                        wu.landTimer = 0;
                    } else {
                        moveTo(target, wut.damageRadius * 0.7F);
                        walkEffect.at(unit);
                    }
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
        powerTarget = Units.closestTarget(unit.team, unit.x, unit.y, powerRange, u -> false, b -> {

            if (b.power != null && b.power.graph.getLastPowerStored() >= 1000) {

                final int[] number = {0};
                Units.nearbyBuildings(b.x, b.y, getRange * 0.8F, bu -> {
                    if (b.team != unit.team && b.power.graph.getLastPowerStored() >= 1000) {
                        number[0]++;
                    }
                });

                return number[0] >= 1;

            }
            return false;
        });
        if (target == null && powerTarget == null) {
            target = unit.closestEnemyCore();
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