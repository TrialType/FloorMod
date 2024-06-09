package Floor.FType.FDialog;

import Floor.FEntities.FBulletType.LimitBulletType;
import arc.Core;
import arc.func.Boolp;
import arc.func.Cons;
import arc.func.Prov;
import arc.graphics.Color;
import arc.scene.ui.layout.Table;
import mindustry.entities.abilities.*;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.ui.dialogs.BaseDialog;

import static Floor.FType.FDialog.ProjectUtils.*;
import static mindustry.Vars.ui;

public class AbilityDialog extends BaseDialog implements EffectTableGetter {
    public Table type, effect;
    public Ability ability;
    public BulletDialog dialog;
    public float heavy, bulletHeavy;
    public String aType;
    public Boolp
            heavyUse = () -> heavy + bulletHeavy <= freeSize,
            powerUse = () -> couldUse("abilityPower", findVal("abilityPower")),
            boostUse = () -> couldUse("abilityBoost", findVal("abilityBoost")),
            statusUse = () -> couldUse("abilityStatus", findVal("abilityStatus"));
    protected static String dia = "ability";

    public AbilityDialog(String title, Prov<Ability> def, Cons<Ability> apply, Cons<Float> heavyApply) {
        super(title);
        setAbility(def.get());
        updateHeavy();
        shown(this::rebuild);

        buttons.button("@back", Icon.left, () -> {
            apply.get(def.get());
            hide();
        });
        buttons.button("@apply", Icon.right, () -> {
            updateHeavy();
            if (heavy + bulletHeavy <= freeSize) {
                apply.get(ability);
                heavyApply.get(heavy);
            } else {
                ui.showInfo(Core.bundle.get("@tooHeavy"));
            }
            hide();
        });
    }

    public void rebuild() {
        cont.clear();
        cont.pane(t -> {
            t.setBackground(Tex.buttonEdge1);
            t.label(() -> Core.bundle.get("dialog.ability." + aType)).width(100);
            t.label(() -> Core.bundle.get("@heavyUse") + ": " + heavy + bulletHeavy).width(100);
            t.button(b -> {
                b.image(Icon.pencil);

                b.clicked(() -> createSelectDialog(b, (tb, hide) -> {
                    tb.clear();
                    tb.button(Core.bundle.get("dialog.ability.repairField"), () -> {
                        if (!aType.equals("repairField")) {
                            setAbility(new RepairFieldAbility(0, 500, 0));
                            updateHeavy();
                        }
                        hide.run();
                    }).width(100).row();
                    tb.button(Core.bundle.get("dialog.ability.suppressionField"), () -> {
                        if (!aType.equals("suppressionField")) {
                            setAbility(new SuppressionFieldAbility());
                            updateHeavy();
                        }
                        hide.run();
                    }).width(100).row();
                    tb.button(Core.bundle.get("dialog.ability.regen"), () -> {
                        if (!aType.equals("regen")) {
                            setAbility(new RegenAbility());
                            updateHeavy();
                        }
                        hide.run();
                    }).width(100).row();
                    tb.button(Core.bundle.get("dialog.ability.forceField"), () -> {
                        if (!aType.equals("forceField")) {
                            setAbility(new ForceFieldAbility(0, 0, 0, 500));
                            updateHeavy();
                        }
                        hide.run();
                    }).width(100).row();
                    tb.button(Core.bundle.get("dialog.ability.shieldArc"), () -> {
                        if (!aType.equals("shieldArc")) {
                            setAbility(new ShieldArcAbility());
                            updateHeavy();
                        }
                        hide.run();
                    }).width(100).row();
                    tb.button(Core.bundle.get("dialog.ability.armorPlate"), () -> {
                        if (!aType.equals("armorPlate")) {
                            setAbility(new ArmorPlateAbility());
                            updateHeavy();
                        }
                        hide.run();
                    }).width(100).row();
                    tb.button(Core.bundle.get("dialog.ability.moveLightning"), () -> {
                        if (!aType.equals("moveLightning")) {
                            setAbility(new MoveLightningAbility(0, 0, 0, 0, 0, 0, new Color()));
                            updateHeavy();
                        }
                        hide.run();
                    }).width(100).row();
                }));
            }, () -> {
            }).width(100).pad(5);
        }).width(1400);
        cont.row();
        cont.pane(t -> {
            type = t;
            type.setBackground(Tex.buttonEdge1);
            rebuildType();
        }).width(1400);
    }

    public void rebuildType() {
        type.clear();

        switch (aType) {
            case "repairField" -> {
                RepairFieldAbility a = (RepairFieldAbility) ability;
                createLevDialog(type, dia, "abilityStatus", "amount", a.amount,
                        f -> a.amount = f, this::rebuildType, this::updateHeavy, statusUse, heavyUse);
                createLevDialog(type, dia, "abilityPower", "reload", a.reload,
                        f -> a.reload = f, this::rebuildType, this::updateHeavy, powerUse, heavyUse);
                createLevDialog(type, dia, "abilityBoost", "range", a.range,
                        f -> a.range = f, this::rebuildType, this::updateHeavy, boostUse, heavyUse);
                type.row();
                createEffectList(type, this, dia, "healEffect", () -> a.healEffect, e -> a.healEffect = e);
                createEffectList(type, this, dia, "activeEffect", () -> a.activeEffect, e -> a.activeEffect = e);
                createBooleanDialog(type, dia, "parentizeEffects", a.parentizeEffects,
                        b -> a.parentizeEffects = b, this::rebuildType);
            }
            case "suppressionField" -> {
                SuppressionFieldAbility a = (SuppressionFieldAbility) ability;
                createLevDialog(type, dia, "abilityStatus", "applyParticleChance", a.applyParticleChance,
                        f -> a.applyParticleChance = f, this::rebuildType, this::updateHeavy, statusUse, heavyUse);
                createLevDialog(type, dia, "abilityPower", "reload", a.reload,
                        f -> a.reload = f, this::rebuildType, this::updateHeavy, powerUse, heavyUse);
                createLevDialog(type, dia, "abilityBoost", "range", a.range,
                        f -> a.range = f, this::rebuildType, this::updateHeavy, boostUse, heavyUse);
                type.row();
                createNumberDialog(type, dia, "orbRadius", a.orbRadius,
                        f -> a.orbRadius = f, this::rebuildType);
                createNumberDialog(type, dia, "orbMidScl", a.orbMidScl,
                        f -> a.orbMidScl = f, this::rebuildType);
                createNumberDialog(type, dia, "orbSinScl", a.orbSinScl,
                        f -> a.orbSinScl = f, this::rebuildType);
                type.row();
                createNumberDialog(type, dia, "orbSinMag", a.orbSinMag,
                        f -> a.orbSinMag = f, this::rebuildType);
                createNumberDialog(type, dia, "layer", a.layer,
                        f -> a.layer = f, this::rebuildType);
                createColorDialog(type, dia, "color", a.color,
                        c -> a.color = c, this::rebuildType);
                type.row();
                createNumberDialogWithLimit(type, dia, "x", a.x, 15, 0,
                        f -> a.x = f, this::rebuildType);
                createNumberDialogWithLimit(type, dia, "y", a.y, 15, -15,
                        f -> a.y = f, this::rebuildType);
                createNumberDialog(type, dia, "particleSize", a.particleSize,
                        f -> a.particleSize = f, this::rebuildType);
                type.row();
                createNumberDialog(type, dia, "particleLen", a.particleLen,
                        f -> a.particleLen = f, this::rebuildType);
                createNumberDialog(type, dia, "rotateScl", a.rotateScl,
                        f -> a.rotateScl = f, this::rebuildType);
                createNumberDialog(type, dia, "particleLife", a.particleLife,
                        f -> a.particleLife = f, this::rebuildType);
                type.row();
                createNumberDialog(type, dia, "particles", a.particles,
                        f -> a.particles = (int) (f + 0), this::rebuildType);
                createInterpolSelect(type, dia, "particleInterp", i -> a.particleInterp = i);
                createColorDialog(type, dia, "particleColor", a.particleColor,
                        c -> a.particleColor = c, this::rebuildType);
            }
            case "regen" -> {
                RegenAbility a = (RegenAbility) ability;
                createLevDialog(type, dia, "abilityStatus", "percentAmount", a.percentAmount,
                        f -> a.percentAmount = f, this::rebuildType, this::updateHeavy, statusUse, heavyUse);
                createLevDialog(type, dia, "abilityStatus", "amount", a.amount,
                        f -> a.amount = f, this::rebuildType, this::updateHeavy, statusUse, heavyUse);
            }
            case "forceField" -> {
                ForceFieldAbility a = (ForceFieldAbility) ability;
                createLevDialog(type, dia, "abilityBoost", "radius", a.radius,
                        f -> a.radius = f, this::rebuildType, this::updateHeavy, boostUse, heavyUse);
                createLevDialog(type, dia, "abilityStatus", "regen", a.regen,
                        f -> a.regen = f, this::rebuildType, this::updateHeavy, statusUse, heavyUse);
                createLevDialog(type, dia, "abilityStatus", "max", a.max,
                        f -> a.max = f, this::rebuildType, this::updateHeavy, statusUse, heavyUse);
                type.row();
                createLevDialog(type, dia, "abilityPower", "cooldown", a.cooldown,
                        f -> a.cooldown = f, this::rebuildType, this::updateHeavy, powerUse, heavyUse);
                createNumberDialog(type, dia, "sides", a.sides,
                        f -> a.sides = (int) (f + 0), this::rebuildType);
                createNumberDialog(type, dia, "rotation", a.rotation,
                        f -> a.rotation = f, this::rebuildType);
            }
            case "shieldArc" -> {
                ShieldArcAbility a = (ShieldArcAbility) ability;
                createLevDialog(type, dia, "abilityBoost", "radius", a.radius,
                        f -> a.radius = f, this::rebuildType, this::updateHeavy, boostUse, heavyUse);
                createLevDialog(type, dia, "abilityStatus", "regen", a.regen,
                        f -> a.regen = f, this::rebuildType, this::updateHeavy, statusUse, heavyUse);
                createLevDialog(type, dia, "abilityStatus", "max", a.max,
                        f -> a.max = f, this::rebuildType, this::updateHeavy, statusUse, heavyUse);
                type.row();
                createLevDialog(type, dia, "abilityPower", "cooldown", a.cooldown,
                        f -> a.cooldown = f, this::rebuildType, this::updateHeavy, powerUse, heavyUse);
                createLevDialog(type, dia, "abilityBoost", "width", a.width,
                        f -> a.width = f, this::rebuildType, this::updateHeavy, boostUse, heavyUse);
                createNumberDialog(type, dia, "angle", a.angle,
                        f -> a.angle = (int) (f + 0), this::rebuildType);
                type.row();
                createNumberDialog(type, dia, "angleOffset", a.angleOffset,
                        f -> a.angleOffset = f, this::rebuildType);
                createBooleanDialog(type, dia, "whenShooting", a.whenShooting,
                        b -> a.whenShooting = b, this::rebuildType);
                createBooleanDialog(type, dia, "drawArc", a.drawArc,
                        b -> a.drawArc = b, this::rebuildType);
                type.row();
                createNumberDialogWithLimit(type, dia, "x", a.x, 12, 0,
                        f -> a.x = f, this::rebuildType);
                createNumberDialogWithLimit(type, dia, "x", a.x, 12, -12,
                        f -> a.x = f, this::rebuildType);
            }
            case "armorPlate" -> {
                ArmorPlateAbility a = (ArmorPlateAbility) ability;
                createLevDialog(type, dia, "abilityStatus", "healthMultiplier", a.healthMultiplier,
                        f -> a.healthMultiplier = f, this::rebuildType, this::updateHeavy, statusUse, heavyUse);
                createNumberDialogWithLimit(type, dia, "z", a.z, 220, -11,
                        f -> a.z = f, this::rebuildType);
                createColorDialog(type, dia, "color", a.color, c -> a.color = c, this::rebuildType);
            }
            case "moveLightning" -> {
                MoveLightningAbility a = (MoveLightningAbility) ability;
                createLevDialog(type, dia, "abilityStatus", "damage", a.damage,
                        f -> a.damage = f, this::rebuildType, this::updateHeavy, statusUse, heavyUse);
                createLevDialog(type, dia, "abilityBoost", "length", a.length,
                        f -> a.length = (int) (f + 0), this::rebuildType, this::updateHeavy, boostUse, heavyUse);
                createNumberDialogWithLimit(type, dia, "chance", a.chance, 1, 0,
                        f -> a.chance = f, this::rebuildType);
                type.row();
                createNumberDialog(type, dia, "minSpeed", a.minSpeed,
                        f -> a.minSpeed = f, this::rebuildType);
                createNumberDialog(type, dia, "maxSpeed", a.maxSpeed,
                        f -> a.maxSpeed = f, this::rebuildType);
                createColorDialog(type, dia, "color", a.color, c -> a.color = c, this::rebuildType);
                type.row();
                createNumberDialogWithLimit(type, dia, "x", a.x, 12, 0,
                        f -> a.x = f, this::rebuildType);
                createNumberDialogWithLimit(type, dia, "y", a.y, 12, -12,
                        f -> a.y = f, this::rebuildType);
                createBooleanDialog(type, dia, "alternate", a.alternate,
                        b -> a.alternate = b, this::rebuildType);
                type.row();
                createNumberDialog(type, dia, "bulletAngle", a.bulletAngle,
                        f -> a.bulletAngle = f, this::rebuildType);
                createNumberDialog(type, dia, "bulletSpread", a.bulletSpread,
                        f -> a.bulletSpread = f, this::rebuildType);
                type.table(t -> {
                    t.label(() -> Core.bundle.get("dialog.ability.bullet")).width(100);
                    t.button(Icon.pencil, () -> dialog.show()).pad(5);
                }).width(250);
                type.row();
                createEffectList(type, this, dia, "shootEffect",
                        () -> a.shootEffect, e -> a.shootEffect = e);
                createSoundSelect(type, dia, "shootSound", s -> a.shootSound = s);
                createBooleanDialog(type, dia, "parentizeEffects", a.parentizeEffects,
                        b -> a.parentizeEffects = b, this::rebuildType);
            }
        }
    }

    public void setAbility(Ability ability) {
        if (ability instanceof SuppressionFieldAbility a) {
            SuppressionFieldAbility sfa = new SuppressionFieldAbility();
            sfa.active = a.active;
            sfa.x = a.x;
            sfa.y = a.y;
            sfa.applyParticleChance = a.applyParticleChance;
            sfa.color = a.color;
            sfa.layer = a.layer;
            sfa.orbMidScl = a.orbMidScl;
            sfa.orbRadius = a.orbRadius;
            sfa.orbSinMag = a.orbSinMag;
            sfa.orbSinScl = a.orbSinScl;
            sfa.particleColor = a.particleColor;
            sfa.particleInterp = a.particleInterp;
            sfa.particleLen = a.particleLen;
            sfa.particleLife = a.particleLife;
            sfa.particles = a.particles;
            sfa.particleSize = a.particleSize;
            sfa.range = a.range;
            sfa.reload = a.reload;
            sfa.rotateScl = a.rotateScl;
            this.ability = sfa;
            aType = "suppressionField";
        } else if (ability instanceof RegenAbility a) {
            RegenAbility ra = new RegenAbility();
            ra.amount = a.amount;
            ra.percentAmount = a.percentAmount;
            this.ability = ra;
            aType = "regen";
        } else if (ability instanceof ForceFieldAbility a) {
            this.ability = new ForceFieldAbility(a.radius, a.regen, a.max, a.cooldown, a.sides, a.rotation);
            this.ability.data = a.data;
            aType = "forceField";
        } else if (ability instanceof ShieldArcAbility a) {
            ShieldArcAbility saa = new ShieldArcAbility();
            saa.radius = a.radius;
            saa.regen = a.regen;
            saa.max = a.max;
            saa.cooldown = a.cooldown;
            saa.angle = a.angle;
            saa.angleOffset = a.angleOffset;
            saa.whenShooting = a.whenShooting;
            saa.width = a.width;
            saa.drawArc = a.drawArc;
            this.ability = saa;
            aType = "shieldArc";
        } else if (ability instanceof ArmorPlateAbility a) {
            ArmorPlateAbility aa = new ArmorPlateAbility();
            aa.healthMultiplier = a.healthMultiplier;
            aa.color = a.color;
            aa.z = a.z;
            aType = "armorPlate";
        } else if (ability instanceof MoveLightningAbility a) {
            MoveLightningAbility mla = new MoveLightningAbility(a.damage, a.length, a.chance, a.y, a.minSpeed, a.maxSpeed, a.color);
            mla.x = a.x;
            mla.bullet = a.bullet;
            mla.bulletAngle = a.bulletAngle;
            mla.bulletSpread = a.bulletSpread;
            mla.alternate = a.alternate;
            mla.shootEffect = a.shootEffect;
            mla.parentizeEffects = a.parentizeEffects;
            mla.shootSound = a.shootSound;
            this.ability = mla;
            aType = "moveLightning";
            updateDialog(mla.bullet != null);
        } else {
            RepairFieldAbility rfa;
            if (ability instanceof RepairFieldAbility a) {
                rfa = new RepairFieldAbility(a.amount, a.reload, a.range);
                rfa.activeEffect = a.activeEffect;
                rfa.healEffect = a.healEffect;
                rfa.parentizeEffects = a.parentizeEffects;
            } else {
                rfa = new RepairFieldAbility(0, 500, 0);
            }
            this.ability = rfa;
            aType = "repairField";
        }
    }

    public void updateDialog(boolean add) {
        if (add) {
            MoveLightningAbility a = (MoveLightningAbility) ability;
            if (!(a.bullet instanceof LimitBulletType)) {
                a.bullet = new LimitBulletType();
            }
            dialog = new BulletDialog(() -> a.bullet, f -> bulletHeavy = f, b -> a.bullet = b, "", () -> 1);
            dialog.shown(() -> freeSize -= this.heavy);
            dialog.hidden(() -> freeSize += this.heavy);
        } else {
            dialog = null;
        }
    }

    public float findVal(String type) {
        switch (type) {
            case "abilityPower" -> {
                switch (aType) {
                    case "repairField" -> {
                        RepairFieldAbility a = (RepairFieldAbility) ability;
                        return 60 / a.reload;
                    }
                    case "suppressionField", "regen", "armorPlate" -> {
                        return 1.5f;
                    }
                    case "forceField" -> {
                        ForceFieldAbility a = (ForceFieldAbility) ability;
                        return 60 / a.cooldown;
                    }
                    case "shieldArc" -> {
                        ShieldArcAbility a = (ShieldArcAbility) ability;
                        return 60 / a.cooldown;
                    }
                    case "moveLightning" -> {
                        return 2f;
                    }
                }
            }
            case "abilityBoost" -> {
                switch (aType) {
                    case "repairField" -> {
                        RepairFieldAbility a = (RepairFieldAbility) ability;
                        return a.range / 4;
                    }
                    case "suppressionField" -> {
                        SuppressionFieldAbility a = (SuppressionFieldAbility) ability;
                        return a.range / 4;
                    }
                    case "regen", "armorPlate" -> {
                        return 15;
                    }
                    case "forceField" -> {
                        ForceFieldAbility a = (ForceFieldAbility) ability;
                        return a.radius / 4;
                    }
                    case "shieldArc" -> {
                        ShieldArcAbility a = (ShieldArcAbility) ability;
                        return a.radius / 4 + a.width / 4;
                    }
                    case "moveLightning" -> {
                        MoveLightningAbility a = (MoveLightningAbility) ability;
                        return a.length / 4f;
                    }
                }
            }
            case "abilityStatus" -> {
                switch (aType) {
                    case "repairField" -> {
                        RepairFieldAbility a = (RepairFieldAbility) ability;
                        return 120 * a.amount;
                    }
                    case "suppressionField" -> {
                        SuppressionFieldAbility a = (SuppressionFieldAbility) ability;
                        return a.applyParticleChance;
                    }
                    case "regen" -> {
                        RegenAbility a = (RegenAbility) ability;
                        return a.percentAmount * 100 + 120 * a.amount;
                    }
                    case "forceField" -> {
                        ForceFieldAbility a = (ForceFieldAbility) ability;
                        return a.max + 120 * a.regen;
                    }
                    case "shieldArc" -> {
                        ShieldArcAbility a = (ShieldArcAbility) ability;
                        return a.max + 120 * a.regen;
                    }
                    case "armorPlate" -> {
                        ArmorPlateAbility a = (ArmorPlateAbility) ability;
                        return 100 * a.healthMultiplier;
                    }
                    case "moveLightning" -> {
                        MoveLightningAbility a = (MoveLightningAbility) ability;
                        return a.damage * 60;
                    }
                }
            }
        }
        return 0;
    }

    public void updateHeavy() {
        heavy = 0.5f;
        heavy += ProjectUtils.getHeavy("abilityPower", findVal("abilityPower"));
        heavy += ProjectUtils.getHeavy("abilityBoost", findVal("abilityBoost"));
        heavy += ProjectUtils.getHeavy("abilityStatus", findVal("abilityStatus"));
        bulletHeavy = dialog == null ? 0 : dialog.heavyOut() * 30;
    }

    @Override
    public Table get() {
        return effect;
    }

    @Override
    public void set(Table table) {
        effect = table;
    }
}