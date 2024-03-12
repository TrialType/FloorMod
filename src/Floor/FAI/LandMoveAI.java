package Floor.FAI;

import Floor.FEntities.FUnit.F.WUGENANSMechUnit;
import Floor.FEntities.FUnitType.WUGENANSMechUnitType;
import arc.util.Time;
import mindustry.ai.types.GroundAI;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.effect.ExplosionEffect;
import mindustry.gen.Player;
import mindustry.gen.Teamc;
import mindustry.graphics.Pal;
import mindustry.type.ammo.PowerAmmoType;
import mindustry.world.Block;

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
    private float effectTimer = 0;
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
            effectTimer += Time.delta;

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
            if (unit.within(powerTarget, getRange)) {
                if (!under) {
                    boolean effect;
                    if (effectTimer >= 120) {
                        effectTimer = 0;
                        effect = true;
                    } else {
                        effect = false;
                    }
                    moveTo(powerTarget, getRange * 0.4F);
                    Units.nearbyBuildings(unit.x, unit.y, getRange, b -> {
                        if (b.team != unit.team && b.power != null) {
                            float capacity = b.power.graph.getLastPowerStored();
                            float product = b.power.graph.getLastPowerProduced();
                            if (capacity > 0 || product > 0) {
                                if (b.power.status > 0) {
                                    wu.power += min(wut.needPower / 6000, capacity / 10);
                                    b.power.status -= min(b.power.status, min(wut.needPower / 6000, capacity) / capacity);
                                    if (effect) {
                                        Fx.itemTransfer.at(b.x, b.y, Math.max(getRange / 100f, 1f), Pal.power, unit);
                                    }
                                }
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
                    if (under) {
                        walkEffect.at(unit);
                    }
                }
            }
        } else if (target != null) {
            if (unit.within(target, wut.damageRadius)) {
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
                    if (under) {
                        walkEffect.at(unit);
                    }
                }
            }
        } else {
            if (under || wu.under) {
                under = wu.under = false;
            }
        }
    }

    public void updatePowerTarget() {
        powerTarget = Units.closestTarget(unit.team, unit.x, unit.y, powerRange, u -> false, b -> b.power != null && (b.power.graph.getLastPowerStored() >= 1000 || b.power.graph.getLastPowerProduced() >= 1000));
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