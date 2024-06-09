package Floor.FType.FDialog;

import Floor.FEntities.FBulletType.LimitBulletType;
import arc.func.Boolp;
import arc.func.Cons;
import arc.func.Prov;
import arc.scene.ui.layout.Table;
import mindustry.entities.abilities.*;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.ui.dialogs.BaseDialog;

import static Floor.FType.FDialog.ProjectUtils.*;

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

    public AbilityDialog(String title, Prov<Ability> def, Cons<Ability> apply) {
        super(title);
        setAbility(def.get());
        updateHeavy();
        shown(this::rebuild);

        buttons.button("@back", Icon.left, () -> {
            apply.get(def.get());
            hide();
        });
        buttons.button("@apply", Icon.right, () -> {
            apply.get(ability);
            hide();
        });
    }

    public void rebuild() {
        cont.clear();

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

            }
            case "regen" -> {
                RegenAbility a = (RegenAbility) ability;

            }
            case "forceField" -> {
                ForceFieldAbility a = (ForceFieldAbility) ability;

            }
            case "shieldArc" -> {
                ShieldArcAbility a = (ShieldArcAbility) ability;

            }
            case "armorPlate" -> {
                ArmorPlateAbility a = (ArmorPlateAbility) ability;

            }
            case "moveLightning" -> {
                MoveLightningAbility a = (MoveLightningAbility) ability;

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
        updateHeavy();
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
            bulletHeavy = 0;
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
                        return 50;
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
