package Floor.FType.FDialog;

import Floor.FEntities.FBulletType.LimitBulletType;
import arc.Core;
import arc.func.Cons;
import arc.func.Cons2;
import arc.graphics.Color;
import arc.scene.actions.Actions;
import arc.scene.ui.Button;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Align;
import arc.util.Strings;
import arc.util.Tmp;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;

import static mindustry.Vars.ui;

public class BulletDialog extends BaseDialog {
    public WeaponDialog parentW;
    public BulletDialog parentB;
    public final Seq<String> types = new Seq<>(new String[]{
            "bullet", "laser", "lightning", "continuousF", "continuousL", "point", "rail"
    });
    public String newType = "bullet";
    //global
    public LimitBulletType bullet = new LimitBulletType();
    public LimitBulletType FBullet;
    public float bulletHeavy = 0;
    public float fromHeavy = 0;
    public float heavy = 0.5f;
    public Table typeOn;
    public Table baseOn;

    public BulletDialog(BaseDialog parent, String title) {
        super(title);
        shown(this::loadBase);
        this.parentW = null;
        this.parentB = null;
        if (parent instanceof WeaponDialog wd) {
            this.parentW = wd;
            (wd.bullet == null ? new LimitBulletType() : wd.bullet).copyTo(this.bullet);
            updateHeavy();
            this.fromHeavy = parentW.heavy;
            this.bulletHeavy = parentW.bulletHeavy - this.heavy;
            newType = bullet.type;
        } else if (parent instanceof BulletDialog bd) {
            this.parentB = bd;
            (bd.FBullet == null ? new LimitBulletType() : bd.FBullet).copyTo(this.bullet);
            updateHeavy();
            this.fromHeavy = parentB.fromHeavy + parentB.heavy;
            this.bulletHeavy = parentB.bulletHeavy - this.heavy;
            newType = bullet.type;
        }

        buttons.button("@back", Icon.left, () -> {
            hide();
            if (parentB != null) {
                parentB.bulletHeavy = this.heavy + this.bulletHeavy;
            } else if (parentW != null) {
                parentW.bulletHeavy = this.heavy + this.bulletHeavy;
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
                parentB.bulletHeavy = this.heavy + this.bulletHeavy;
            } else if (parentW != null) {
                if (parentW.bullet == null) {
                    parentW.bullet = new LimitBulletType();
                }
                bullet.copyTo(parentW.bullet);
                parentW.bulletHeavy = this.heavy + this.bulletHeavy;
            }
            hide();
        }).size(210f, 64f);
        buttons.button("@toZero", Icon.defense, () -> {
            bullet.setZero();
            rebuildType();
            rebuildBase();
            updateHeavy();
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
                    (heavy + fromHeavy + bulletHeavy) + " / " +
                    ProjectsLocated.freeSize).size(25).left().width(100).pad(5);
        }).pad(2).growX().row();

        table.table(t -> typeOn = t).pad(2).left().growX();
        rebuildType();
        table.row();
        table.table(t -> baseOn = t).pad(2).left().growX();
        rebuildBase();
    }

    public void rebuildType() {
        typeOn.clear();
        typeOn.background(Tex.buttonDown);
        switch (newType) {
            case "bullet" -> {
                createLevDialog("bulletSpeed", "pass", typeOn, bullet.speed,
                        f -> bullet.speed = f, f -> bullet.speed = f);
                createNumberDialog("bulletWide", typeOn, bullet.width, 0, 45,
                        f -> bullet.width = f);
                createNumberDialog("bulletHeight", typeOn, bullet.height, 0, 45,
                        f -> bullet.height = f);
                typeOn.row();
            }
            case "laser" -> {
                createLevDialog("laserLength", "pass", typeOn, bullet.laserCLength,
                        f -> bullet.laserCLength = f, f -> bullet.laserCLength = f);
                createNumberDialog("laserWidth", typeOn, bullet.width, 0.01f, 45,
                        f -> bullet.width = f);
            }
            case "lightning" -> {
                createLevDialog("bulletLightningLength", "pass", typeOn, bullet.bulletLightningLength,
                        f -> bullet.bulletLightningLength = (int) (f + 0), f -> bullet.bulletLightningLength = (int) (f + 0));
                createLevDialog("bulletLightningLengthRand", "pass", typeOn, bullet.bulletLightningLengthRand,
                        f -> bullet.bulletLightningLengthRand = (int) (f + 0), f -> bullet.bulletLightningLengthRand = (int) (f + 0));
            }
            case "continuousF" -> {
                createLevDialog("flareLength", "pass", typeOn, bullet.laserCLength,
                        f -> bullet.laserCLength = f, f -> bullet.laserCLength = f);
                createNumberDialog("flareWidth", typeOn, bullet.flareWidth, 0, 30,
                        f -> bullet.flareWidth = f);
            }
            case "continuousL" -> {
                createLevDialog("laserCLength", "pass", typeOn, bullet.flareLength,
                        f -> bullet.flareLength = f, f -> bullet.flareLength = f);
                createNumberDialog("fadeTime", typeOn, bullet.fadeTime, 12, 36,
                        f -> bullet.fadeTime = f);
            }
            case "point" -> {
                createLevDialog("bulletSpeed", "pass", typeOn, bullet.speed,
                        f -> bullet.speed = f, f -> bullet.speed = f);
                createNumberDialog("trailSpacing", typeOn, 10, 10, 180,
                        f -> bullet.trailSpacing = f);
            }
            case "rail" -> {
                createLevDialog("railLength", "pass", typeOn, bullet.railLength,
                        f -> bullet.railLength = f, f -> bullet.railLength = f);
                createNumberDialog("pointEffectSpace", typeOn, 10, 10, 180,
                        f -> bullet.pointEffectSpace = f);
            }
        }
    }

    public void rebuildBase() {
        baseOn.clear();
        createTypeLine(baseOn, "damage");

        baseOn.table(s -> {
            s.background(Tex.buttonDown);
            createLevDialog("damage", "damage", s, bullet.damage,
                    f -> bullet.damage = f, f -> bullet.damage = f);
            createLevDialog("lifetime", findTyp("lifetime"), s, bullet.lifetime,
                    f -> bullet.lifetime = f, f -> bullet.lifetime = f);
        }).growX();

        createTypeLine(baseOn, "frags");

        baseOn.table(s -> {
            s.background(Tex.buttonDown);
            createNumberDialog("fragAngle", s, bullet.fragAngle, -12, 12,
                    f -> bullet.fragAngle = f);
            createLevDialog("frags", "frags", s, bullet.fragBullets,
                    f -> bullet.fragBullets = (int) (f + 0), f -> bullet.fragBullets = (int) (f + 0));
        }).growX();

        createTypeLine(baseOn, "lightning");

        baseOn.table(s -> {
            s.background(Tex.buttonDown);
            createNumberDialog("lightningAngle", s, bullet.lightningAngle, 0, 360,
                    f -> bullet.lightningAngle = f);
            createNumberDialog("lightningAngleRand", s, bullet.lightningAngleRand, 0, 360,
                    f -> bullet.lightningAngle = f);
            createLevDialog("lightningLength", "lightning", s, bullet.lightningLength,
                    f -> bullet.lightningLength = (int) (f + 0), f -> bullet.lightningLength = (int) (f + 0));
            s.row();
            createLevDialog("lightningLengthRand", "lightning", s, bullet.lightningLengthRand,
                    f -> bullet.lightningLengthRand = (int) (f + 0), f -> bullet.lightningLengthRand = (int) (f + 0));
            createLevDialog("lightningDamage", "lightning", s, bullet.lightningDamage,
                    f -> bullet.lightningDamage = f + 0, f -> bullet.lightningDamage = f + 0);
            createLevDialog("lightnings", "lightning", s, bullet.lightning,
                    f -> bullet.lightning = (int) (f + 0), f -> bullet.lightning = (int) (f + 0));
        }).growX();
    }

    public void createTypeLine(Table t, String type) {
        t.row();
        t.table(table -> {
            table.background(Tex.scroll);
            table.label(() -> Core.bundle.get("dialog.bullet." + type)).left();
            table.label(() -> Core.bundle.get("@heavyUse") + ":  " + ProjectsLocated.getHeavy(type, findVal(type))).left().pad(5);
            table.label(() -> Core.bundle.get("@maxLevel") + ":  " + ProjectsLocated.maxLevel.get(type)).left().pad(5);
        });
        t.row();
    }

    public void createLevDialog(String name, String line, Table t, float value, Cons<Float> changer, Cons<Float> rollback) {
        t.table(type -> {
            type.add(Core.bundle.get("dialog.bullet." + name) + ":").pad(3).color(Color.red);
            type.label(() -> value + "").pad(3);
            type.button(Icon.pencil, Styles.flati, () -> ui.showTextInput(Core.bundle.get("dialog.bullet." + name), "", 15, value + "", true, str -> {
                if (Strings.canParsePositiveFloat(str)) {
                    float amount = Strings.parseFloat(str);
                    changer.get(amount);
                    float now = this.heavy;
                    updateHeavy();
                    if (ProjectsLocated.couldUse(line, findVal(line)) && heavy + bulletHeavy + fromHeavy <= ProjectsLocated.freeSize) {
                        rebuildBase();
                        rebuildType();
                    } else {
                        if (!ProjectsLocated.couldUse(line, findVal(line))) {
                            ui.showInfo(Core.bundle.get("@levelOutOfBounds"));
                        } else if (!(heavy + bulletHeavy + fromHeavy <= ProjectsLocated.freeSize)) {
                            ui.showInfo(Core.bundle.get("@tooHeavy"));
                        }
                        this.heavy = now;
                        rollback.get(value);
                    }
                    return;
                }
                ui.showInfo(Core.bundle.get("@inputError"));
            })).size(55);
        }).pad(10).fillX();
    }

    public void createNumberDialog(String name, Table t, float value, float min, float max, Cons<Float> changer) {
        t.table(base -> {
            base.add(Core.bundle.get("dialog.bullet." + name) + ":");
            base.label(() -> value + "").pad(3);
            base.button(Icon.pencil, Styles.flati, () -> ui.showTextInput(Core.bundle.get("dialog.bullet." + name), "", 15, value + "", true, str -> {
                if (Strings.canParsePositiveFloat(str)) {
                    float amount = Strings.parseFloat(str);
                    if ((amount >= min && amount <= max) || max < min) {
                        changer.get(amount);
                        rebuildBase();
                        rebuildType();
                        return;
                    }
                }
                ui.showInfo(Core.bundle.format("configure.invalid", min, max));
            })).size(55);
        }).pad(10).fillX();
    }

    public void createSelectDialog(Button b, Cons2<Table, Runnable> table) {
        Table ta = new Table() {
            @Override
            public float getPrefHeight() {
                return Math.min(super.getPrefHeight(), Core.graphics.getHeight());
            }

            @Override
            public float getPrefWidth() {
                return Math.min(super.getPrefWidth(), Core.graphics.getWidth());
            }
        };
        ta.margin(4);
        ta.update(() -> {
            b.localToStageCoordinates(Tmp.v1.set(b.getWidth() / 2f, b.getHeight() / 2f));
            ta.setPosition(Tmp.v1.x, Tmp.v1.y, Align.center);
            if (ta.getWidth() > Core.scene.getWidth()) ta.setWidth(Core.graphics.getWidth());
            if (ta.getHeight() > Core.scene.getHeight()) ta.setHeight(Core.graphics.getHeight());
            ta.keepInStage();
            ta.invalidateHierarchy();
            ta.pack();
        });

        Core.scene.add(ta);

        ta.top().pane(select -> table.get(select, () -> ta.actions(Actions.remove()))).pad(0f).top().scrollX(false);
        ta.actions(Actions.alpha(0), Actions.fadeIn(0.001f));

        ta.pack();
    }

    public float findVal(String name) {
        switch (name) {
            case "damage": {
                if (newType.equals("point")) {
                    return bullet.damage / 1.4f;
                }
                return bullet.damage;
            }
            case "pass": {
                return bullet.calculateRange();
            }
            case "prices": {
                if (newType.equals("point")) {
                    return 0;
                }
                return bullet.pierceCap;
            }
            case "splash": {
                return bullet.splashDamage * bullet.splashDamageRadius * (bullet.splashDamagePierce ? 1.5f : 1f) / 4;
            }
            case "lightning": {
                switch (newType) {
                    case "bullet", "laser", "continuousF", "continuousL", "point", "rail" -> {
                        return bullet.lightningLength * bullet.lightningLengthRand * bullet.lightningDamage * bullet.lightning / 8;
                    }
                    case "lightning" -> {
                        return bullet.lightningLength * bullet.lightningLengthRand * bullet.lightningDamage * bullet.lightning / 16;
                    }
                }
            }
            case "percent": {
                return bullet.percent;
            }
            case "frags": {
                return bullet.fragBullets;
            }
        }
        return Float.MAX_VALUE;
    }

    public String findTyp(String name) {
        if (newType.equals("continuousF") || name.equals("continuousL")) {
            if (name.equals("lifetime")) {
                return "damage";
            }
        }
        return "none";
    }

    public void updateHeavy() {
        heavy = 0.5f;
        heavy += ProjectsLocated.getHeavy("damage", findVal("damage"));
        heavy += ProjectsLocated.getHeavy("pass", findVal("pass"));
        heavy += ProjectsLocated.getHeavy("prices", findVal("prices"));
        heavy += ProjectsLocated.getHeavy("splash", findVal("splash"));
        heavy += ProjectsLocated.getHeavy("lightning", findVal("lightning"));
        heavy += ProjectsLocated.getHeavy("percent", findVal("percent"));
        heavy += ProjectsLocated.getHeavy("frags", findVal("frags"));
    }
}