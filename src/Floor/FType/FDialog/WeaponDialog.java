package Floor.FType.FDialog;

import Floor.FEntities.FBulletType.LimitBulletType;
import arc.Core;
import arc.func.Boolp;
import arc.func.Cons;
import arc.scene.ui.layout.Table;
import mindustry.entities.effect.MultiEffect;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.type.Weapon;
import mindustry.type.weapons.PointDefenseWeapon;
import mindustry.type.weapons.RepairBeamWeapon;
import mindustry.ui.dialogs.BaseDialog;

import java.lang.reflect.Field;

import static Floor.FType.FDialog.ProjectUtils.*;
import static mindustry.Vars.ui;

public class WeaponDialog extends BaseDialog implements EffectTableGetter {
    public Weapon weapon;
    public float bulletHeavy = 0;
    public float heavy = 0;
    protected BulletDialog bulletDialog;
    protected String type = "default";
    protected Table baseOn;
    protected Table typeOn;
    protected Table effectOn;

    protected Runnable rebuildHeavy = () -> {
    };
    protected Runnable reBase = () -> {
        rebuildBase();
        rebuildHeavy.run();
    };
    protected Runnable reType = () -> {
        rebuildType();
        rebuildHeavy.run();
    };
    protected static String dia = "weapon";
    protected Boolp
            hevUser = () -> weapon.shoot.shots * bulletHeavy + heavy <= freeSize,
            targetUse = () -> couldUse("target", getVal("target")),
            reloadUse = () -> couldUse("reload", getVal("reload"));

    public WeaponDialog(String title, Weapon rollback, Cons<Weapon> apply, Cons<Float> heavyApply) {
        super(title);
        shown(this::rebuild);
        shown(() -> freeSize += this.heavy + weapon.shoot.shots * bulletHeavy);
        hidden(() -> {
            freeSize -= this.heavy;
            freeSize -= getShootVal(weapon.shoot) * bulletHeavy;
            heavyApply.get(weapon.shoot.shots * bulletHeavy + this.heavy);
        });

        if (this.weapon == null) {
            this.weapon = rollback;
            updateHeavy();
        }
        setWeapon(weapon);
        if (weapon.bullet != null) {
            updateDialog(true);
            bulletHeavy = bulletDialog.heavyOut();
        }

        buttons.button("@back", Icon.left, () -> {
            weapon = rollback;
            updateHeavy();
            hide();
        }).width(200);
        buttons.button(Core.bundle.get("@apply"), Icon.right, () -> {
            updateHeavy();
            if (heavy + weapon.shoot.shots * bulletHeavy <= freeSize) {
                apply.get(weapon);
                hide();
            } else {
                ui.showInfo(Core.bundle.get("@tooHeavy"));
            }
        }).width(200);
        buttons.button(Core.bundle.get("@setZero"), () -> {
            weapon.reload = 300;
            weapon.shoot.shots = 1;
            weapon.targetSwitchInterval = weapon.targetInterval = 240;
            if (weapon instanceof RepairBeamWeapon r) {
                r.repairSpeed = r.fractionRepairSpeed = r.beamWidth = 0;
            }
            updateHeavy();
            rebuild();
        }).width(200);
    }

    public void rebuild() {
        cont.clear();
        cont.pane(t -> {
            t.table(s -> {
                s.setBackground(Tex.buttonEdge1);
                s.label(() -> Core.bundle.get("dialog.weapon." + type)).center();
                s.table(h -> {
                    rebuildHeavy = () -> {
                        h.clear();
                        updateHeavy();
                        h.label(() -> Core.bundle.get("@heavyUse") +
                                (heavy + bulletHeavy * getShootVal(weapon.shoot)) + "/" + freeSize).pad(5);
                    };
                    rebuildHeavy.run();
                });
                s.button(b -> {
                    b.image(Icon.pencil);

                    b.clicked(() -> createSelectDialog(b, (tb, hide) -> {
                        tb.button(Core.bundle.get("dialog.weapon.default"), () -> {
                            if (type.equals("default")) {
                                hide.run();
                                return;
                            }
                            Weapon w = new Weapon();
                            cloneWeapon(weapon, w);
                            weapon = w;
                            type = "default";
                            typeOn.clear();
                            hide.run();
                        }).width(100);
                        tb.row();
                        tb.button(Core.bundle.get("dialog.weapon.defense"), () -> {
                            if (type.equals("defense")) {
                                hide.run();
                                return;
                            }
                            PointDefenseWeapon w = new PointDefenseWeapon();
                            cloneWeapon(weapon, w);
                            weapon = w;
                            type = "defense";
                            rebuildType();
                            hide.run();
                        }).width(100);
                        tb.row();
                        tb.button(Core.bundle.get("dialog.weapon.repair"), () -> {
                            if (type.equals("repair")) {
                                hide.run();
                                return;
                            }
                            RepairBeamWeapon w = new RepairBeamWeapon();
                            cloneWeapon(weapon, w);
                            weapon = w;
                            type = "repair";
                            rebuildType();
                            hide.run();
                        }).width(100);
                    }));
                }, () -> {
                }).size(25).center().pad(5);
            }).width(1400);

            t.row();
            t.table(ta -> {
                ta.setBackground(Tex.buttonEdge1);
                baseOn = ta;
            }).width(1400);
            t.row();
            t.table(ta -> {
                ta.setBackground(Tex.buttonEdge1);
                typeOn = ta;
            }).width(1400);
            rebuildBase();
            rebuildType();
        }).width(1400);
    }

    public void rebuildBase() {
        baseOn.clear();
        baseOn.table(b -> {
            b.label(() -> Core.bundle.get("dialog.weapon.bullet")).width(100);
            if (bulletDialog != null) {
                b.button(Icon.pencil, () -> {
                    if (heavy + weapon.shoot.shots * bulletHeavy <= freeSize) {
                        bulletDialog.show();
                    } else {
                        ui.showInfo(Core.bundle.get("@tooHeavy"));
                    }
                }).pad(5);
                b.button(Icon.trash, () -> {
                    updateDialog(false);
                    rebuildBase();
                }).pad(5);
            } else {
                b.button(Icon.add, () -> {
                    updateDialog(true);
                    rebuildBase();
                }).pad(5);
            }
        });
        if (!(weapon.ejectEffect instanceof MultiEffect)) {
            weapon.ejectEffect = new MultiEffect();
        }
        createEffectList(baseOn, this, dia, "ejectEffect", weapon.ejectEffect);
        createShootDialog(baseOn, dia, "shoot", getHeavy("number", getVal("number")), () -> weapon.shoot,
                s -> weapon.shoot = s, () -> couldUse("number", getVal("number")), hevUser, this::updateHeavy, this::rebuildBase);
        baseOn.row();
        createNumberDialog(baseOn, dia, "x", weapon.x, f -> weapon.x = f, reBase);
        createNumberDialog(baseOn, dia, "y", weapon.y, f -> weapon.y = f, reBase);
        createNumberDialog(baseOn, dia, "shootY", weapon.shootY, f -> weapon.shootY = f, reBase);
        baseOn.row();
        createNumberDialog(baseOn, dia, "shootX", weapon.shootX, f -> weapon.shootX = f, reBase);
        createNumberDialog(baseOn, dia, "shootCone", weapon.shootCone, f -> weapon.shootCone = f, reBase);
        createBooleanDialog(baseOn, dia, "rotate", weapon.rotate, b -> weapon.rotate = b, reBase);
        baseOn.row();
        createNumberDialog(baseOn, dia, "rotateSpeed", weapon.rotateSpeed, f -> weapon.rotateSpeed = f, reBase);
        createPartsDialog(baseOn, dia, "parts", weapon.parts);
        createNumberDialog(baseOn, dia, "baseRotation", weapon.baseRotation, f -> weapon.baseRotation = f, reBase);
        baseOn.row();
        createBooleanDialog(baseOn, dia, "mirror", weapon.mirror, b -> weapon.mirror = b, reBase);
        createBooleanDialog(baseOn, dia, "alternate", weapon.alternate, b -> weapon.alternate = b, reBase);
        createBooleanDialog(baseOn, dia, "continuous", weapon.continuous, b -> weapon.continuous = b, reBase);
        baseOn.row();
        createBooleanDialog(baseOn, dia, "alwaysContinuous", weapon.alwaysContinuous, b -> weapon.alwaysContinuous = b, reBase);
        createBooleanDialog(baseOn, dia, "controllable", weapon.controllable, b -> weapon.controllable = b, reBase);
        createBooleanDialog(baseOn, dia, "aiControllable", weapon.aiControllable, b -> weapon.aiControllable = b, reBase);
        baseOn.row();
        createBooleanDialog(baseOn, dia, "alwaysShooting", weapon.alwaysShooting, b -> weapon.alwaysShooting = b, reBase);
        createBooleanDialog(baseOn, dia, "autoTarget", weapon.autoTarget, b -> weapon.autoTarget = b, reBase);
        createBooleanDialog(baseOn, dia, "predictTarget", weapon.predictTarget, b -> weapon.predictTarget = b, reBase);
        baseOn.row();
        createBooleanDialog(baseOn, dia, "useAttackRange", weapon.useAttackRange, b -> weapon.useAttackRange = b, reBase);
        createLevDialog(baseOn, dia, "target", "targetInterval", weapon.targetInterval,
                f -> weapon.targetInterval = f, reBase, this::updateHeavy, targetUse, hevUser);
        createLevDialog(baseOn, dia, "target", "targetSwitchInterval", weapon.targetSwitchInterval,
                f -> weapon.targetSwitchInterval = f, reBase, this::updateHeavy, targetUse, hevUser);
        baseOn.row();
        createLevDialog(baseOn, dia, "reload", "reload", weapon.reload,
                f -> weapon.reload = f, reBase, this::updateHeavy, reloadUse, hevUser);
        createNumberDialog(baseOn, dia, "inaccuracy", weapon.inaccuracy, f -> weapon.inaccuracy = f, reBase);
        createNumberDialog(baseOn, dia, "shake", weapon.shake, f -> weapon.shake = f, reBase);
        baseOn.row();
        createNumberDialog(baseOn, dia, "recoil", weapon.recoil, f -> weapon.recoil = f, reBase);
        createNumberDialog(baseOn, dia, "recoils", weapon.recoils, f -> weapon.recoils = (int) (f + 0), reBase);
        createNumberDialog(baseOn, dia, "recoilTime", weapon.recoilTime, f -> weapon.recoilTime = f, reBase);
        baseOn.row();
        createNumberDialog(baseOn, dia, "recoilPow", weapon.recoilPow, f -> weapon.recoilPow = f, reBase);
        createNumberDialogWithLimit(baseOn, dia, "xRand", weapon.xRand, 25, 0, f -> weapon.xRand = f, reBase);
        createNumberDialogWithLimit(baseOn, dia, "rotationLimit", weapon.rotationLimit, 361, 0, f -> weapon.rotationLimit = f, reBase);
        baseOn.row();
        createNumberDialogWithLimit(baseOn, dia, "minWarmup", weapon.minWarmup, 0.99f, 0, f -> weapon.minWarmup = f, reBase);
        createNumberDialogWithLimit(baseOn, dia, "shootWarmupSpeed", weapon.shootWarmupSpeed, 0.99f, 0, f -> weapon.shootWarmupSpeed = f, reBase);
        createNumberDialogWithLimit(baseOn, dia, "smoothReloadSpeed", weapon.smoothReloadSpeed, 0.99f, 0, f -> weapon.smoothReloadSpeed = f, reBase);
        baseOn.row();
        createBooleanDialog(baseOn, dia, "ignoreRotation", weapon.ignoreRotation, b -> weapon.ignoreRotation = b, reBase);
        createBooleanDialog(baseOn, dia, "noAttack", weapon.noAttack, b -> weapon.noAttack = b, reBase);
        createBooleanDialog(baseOn, dia, "linearWarmup", weapon.linearWarmup, b -> weapon.linearWarmup = b, reBase);
    }

    public void rebuildType() {
        typeOn.clear();
        if (type.equals("defense")) {
            PointDefenseWeapon p = (PointDefenseWeapon) weapon;
            createColorDialog(typeOn, dia, "color", p.color,
                    c -> p.color = c, reType);
            if (!(p.beamEffect instanceof MultiEffect)) {
                p.beamEffect = new MultiEffect();
            }
            createEffectList(typeOn, this, dia, "effect", p.beamEffect);
        } else if (type.equals("repair")) {
            RepairBeamWeapon r = (RepairBeamWeapon) weapon;
            createBooleanDialog(typeOn, dia, "targetBuildings", r.targetBuildings,
                    b -> r.targetBuildings = b, reType);
            createBooleanDialog(typeOn, dia, "targetUnits", r.targetUnits,
                    b -> r.targetUnits = b, reType);
            createNumberDialogWithLimit(typeOn, dia, "recentDamageMultiplier", r.recentDamageMultiplier, 1, 0,
                    f -> r.recentDamageMultiplier = f, reType);
            typeOn.row();
            createLevDialog(typeOn, dia, "target", "repairSpeed", r.repairSpeed,
                    f -> r.repairSpeed = f, reType, this::updateHeavy, targetUse, hevUser);
            createLevDialog(typeOn, dia, "target", "fractionRepairSpeed", r.fractionRepairSpeed,
                    f -> r.fractionRepairSpeed = f, reType, this::updateHeavy, targetUse, hevUser);
            createLevDialog(typeOn, dia, "target", "beamWidth", r.beamWidth,
                    f -> r.beamWidth = f, reType, this::updateHeavy, targetUse, hevUser);
            typeOn.row();
            createNumberDialog(typeOn, dia, "pulseRadius", r.pulseRadius,
                    f -> r.pulseRadius = f, reType);
            createNumberDialog(typeOn, dia, "pulseStroke", r.pulseStroke,
                    f -> r.pulseStroke = f, reType);
            createNumberDialog(typeOn, dia, "widthSinMag", r.widthSinMag,
                    f -> r.widthSinMag = f, reType);
            typeOn.row();
            createNumberDialog(typeOn, dia, "widthSinScl", r.widthSinScl,
                    f -> r.widthSinScl = f, reType);
            if (!(r.healEffect instanceof MultiEffect)) {
                r.healEffect = new MultiEffect();
            }
            createEffectList(typeOn, this, dia, "healEffect", r.healEffect);
            createColorDialog(typeOn, dia, "laserColor", r.laserColor,
                    c -> r.laserColor = c, reType);
            typeOn.row();
            createColorDialog(typeOn, dia, "laserTopColor", r.laserTopColor,
                    c -> r.laserTopColor = c, reType);
            createColorDialog(typeOn, dia, "healColor", r.healColor,
                    c -> r.healColor = c, reType);
        }
    }

    public float getVal(String type) {
        RepairBeamWeapon r = weapon instanceof RepairBeamWeapon ? (RepairBeamWeapon) weapon : new RepairBeamWeapon() {{
            repairSpeed = fractionRepairSpeed = beamWidth = 0;
        }};
        return switch (type) {
            case "number" -> getShootVal(weapon.shoot);
            case "reload" -> weapon.reload;
            case "target" ->
                    this.type.equals("repair") ? r.repairSpeed * 15 + r.fractionRepairSpeed * 60 + r.beamWidth / 4 :
                            this.type.equals("defense") ? weapon.targetInterval * weapon.targetSwitchInterval : 500;
            default -> -1;
        };
    }

    public void updateHeavy() {
        heavy = 0.5f;
        heavy += getHeavy("number", getShootVal(weapon.shoot));
        heavy += getHeavy("reload", weapon.reload);
        heavy += getHeavy("target", weapon.targetInterval * weapon.targetSwitchInterval);
        bulletHeavy = bulletDialog == null ? 0 : bulletDialog.heavyOut();
    }

    public void setWeapon(Weapon weapon) {
        if (weapon instanceof RepairBeamWeapon r) {
            this.weapon = new RepairBeamWeapon();
            RepairBeamWeapon rb = (RepairBeamWeapon) this.weapon;
            Field[] fields = RepairBeamWeapon.class.getFields();
            for (Field field : fields) {
                try {
                    field.set(rb, field.get(r));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            type = "repair";
        } else if (weapon instanceof PointDefenseWeapon p) {
            this.weapon = new PointDefenseWeapon();
            PointDefenseWeapon pw = (PointDefenseWeapon) this.weapon;
            Field[] fields = PointDefenseWeapon.class.getFields();
            for (Field field : fields) {
                try {
                    field.set(pw, field.get(p));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            type = "defense";
        } else {
            this.weapon = new Weapon();
            cloneWeapon(weapon, this.weapon);
            weapon.reload = 500;
            weapon.shoot.shots = 1;
            weapon.targetSwitchInterval = weapon.targetInterval = 500;
            type = "default";
        }
    }

    public void cloneWeapon(Weapon from, Weapon to) {
        Field[] fields = Weapon.class.getFields();
        for (Field field : fields) {
            try {
                field.set(to, field.get(from));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void updateDialog(boolean add) {
        if (add) {
            if (!(weapon.bullet instanceof LimitBulletType)) {
                weapon.bullet = new LimitBulletType();
            }
            bulletDialog = new BulletDialog(() -> weapon.bullet, f -> bulletHeavy = f,
                    b -> weapon.bullet = b, "", () -> (int) getShootVal(weapon.shoot));
            bulletDialog.hidden(() -> freeSize += this.heavy);
            bulletDialog.shown(() -> freeSize -= this.heavy);
            bulletHeavy = bulletDialog.heavyOut();
        } else {
            bulletDialog = null;
            weapon.bullet = null;
            bulletHeavy = 0;
        }
    }

    @Override
    public Table get() {
        return effectOn;
    }

    @Override
    public void set(Table table) {
        effectOn = table;
    }
}