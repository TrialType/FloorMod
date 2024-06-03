package Floor.FType.FDialog;

import Floor.FEntities.FBulletType.LimitBulletType;
import Floor.Floor;
import arc.Core;
import arc.func.Cons;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.FlakBulletType;
import mindustry.entities.effect.MultiEffect;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;

import static Floor.FType.FDialog.ProjectDialogUtils.*;
import static mindustry.Vars.ui;

public class BulletDialog extends BaseDialog implements EffectTableGetter {
    protected int boost;
    protected Cons<BulletType> apply;
    protected Cons<Float> heavyApply;
    protected String newType = "bullet";
    //global
    protected LimitBulletType bullet;
    protected float bulletHeavy = 0;
    protected float heavy = 0.5f;
    protected Table typeOn;
    protected Table baseOn;
    protected Table effectOn;
    protected static final Seq<String> types = new Seq<>(new String[]{
            "bullet", "laser", "lightning", "continuousF", "continuousL", "point", "rail"
    });

    protected static String dia = "bullet";
    protected Runnable reb = this::rebuildBase;
    protected Runnable ret = this::rebuildType;
    protected StrBool levUser = str -> couldUse(str, findVal(str));
    protected BoolGetter hevUser = () -> boost * (heavy + bullet.fragBullets * bulletHeavy) <= freeSize;

    public BulletDialog(BulletType def, Cons<Float> heavyApply, Cons<BulletType> apply, String title, int boost, float heavy) {
        super(title);
        shown(this::loadBase);
        this.apply = apply;
        this.boost = boost;
        this.heavyApply = heavyApply;
        this.bullet = def instanceof LimitBulletType l ? l : new LimitBulletType();
        updateHeavy();
        bulletHeavy = (heavy - this.heavy) / bullet.fragBullets;

        buttons.button("@back", Icon.left, () -> {
            apply.get(bullet);
            heavyApply.get(heavy + bullet.fragBullets * bulletHeavy);
            hide();
        }).size(210f, 64f);
        buttons.button(Core.bundle.get("@apply"), Icon.right, () -> {
            if (boost * heavy + bullet.fragBullets * bulletHeavy <= freeSize) {
                if (getHeavy("percent", findVal("percent")) > 0) {
                    bullet.havePercent = true;
                }
                if (getHeavy("emp", findVal("emp")) > 0) {
                    bullet.haveEmp = true;
                }
                if (newType.equals("lightning")) {
                    if (bullet.havePercent) {
                        bullet.lightningType = new LimitBulletType() {{
                            havePercent = true;
                            percent = bullet.percent;
                            lightningDamage = bullet.lightningDamage;
                        }};
                    }
                }
                apply.get(bullet);
                heavyApply.get(heavy + bullet.fragBullets * bulletHeavy);
                hide();
            } else {
                ui.showInfo(Core.bundle.get("@tooHeavy"));
            }
        }).size(210f, 64f);
        buttons.button("@toZero", Icon.defense, () -> {
            bullet.setZero();
            updateHeavy();
            rebuildType();
            rebuildBase();
        }).size(210f, 64f);
    }

    public void loadBase() {
        cont.clear();
        cont.pane(this::Front);
    }

    public void Front(Table table) {
        table.table(t -> {
            t.background(Tex.scroll);
            t.add(Core.bundle.get("dialog.bullet-type") + " : ").size(25).left().width(100);
            t.label(() -> Core.bundle.format("dialog." + newType)).size(25).left().width(100).pad(1);
            t.button(b -> {
                b.image(Icon.down).size(25);

                b.clicked(() -> createSelectDialog(b, (tb, hide) -> {
                    tb.top();
                    for (String name : types) {
                        tb.button(Core.bundle.get("dialog." + name), () -> {
                            newType = name;
                            bullet.type = name;
                            hide.run();
                            updateHeavy();

                            rebuildType();

                        }).growX().width(200);
                        tb.row();
                    }
                }));
            }, Styles.logici, () -> {
            }).size(25).left().pad(5);
            t.row();
            t.label(() -> Core.bundle.get("@heavyUse") + ": " +
                    (heavy + bulletHeavy) + " / " + freeSize).size(25).left().width(100).pad(5);
        }).pad(2).growX().row();

        table.table(t -> typeOn = t).pad(2).left().growX();
        rebuildType();
        table.row();
        table.table(t -> baseOn = t).pad(2).left().growX();
        rebuildBase();

        table.table(this::rebuildEffect).pad(2).left().growX();
    }

    public void rebuildBase() {
        baseOn.clear();
        createTypeLine(baseOn, dia, "bulletBase", findVal("bulletBase"));

        baseOn.table(s -> {
            s.background(Tex.buttonDown);
            createLevDialog(s, dia, "damage", "bulletBase", bullet.damage,
                    f -> bullet.damage = f, reb, this::updateHeavy, levUser, hevUser);
        }).growX();

        createTypeLine(baseOn, dia, "frags", findVal("frags"));

        baseOn.table(s -> {
            s.background(Tex.buttonDown);
            createNumberDialogWithLimit(s, dia, "fragAngle", bullet.fragAngle,
                    12, -12, f -> bullet.fragAngle = f, reb);
            createLevDialog(s, dia, "frags", "fragBullets", bullet.fragBullets,
                    f -> bullet.fragBullets = (int) (f + 0), reb, this::updateHeavy, levUser, hevUser);
            s.row();
            s.label(() -> Core.bundle.get("writeFrag") + "->").width(150);
            s.button(Icon.pencilSmall, () -> {
                freeSize -= this.heavy * boost;
                BulletDialog bd = new BulletDialog(bullet.fragBullet, f -> bulletHeavy = f,
                        b -> b.fragBullet = b, "", bullet.fragBullets, bulletHeavy);
                bd.hidden(() -> freeSize += this.heavy * boost);
                bd.show();
            }).pad(15).width(24);
            s.row();
        }).growX();

        createTypeLine(baseOn, dia, "lightning", findVal("lightning"));

        baseOn.table(s -> {
            s.background(Tex.buttonDown);
            createNumberDialogWithLimit(s, dia, "lightningAngle", bullet.lightningAngle,
                    360, 0, f -> bullet.lightningAngle = f, reb);
            createNumberDialogWithLimit(s, dia, "lightningAngleRand", bullet.lightningAngleRand,
                    360, 0, f -> bullet.lightningAngle = f, reb);
            createLevDialog(s, dia, "lightningLength", "lightning", bullet.lightningLength,
                    f -> bullet.lightningLength = (int) (f + 0), reb, this::updateHeavy, levUser, hevUser);
            s.row();
            createLevDialog(s, dia, "lightningLengthRand", "lightning", bullet.lightningLengthRand,
                    f -> bullet.lightningLengthRand = (int) (f + 0), reb, this::updateHeavy, levUser, hevUser);
            createLevDialog(s, dia, "lightningDamage", "lightning", bullet.lightningDamage,
                    f -> bullet.lightningDamage = f + 0, reb, this::updateHeavy, levUser, hevUser);
            createLevDialog(s, dia, "lightnings", "lightning", bullet.lightning,
                    f -> bullet.lightning = (int) (f + 0), reb, this::updateHeavy, levUser, hevUser);
        }).growX();

        createTypeLine(baseOn, dia, "percent", findVal("percent"));

        baseOn.table(p -> createLevDialog(p, dia, "percent", "percent", bullet.percent,
                f -> bullet.percent = f, reb, this::updateHeavy, levUser, hevUser));

        baseOn.row();
        baseOn.label(() -> Core.bundle.get("dialog.bullet.effects") + ": ");
    }

    public void rebuildType() {
        typeOn.clear();
        typeOn.background(Tex.buttonDown);
        switch (newType) {
            case "bullet" -> {
                createLevDialog(typeOn, dia, "range", "bulletBase", bullet.range,
                        f -> bullet.range = f, ret, this::updateHeavy, levUser, hevUser);
                createNumberDialog(typeOn, dia, "lifetime", bullet.lifetime, f -> {
                    bullet.lifetime = f;
                    bullet.speed = bullet.range / (f == 0 ? 0.0001f : f);
                }, ret);
                createNumberDialogWithLimit(typeOn, dia, "bulletWide", bullet.width,
                        45, 0, f -> bullet.width = f, ret);
                createNumberDialogWithLimit(typeOn, dia, "bulletHeight", bullet.height,
                        45, 0, f -> bullet.height = f, ret);
                typeOn.row();
            }
            case "laser" -> {
                createLevDialog(typeOn, dia, "laserLength", "bulletBase", bullet.laserCLength,
                        f -> bullet.laserCLength = f, ret, this::updateHeavy, levUser, hevUser);
                createNumberDialogWithLimit(typeOn, dia, "laserWidth", bullet.width,
                        45, 0.001f, f -> bullet.width = f, ret);
            }
            case "lightning" -> {
                createLevDialog(typeOn, dia, "bulletLightningLength", "bulletBase", bullet.bulletLightningLength,
                        f -> bullet.bulletLightningLength = (int) (f + 0), ret, this::updateHeavy, levUser, hevUser);
                createLevDialog(typeOn, dia, "bulletLightningLengthRand", "bulletBase", bullet.bulletLightningLengthRand,
                        f -> bullet.bulletLightningLengthRand = (int) (f + 0), ret, this::updateHeavy, levUser, hevUser);
            }
            case "continuousF" -> {
                createLevDialog(typeOn, dia, "flareLength", "bulletBase", bullet.laserCLength,
                        f -> bullet.laserCLength = f, ret, this::updateHeavy, levUser, hevUser);
                createLevDialog(typeOn, dia, "lifetime", "bulletBase", bullet.lifetime,
                        f -> bullet.lifetime = f, ret, this::updateHeavy, levUser, hevUser);
                createNumberDialogWithLimit(typeOn, dia, "flareWidth", bullet.flareWidth,
                        30, 0, f -> bullet.flareWidth = f, ret);
            }
            case "continuousL" -> {
                createLevDialog(typeOn, dia, "laserCLength", "bulletBase", bullet.flareLength,
                        f -> bullet.flareLength = f, ret, this::updateHeavy, levUser, hevUser);
                createLevDialog(typeOn, dia, "lifetime", "bulletBase", bullet.lifetime,
                        f -> bullet.lifetime = f, ret, this::updateHeavy, levUser, hevUser);
                createNumberDialogWithLimit(typeOn, dia, "fadeTime", bullet.fadeTime,
                        36, 12, f -> bullet.fadeTime = f, ret);
            }
            case "point" -> {
                createLevDialog(typeOn, dia, "range", "bulletBase", bullet.range,
                        f -> bullet.range = f, ret, this::updateHeavy, levUser, hevUser);
                createNumberDialogWithLimit(typeOn, dia, "trailSpacing", bullet.trailSpacing,
                        180, 2, f -> bullet.trailSpacing = f, ret);
            }
            case "rail" -> {
                createLevDialog(typeOn, dia, "railLength", "bulletBase", bullet.railLength,
                        f -> bullet.railLength = f, ret, this::updateHeavy, levUser, hevUser);
                createNumberDialogWithLimit(typeOn, dia, "pointEffectSpace", bullet.pointEffectSpace,
                        180, 10, f -> bullet.pointEffectSpace = f, ret);
            }
        }
    }

    public void rebuildEffect(Table on) {
        if (!(bullet.shootEffect instanceof MultiEffect)) {
            bullet.shootEffect = new MultiEffect();
        }
        if (!(bullet.despawnEffect instanceof MultiEffect)) {
            bullet.despawnEffect = new MultiEffect();
        }
        if (!(bullet.hitEffect instanceof MultiEffect)) {
            bullet.hitEffect = new MultiEffect();
        }
        if (!(bullet.chargeEffect instanceof MultiEffect)) {
            bullet.chargeEffect = new MultiEffect();
        }
        if (!(bullet.smokeEffect instanceof MultiEffect)) {
            bullet.smokeEffect = new MultiEffect();
        }
        on.clear();
        on.table(l -> createEffectList(l, this, dia, "shootEffect", bullet.shootEffect)).growX();
        on.table(l -> createEffectList(l, this, dia, "despawnEffect", bullet.despawnEffect)).growX();
        on.row();
        on.table(l -> createEffectList(l, this, dia, "hitEffect", bullet.hitEffect)).growX();
        on.table(l -> createEffectList(l, this, dia, "chargeEffect", bullet.chargeEffect)).growX();
        on.row();
        on.table(l -> createEffectList(l, this, dia, "smokeEffect", bullet.smokeEffect)).growX();
    }

    public float findVal(String name) {
        return switch (name) {
            case "bulletBase" -> switch (newType) {
                case "point" -> bullet.damage / 1.4f + bullet.range / 8;
                case "bullet" -> bullet.damage + bullet.range / 8 + bullet.pierceCap;
                case "laser" -> bullet.damage + bullet.laserLength / 8 + bullet.pierceCap;
                case "continuousF" ->
                        bullet.damage * 1.2f * bullet.lifetime * (40 / bullet.damageInterval) + bullet.flareLength / 8;
                case "continuousL" ->
                        bullet.damage * 1.2f * bullet.lifetime * (40 / bullet.damageInterval) + bullet.laserCLength / 8;
                case "lightning" ->
                        bullet.damage * 1.2f + (bullet.bulletLightningLength + bullet.bulletLightningLengthRand / 1.2f) / 8;
                default -> bullet.damage * 1.2f + bullet.railLength / 8;
            };
            case "splash" -> switch (newType) {
                case "point", "bullet", "laser" -> bullet.splashDamage * bullet.splashDamageRadius / 4;
                case "continuousF", "continuousL" ->
                        bullet.lifetime * bullet.splashDamage * bullet.splashDamageRadius * 1.2f * (40 / bullet.damageInterval) / 4;
                case "lightning", "rail" -> bullet.splashDamage * bullet.splashDamageRadius * 1.2f / 4;
                default -> 100000;
            };
            case "lightning" -> switch (newType) {
                case "point", "bullet", "laser" ->
                        bullet.lightning * bullet.lightningDamage * bullet.lightningLength * bullet.lightningLengthRand / 4;
                case "continuousF", "continuousL" ->
                        bullet.lightning * bullet.lightningDamage * bullet.lightningLength *
                                bullet.lightningLengthRand * 1.2f * bullet.lifetime * (40 / bullet.damageInterval) / 4;
                case "lightning", "rail" ->
                        bullet.lightning * bullet.lightningDamage * bullet.lightningLength * bullet.lightningLengthRand * 1.2f / 4;
                default -> 100000;
            };
            case "percent" -> switch (newType) {
                case "point", "bullet", "laser" -> bullet.percent;
                case "continuousF", "continuousL" ->
                        bullet.percent * 1.2f * bullet.lifetime * (40 / bullet.damageInterval);
                case "lightning", "rail" -> bullet.percent * 1.2f;
                default -> 100000;
            };
            case "frags" -> switch (newType) {
                case "point", "bullet", "laser" -> bullet.fragBullets;
                case "continuousF", "continuousL" ->
                        bullet.fragBullets * 1.2f * bullet.lifetime * (40 / bullet.damageInterval);
                case "lightning", "rail" -> bullet.fragBullets * 1.2f;
                default -> 100000;
            };
            case "knock" -> switch (newType) {
                case "point", "bullet", "laser" -> bullet.knockback;
                case "continuousF", "continuousL" ->
                        bullet.knockback * 1.2f * bullet.lifetime * (40 / bullet.damageInterval);
                case "lightning", "rail" -> bullet.knockback * 1.2f;
                default -> 100000;
            };
            case "emp" -> switch (newType) {
                case "point", "bullet", "laser" ->
                        bullet.empDamage * bullet.radius * bullet.powerSclDecrease * bullet.width * bullet.height / 4;
                case "continuousF", "continuousL" ->
                        bullet.empDamage * bullet.radius * bullet.powerSclDecrease * bullet.width *
                                bullet.height * 1.2f * bullet.lifetime * (40 / bullet.damageInterval) / 4;
                case "lightning", "rail" ->
                        bullet.empDamage * bullet.radius * bullet.powerSclDecrease * bullet.width * bullet.height * 1.2f / 4;
                default -> 100000;
            };
            case "none" -> 0;
            default -> 100000;
        };
    }

    public void updateHeavy() {
        heavy = 0.5f;
        heavy += getHeavy("bulletBase", findVal("bulletBase"));
        heavy += getHeavy("emp", findVal("emp"));
        heavy += getHeavy("splash", findVal("splash"));
        heavy += getHeavy("lightning", findVal("lightning"));
        heavy += getHeavy("percent", findVal("percent"));
        heavy += getHeavy("frags", findVal("frags"));
        heavy += getHeavy("knock", findVal("knock"));
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