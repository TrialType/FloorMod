package Floor.FAI;

import Floor.FContent.FStatusEffects;
import Floor.FEntities.FUnit.F.ENGSWEISUnitEntity;
import Floor.FEntities.FUnitType.ENGSWEISUnitType;
import Floor.FTools.BossList;
import arc.Core;
import arc.graphics.g2d.Draw;
import arc.math.Angles;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.ai.types.FlyingAI;
import mindustry.entities.Effect;
import mindustry.entities.Predict;
import mindustry.entities.Sized;
import mindustry.entities.Units;
import mindustry.gen.Healthc;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.type.Weapon;
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
    private int order = 3;
    private int number;

    @Override
    public void updateUnit() {
        if (BoostUnitType != null && wu != null) {
            if (first) {
                target = null;
            }
            if (target == null) {
                wu.target = null;
            } else {
                wu.target = target;
            }
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
        if (!first) {
            if (unit.type.circleTarget) {
                updateTarget(true);
                if (target != null) {
                    circleShoot(30.0f);
                }
                unit.rotation = unit.rotation + 14;
            } else if (target != null) {
                float x = target.x();
                float y = target.y();
                if (start && abs(unit.rotation - Angles.moveToward(
                        unit.rotation,
                        Angles.angle(ux, uy, x, y),
                        unit.type.rotateSpeed * Time.delta * unit.speedMultiplier())) <= 5F
                ) {
                    orx = x - ux;
                    ory = y - uy;
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
                    if (delayCounter >= delay && (orx != 0 || ory != 0)) {
                        if (unit.speed() < 10) {
                            unit.apply(FStatusEffects.boostSpeed, 3);
                            return;
                        }
                        if (unit.speed() >= 10) {
                            vec.set(orx, ory);
                            vec.setLength(min(unit.speed() * 80, 68));
                            unit.moveAt(vec);
                            boostEffect.at(ux, -unit.hitSize() / 2 + uy, unit.rotation - 90);
                            start = false;
                            counter = 0;
                            lastTarget = null;
                        }
                    }
                }
                moveTo(target, unit.range() * 0.8f);
                unit.lookAt(target);
                if (unit.within(x, y, unit.range()) && counter >= reload && !start) {
                    start = true;
                    delayCounter = 0;
                }
            } else {
                updateTarget(false);
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
                        } else {
                            step++;
                        }
                    }
                    if (mx.size == 0) {
                        vec.set(cx - ux, cy - uy);
                        vec.setLength(unit.speed());
                    } else {
                        moveX = ux;
                        moveY = uy;
                        orx = mx.get(step);
                        ory = my.get(step);
                        float mxx = orx - ux;
                        float myy = ory - uy;
                        float mll = (float) sqrt(mxx * mxx + myy * myy);
                        vec.set(mxx * unit.speed() / mll, myy * unit.speed() / mll);
                    }
                    unit.rotation = unit.rotation + 2;
                    unit.moveAt(vec);
                } else {
                    if (mx.size == 0 && my.size == 0) {
                        float lx = cx - ux;
                        float ly = cy - uy;
                        float ll = (float) sqrt(lx * lx + ly * ly);
                        float mmx = ly * max(min(unit.speed() * 300, 300), 200) / ll;
                        float mmy = lx * max(min(unit.speed() * 300, 300), 200) / ll;
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
            if (hit || unit.hitTime > 0 || Units.closestTarget(unit.team, ux, uy, unit.range() * 0.8f) != null) {
                hit = true;
                changeCounter += Time.delta;
                if (changeCounter >= changeTime) {
                    first = false;
                    start = false;
                    wu.first = false;
                    unit.health = BoostUnitType.Health2;
                    unit.maxHealth(BoostUnitType.Health2);
                    if (number > 0 && unit.team.isAI()) {
                        Units.nearby(ux, uy, 100, 100, u -> {
                            if (number > 0 && u.team == unit.team && !(BossList.list.indexOf(u.type) >= 0)) {
                                if (u instanceof ENGSWEISUnitEntity) {
                                    u.hitTime = 1.0F;
                                    number--;
                                }
                            }
                        });
                    }
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
        first = wu.first;
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
            number = BoostUnitType.number;
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

    public void circleShoot(float circleLength) {
        float ux = unit.x;
        float uy = unit.y;
        float tx = target.x();
        float ty = target.y();
        float sx = tx - ux;
        float sy = ty - uy;
        if (lastTarget != target) {
            orx = sx;
            ory = sy;
            order = 3;
        }
        lastTarget = target;
        if (order == 3 && unit.within(target, circleLength)) {
            order = 2;
        } else if (order == 2 && !unit.within(target, circleLength)) {
            order = 1;
        }
        if (order == 1) {
            float angle = Angles.angle(tx, ty, ux, uy) + 5;
            vec.set((float) (sx + cos(toRadians(angle))), (float) (sy + sin(toRadians(angle))));
            orx = sx;
            ory = sy;
            order = 3;
        } else if (order == 2) {
            if (target instanceof Unit u && u.elevation == 1 && unit.within(target, 0.1F)) {
                vec.set(-orx, -ory);
            } else {
                vec.set(orx, ory);
            }
            vec.setLength(unit.speed());
        } else {
            vec.set(sx, sy);
            vec.setLength(unit.speed());
        }
        unit.moveAt(vec);
    }

    public void updateWeapons() {
        float rotation = unit.rotation - 90;
        boolean ret = retarget();

        if (ret && unit.team.isAI()) {
            target = findMainTarget(unit.x, unit.y, unit.range(), unit.type.targetAir, unit.type.targetGround);
        } else if (ret && (target == null || !(target instanceof CoreBlock.CoreBuild))) {
            updateTarget(false);
        }

        noTargetTime += Time.delta;

        if (invalid(target)) {
            target = null;
        } else {
            noTargetTime = 0f;
        }

        unit.isShooting = false;

        for (var mount : unit.mounts) {
            Weapon weapon = mount.weapon;
            float wrange = weapon.range();

            //let uncontrollable weapons do their own thing
            if (!weapon.controllable || weapon.noAttack) continue;

            if (!weapon.aiControllable) {
                mount.rotate = false;
                continue;
            }

            float mountX = unit.x + Angles.trnsx(rotation, weapon.x, weapon.y),
                    mountY = unit.y + Angles.trnsy(rotation, weapon.x, weapon.y);

            if (unit.type.singleTarget) {
                mount.target = target;
            } else {
                if (ret) {
                    mount.target = findTarget(mountX, mountY, wrange, weapon.bullet.collidesAir, weapon.bullet.collidesGround);
                }

                if (checkTarget(mount.target, mountX, mountY, wrange)) {
                    mount.target = null;
                }
            }

            boolean shoot = false;

            if (mount.target != null) {
                shoot = mount.target.within(mountX, mountY, wrange + (mount.target instanceof Sized s ? s.hitSize() / 2f : 0f)) && shouldShoot();

                Vec2 to = Predict.intercept(unit, mount.target, weapon.bullet.speed);
                mount.aimX = to.x;
                mount.aimY = to.y;
            }

            unit.isShooting |= (mount.shoot = mount.rotate = shoot);

            if (mount.target == null && !shoot && !Angles.within(mount.rotation, mount.weapon.baseRotation, 0.01f) && noTargetTime >= rotateBackTimer) {
                mount.rotate = true;
                Tmp.v1.trns(unit.rotation + mount.weapon.baseRotation, 5f);
                mount.aimX = mountX + Tmp.v1.x;
                mount.aimY = mountY + Tmp.v1.y;
            }

            if (shoot) {
                unit.aimX = mount.aimX;
                unit.aimY = mount.aimY;
            }
        }
    }

    public void updateTarget(boolean world) {
        float radius;
        if (world) {
            radius = Float.MAX_VALUE;
        } else {
            radius = unit.range() * 10;
        }
        target = Units.closestTarget(unit.team, unit.x, unit.y, radius, u -> !u.spawnedByCore());
        if (target == null) {
            Units.nearbyBuildings(unit.x, unit.y, radius, b -> target = target == null && !(b instanceof CoreBlock.CoreBuild) && !(b.team == unit.team) ? b : target);
        }
        if (target == null) {
            target = unit.closestEnemyCore();
        }
    }
}
