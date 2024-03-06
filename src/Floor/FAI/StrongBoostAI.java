package Floor.FAI;

import Floor.FContent.FStatusEffects;
import Floor.FEntities.FUnit.F.ENGSWEISUnitEntity;
import Floor.FEntities.FUnitType.ENGSWEISUnitType;
import arc.Core;
import arc.graphics.g2d.Draw;
import arc.math.Angles;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.ai.types.FlyingAI;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.gen.Healthc;
import mindustry.gen.Teamc;
import mindustry.graphics.Pal;
import mindustry.world.blocks.storage.CoreBlock;

import static java.lang.Math.*;

public class StrongBoostAI extends FlyingAI {
    private ENGSWEISUnitType BoostUnitType = null;
    private ENGSWEISUnitEntity wu = null;
    private boolean first;
    private float reload;
    private float counter;
    private float delayCounter;
    private float delay;
    private Effect boostEffect;
    private float changeTime;
    private boolean start = false;
    private float orx;
    private float ory;
    private float changeCounter;
    private boolean hit;
    private int step;
    private Seq<Float> mx;
    private Seq<Float> my;
    private float moveX;
    private float moveY;
    private Teamc lastTarget;
    @Override
    public void updateUnit() {
        if (BoostUnitType != null && wu != null) {
            if (lastTarget instanceof Healthc h && (h.dead() || h.health() <= 0)) {
                lastTarget = null;
                start = false;
            }
            counter += Time.delta;
            if (useFallback() && (fallback != null || (fallback = fallback()) != null)) {
                fallback.unit(unit);
                fallback.updateUnit();
                return;
            }
            if (!first) {
                updateVisuals();
                updateTargeting();
            }
            if (first) {
                target = null;
            }
            updateMovement();
        } else {
            if (unit instanceof ENGSWEISUnitEntity eue && unit.type instanceof ENGSWEISUnitType eut) {
                BoostUnitType = eut;
                wu = eue;
            }
        }
    }
    @Override
    public void updateMovement() {
        unloadPayloads();
        float ux = unit.x;
        float uy = unit.y;
        if (!first || !unit.team.isAI()) {
            first = false;
            wu.first = false;
            if (unit.type.circleTarget) {
                updateTarget();
                if (target != null) {
                    circleAttack(30);
                    //circleShoot(30);
                } else{
                    unit.rotation = unit.rotation + 14;
                }
            } else if (target != null) {
                moveTo(target, unit.type.range * 0.8f);
                unit.lookAt(target);
                float x = target.x();
                float y = target.y();
                if (start && unit.rotation == Angles.moveToward(
                        unit.rotation,
                        Angles.angle(ux, uy, x, y),
                        unit.type.rotateSpeed * Time.delta * unit.speedMultiplier())
                ) {
                    if (unit.speed() <= 0.01F) {
                        start = false;
                        counter = 0;
                        delayCounter = 0;
                    }
                    delayCounter += Time.delta;
                    if (lastTarget != null && lastTarget != target) {
                        start = false;
                        lastTarget = target;
                        return;
                    }
                    lastTarget = target;
                    if (delayCounter >= delay) {
                        if (unit.speed() < 10) {
                            unit.apply(FStatusEffects.boostSpeed, 3);
                            return;
                        }
                        if (unit.speed() >= 10) {
                            float ml = (float) sqrt(orx * orx + ory * ory);
                            vec.set(orx * min(unit.speed() * 80, 68) / ml, ory * min(unit.speed() * 80, 68) / ml);
                            unit.moveAt(vec);
                            boostEffect.at(ux, -unit.hitSize() / 2 + uy, unit.rotation - 90);
                            start = false;
                            counter = 0;
                            lastTarget = null;
                        }
                    }
                }
                if (unit.within(x, y, unit.type.range * 0.9f) && counter >= reload && !start) {
                    start = true;
                    delayCounter = 0;
                    orx = x - ux;
                    ory = y - uy;
                }
            }
        } else {
            CoreBlock.CoreBuild core = unit.closestEnemyCore();
            if (core != null) {
                float cx = core.x;
                float cy = core.y;
                if (start) {
                    orx = mx.get(step);
                    ory = my.get(step);
                    if (unit.within(orx, ory, 1F) || orx < 0 || ory < 0 || (abs(moveX - ux) < unit.speed() / 20) && (abs(moveY - uy) < unit.speed() / 20)) {
                        if (step == mx.size - 1) {
                            mx.clear();
                            my.clear();
                            step = 0;
                            start = false;
                            return;
                        } else {
                            step++;
                        }
                    }
                    moveX = ux;
                    moveY = uy;
                    orx = mx.get(step);
                    ory = my.get(step);
                    float mxx = orx - ux;
                    float myy = ory - uy;
                    float mll = (float) sqrt(mxx * mxx + myy * myy);
                    vec.set(mxx * unit.speed() / mll, myy * unit.speed() / mll);
                    unit.rotation = unit.rotation + 2;
                    unit.moveAt(vec);
                } else {
                    if (mx.size == 0 && my.size == 0) {
                        float lx = cx - ux;
                        float ly = cy - uy;
                        float ll = (float) sqrt(lx * lx + ly * ly);
                        float mmx = ly * min(unit.speed() * 300, 300) / ll;
                        float mmy = lx * min(unit.speed() * 300, 300) / ll;
                        float n = 10;
                        for (int i = 1; i <= n; i++) {
                            if (i % 2 == 0) {
                                mx.add(i * lx / n + mmx + ux);
                                my.add(i * ly / n - mmy + uy);
                            } else {
                                mx.add(i * lx / n - mmx + ux);
                                my.add(i * ly / n + mmy + uy);
                            }
                        }
                    }
                    start = true;
                }
            }
            if (hit || unit.hitTime > 0 || Units.closestTarget(unit.team, ux, uy, unit.type.range * 0.8f) != null) {
                hit = true;
                changeCounter += Time.delta;
                if (changeCounter >= changeTime) {
                    first = false;
                    start = false;
                    wu.first = false;
                    unit.health = BoostUnitType.Health2;
                    unit.maxHealth(BoostUnitType.Health2);
                    return;
                }
                Draw.color(Pal.lighterOrange);
                if (changeCounter >= changeTime / 2) {
                    Draw.rect(Core.atlas.find(unit.type.name, "-2"), ux, uy, unit.rotation);
                } else {
                    Draw.rect(Core.atlas.find(unit.type.name, "-1"), ux, uy, unit.rotation);
                }
                Draw.reset();
            }
        }
    }
    @Override
    public void init() {
        if (unit.type instanceof ENGSWEISUnitType) {
            BoostUnitType = (ENGSWEISUnitType) unit.type;
            reload = BoostUnitType.reload;
            delay = BoostUnitType.delay;
            boostEffect = BoostUnitType.boostEffect;
            counter = reload;
            delayCounter = 0;
            changeCounter = 0;
            changeTime = BoostUnitType.exchangeTime;
        }
        if (unit instanceof ENGSWEISUnitEntity) {
            wu = (ENGSWEISUnitEntity) unit;
            first = wu.first;
            hit = !first;
            step = 0;
            mx = new Seq<>();
            my = new Seq<>();
        }
    }
    public void circleShoot(float circleLength){
        vec.set(target).sub(unit);

        float ang = unit.angleTo(target);
        float diff = Angles.angleDist(ang, unit.rotation());

        if(diff > 70f && vec.len() < circleLength){
            vec.setAngle(unit.vel().angle());
        }else{
            vec.setAngle(Angles.moveToward(unit.vel().angle(), vec.angle(), 20));
        }

        vec.setLength(unit.speed());

        unit.moveAt(vec);
    }
    public void updateTarget() {
        target = Units.closestTarget(unit.team, unit.x, unit.y, Float.MAX_VALUE,u -> !u.spawnedByCore());
        if (target == null) {
            Units.nearbyBuildings(unit.x, unit.y, Float.MAX_VALUE, b -> target = target == null && !(b instanceof CoreBlock.CoreBuild) && !(b.team == unit.team) ? b : target);
        }
        if (target == null) {
            target = unit.closestEnemyCore();
        }
    }
}
