package Floor.FType.FDialog;

import Floor.FEntities.FBulletType.LimitBulletType;
import arc.Core;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;

import static Floor.FType.FDialog.DialogUtils.*;

public class BulletDialog extends BaseDialog implements TableGetter {
    protected int boost = 1;
    protected WeaponDialog parentW;
    protected BulletDialog parentB;
    protected static final Seq<String> types = new Seq<>(new String[]{
            "bullet", "laser", "lightning", "continuousF", "continuousL", "point", "rail"
    });
    protected String newType = "bullet";
    //global
    protected LimitBulletType bullet = new LimitBulletType();
    protected LimitBulletType FBullet;
    protected float bulletHeavy = 0;
    protected float heavy = 0.5f;
    protected Table typeOn;
    protected Table baseOn;
    protected Table effectOn;

    protected static String dia = "bullet";
    protected Runnable re = () -> {
        rebuildBase();
        rebuildType();
    };
    protected StrBool levUser = str -> ProjectsLocated.couldUse(str, findVal(str));
    protected BoolGetter hevUser = () -> boost * heavy + bullet.fragBullets * bulletHeavy <= ProjectsLocated.freeSize;

    public BulletDialog(BaseDialog parent, String title) {
        super(title);
        shown(this::loadBase);
        this.parentW = null;
        this.parentB = null;
        if (parent instanceof WeaponDialog wd) {
            this.parentW = wd;
            (wd.bullet == null ? new LimitBulletType() : wd.bullet).copyTo(this.bullet);
            updateHeavy();
            this.bulletHeavy = parentW.bulletHeavy - this.heavy;
            newType = bullet.type;
            boost = wd.weapon.shoot.shots;
        } else if (parent instanceof BulletDialog bd) {
            this.parentB = bd;
            (bd.FBullet == null ? new LimitBulletType() : bd.FBullet).copyTo(this.bullet);
            updateHeavy();
            this.bulletHeavy = parentB.bulletHeavy - this.heavy;
            newType = bullet.type;
            boost = bd.bullet.fragBullets * bd.boost;
        }

        buttons.button("@back", Icon.left, () -> {
            hide();
            if (parentB != null) {
                parentB.bulletHeavy = this.heavy * boost + this.bulletHeavy;
            } else if (parentW != null) {
                parentW.bulletHeavy = this.heavy * boost + this.bulletHeavy;
            }
        }).size(210f, 64f);
        buttons.button(Core.bundle.get("@apply"), Icon.right, () -> {
            if (ProjectsLocated.getHeavy("percent", findVal("percent")) > 0) {
                bullet.havePercent = true;
            }
            if (ProjectsLocated.getHeavy("emp", findVal("emp")) > 0) {
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
            if (parentB != null) {
                if (parentB.FBullet == null) {
                    parentB.FBullet = new LimitBulletType();
                }
                bullet.copyTo(parentB.FBullet);
                parentB.bulletHeavy = this.heavy * boost + this.bulletHeavy;
            } else if (parentW != null) {
                if (parentW.bullet == null) {
                    parentW.bullet = new LimitBulletType();
                }
                bullet.copyTo(parentW.bullet);
                parentW.bulletHeavy = this.heavy * boost + this.bulletHeavy;
            }
            hide();
        }).size(210f, 64f);
        buttons.button("@toZero", Icon.defense, () -> {
            bullet.setZero();
            updateHeavy();
            rebuildType();
            rebuildBase();
        }).size(210f, 64f);
    }

    public void loadBase() {
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
                    (heavy + bulletHeavy) + " / " +
                    ProjectsLocated.freeSize).size(25).left().width(100).pad(5);
        }).pad(2).growX().row();

        table.table(t -> typeOn = t).pad(2).left().growX();
        rebuildType();
        table.row();
        table.table(t -> baseOn = t).pad(2).left().growX();
        rebuildBase();

        table.table(this::rebuildEffect).pad(2).left().growX();
    }

    public void rebuildType() {
        typeOn.clear();
        typeOn.background(Tex.buttonDown);
        switch (newType) {
            case "bullet" -> {
                createLevDialog(typeOn, dia, "range", "bulletBase", bullet.range,
                        f -> bullet.range = f, re, this::updateHeavy, levUser, hevUser);
                createNumberDialog(typeOn, dia, "lifetime", bullet.lifetime, f -> {
                    bullet.lifetime = f;
                    bullet.speed = bullet.range / (f == 0 ? 0.0001f : f);
                }, re);
                createNumberDialogWithLimit(typeOn, dia, "bulletWide", bullet.width,
                        45, 0, f -> bullet.width = f, re);
                createNumberDialogWithLimit(typeOn, dia, "bulletHeight", bullet.height,
                        45, 0, f -> bullet.height = f, re);
                typeOn.row();
            }
            case "laser" -> {
                createLevDialog(typeOn, dia, "laserLength", "bulletBase", bullet.laserCLength,
                        f -> bullet.laserCLength = f, re, this::updateHeavy, levUser, hevUser);
                createNumberDialogWithLimit(typeOn, dia, "laserWidth", bullet.width,
                        45, 0.001f, f -> bullet.width = f, re);
            }
            case "lightning" -> {
                createLevDialog(typeOn, dia, "bulletLightningLength", "bulletBase", bullet.bulletLightningLength,
                        f -> bullet.bulletLightningLength = (int) (f + 0), re, this::updateHeavy, levUser, hevUser);
                createLevDialog(typeOn, dia, "bulletLightningLengthRand", "bulletBase", bullet.bulletLightningLengthRand,
                        f -> bullet.bulletLightningLengthRand = (int) (f + 0), re, this::updateHeavy, levUser, hevUser);
            }
            case "continuousF" -> {
                createLevDialog(typeOn, dia, "flareLength", "bulletBase", bullet.laserCLength,
                        f -> bullet.laserCLength = f, re, this::updateHeavy, levUser, hevUser);
                createLevDialog(typeOn, dia, "lifetime", "bulletBase", bullet.lifetime,
                        f -> bullet.lifetime = f, re, this::updateHeavy, levUser, hevUser);
                createNumberDialogWithLimit(typeOn, dia, "flareWidth", bullet.flareWidth,
                        30, 0, f -> bullet.flareWidth = f, re);
            }
            case "continuousL" -> {
                createLevDialog(typeOn, dia, "laserCLength", "bulletBase", bullet.flareLength,
                        f -> bullet.flareLength = f, re, this::updateHeavy, levUser, hevUser);
                createLevDialog(typeOn, dia, "lifetime", "bulletBase", bullet.lifetime,
                        f -> bullet.lifetime = f, re, this::updateHeavy, levUser, hevUser);
                createNumberDialogWithLimit(typeOn, dia, "fadeTime", bullet.fadeTime,
                        36, 12, f -> bullet.fadeTime = f, re);
            }
            case "point" -> {
                createLevDialog(typeOn, dia, "range", "bulletBase", bullet.range,
                        f -> bullet.range = f, re, this::updateHeavy, levUser, hevUser);
                createNumberDialogWithLimit(typeOn, dia, "trailSpacing", bullet.trailSpacing,
                        180, 2, f -> bullet.trailSpacing = f, re);
            }
            case "rail" -> {
                createLevDialog(typeOn, dia, "railLength", "bulletBase", bullet.railLength,
                        f -> bullet.railLength = f, re, this::updateHeavy, levUser, hevUser);
                createNumberDialogWithLimit(typeOn, dia, "pointEffectSpace", bullet.pointEffectSpace,
                        180, 10, f -> bullet.pointEffectSpace = f, re);
            }
        }
    }

    public void rebuildBase() {
        baseOn.clear();
        createTypeLine(baseOn, dia, "bulletBase", findVal("bulletBase"));

        baseOn.table(s -> {
            s.background(Tex.buttonDown);
            createLevDialog(s, dia, "damage", "bulletBase", bullet.damage,
                    f -> bullet.damage = f, re, this::updateHeavy, levUser, hevUser);
        }).growX();

        createTypeLine(baseOn, dia, "frags", findVal("frags"));

        baseOn.table(s -> {
            s.background(Tex.buttonDown);
            createNumberDialogWithLimit(s, dia, "fragAngle", bullet.fragAngle,
                    12, -12, f -> bullet.fragAngle = f, re);
            createLevDialog(s, dia, "frags", "fragBullets", bullet.fragBullets,
                    f -> bullet.fragBullets = (int) (f + 0), re, this::updateHeavy, levUser, hevUser);
            s.row();
            s.label(() -> Core.bundle.get("writeFrag") + "->").width(150);
            s.button(Icon.pencilSmall, () -> {
                ProjectsLocated.freeSize -= this.heavy * boost;
                BulletDialog bd = new BulletDialog(this, "");
                bd.hidden(() -> ProjectsLocated.freeSize += this.heavy * boost);
                bd.show();
            }).pad(15).width(24);
            s.row();
        }).growX();

        createTypeLine(baseOn, dia, "lightning", findVal("lightning"));

        baseOn.table(s -> {
            s.background(Tex.buttonDown);
            createNumberDialogWithLimit(s, dia, "lightningAngle", bullet.lightningAngle,
                    360, 0, f -> bullet.lightningAngle = f, re);
            createNumberDialogWithLimit(s, dia, "lightningAngleRand", bullet.lightningAngleRand,
                    360, 0, f -> bullet.lightningAngle = f, re);
            createLevDialog(s, dia, "lightningLength", "lightning", bullet.lightningLength,
                    f -> bullet.lightningLength = (int) (f + 0), re, this::updateHeavy, levUser, hevUser);
            s.row();
            createLevDialog(s, dia, "lightningLengthRand", "lightning", bullet.lightningLengthRand,
                    f -> bullet.lightningLengthRand = (int) (f + 0), re, this::updateHeavy, levUser, hevUser);
            createLevDialog(s, dia, "lightningDamage", "lightning", bullet.lightningDamage,
                    f -> bullet.lightningDamage = f + 0, re, this::updateHeavy, levUser, hevUser);
            createLevDialog(s, dia, "lightnings", "lightning", bullet.lightning,
                    f -> bullet.lightning = (int) (f + 0), re, this::updateHeavy, levUser, hevUser);
        }).growX();

        createTypeLine(baseOn, dia, "percent", findVal("percent"));

        baseOn.table(p -> createLevDialog(p, dia, "percent", "percent", bullet.percent,
                f -> bullet.percent = f, re, this::updateHeavy, levUser, hevUser));

        baseOn.row();
        baseOn.label(() -> Core.bundle.get("dialog.bullet.effects") + ": ");
        baseOn.row();

        baseOn.table(this::rebuildEffect).grow();
    }

    public void rebuildEffect(Table on) {
        on.clear();
        on.table(l -> createEffectLine(l, this, dia, "shootEffect", bullet.shootEffect)).growX();
        on.table(l -> createEffectLine(l, this, dia, "despawnEffect", bullet.despawnEffect)).growX();
        on.row();
        on.table(l -> createEffectLine(l, this, dia, "hitEffect", bullet.hitEffect)).growX();
        on.table(l -> createEffectLine(l, this, dia, "chargeEffect", bullet.chargeEffect)).growX();
        on.row();
        on.table(l -> createEffectLine(l, this, dia, "smokeEffect", bullet.smokeEffect)).growX();
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
        heavy += ProjectsLocated.getHeavy("bulletBase", findVal("bulletBase"));
        heavy += ProjectsLocated.getHeavy("emp", findVal("emp"));
        heavy += ProjectsLocated.getHeavy("splash", findVal("splash"));
        heavy += ProjectsLocated.getHeavy("lightning", findVal("lightning"));
        heavy += ProjectsLocated.getHeavy("percent", findVal("percent"));
        heavy += ProjectsLocated.getHeavy("frags", findVal("frags"));
        heavy += ProjectsLocated.getHeavy("knock", findVal("knock"));
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