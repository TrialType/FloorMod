package Floor.FAI;

import Floor.FEntities.FUnit.F.windUnit;
import arc.math.Angles;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.ai.types.FlyingAI;
import mindustry.entities.Predict;
import mindustry.entities.Sized;
import mindustry.entities.Units;
import mindustry.type.Weapon;
import mindustry.world.blocks.storage.CoreBlock;

public class windMoveAI extends FlyingAI {
    private windUnit wu = null;

    @Override
    public void updateUnit() {
        if (wu != null) {
            super.updateUnit();
            wu.target = target;
        } else {
            if (unit instanceof windUnit wU) {
                this.wu = wU;
            }
        }
    }

    @Override
    public void updateTargeting() {
        updateWeapons();
    }

    @Override
    public void updateWeapons() {
        float rotation = unit.rotation - 90;
        boolean ret = retarget();

        if (ret) {
            findMainTarget();
        }

        if (unit.hasWeapons()) {
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
    }

    @Override
    public void updateMovement() {
        unloadPayloads();

        if (unit.team.isAI()) {
            CoreBlock.CoreBuild core = unit.closestEnemyCore();
            if (core != null) {
                if (unit.within(core, unit.range())) {
                    target = core;
                }
                moveTo(core, unit.range() * 0.8f);
                unit.lookAt(target == null ? core : target);
            }
        } else {
            if (target != null) {
                moveTo(target, unit.range());
                unit.lookAt(target);
            }
        }
    }

    public void init() {
        if (unit instanceof windUnit wU) {
            this.wu = wU;
        }
    }

    public void findMainTarget() {
        target = Units.closestTarget(unit.team, unit.x, unit.y, unit.range() * 6, u -> true, b -> !(b instanceof CoreBlock.CoreBuild));
    }
}
