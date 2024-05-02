package Floor.FType.FDialog;

import Floor.FEntities.FBulletType.PercentBulletType;
import arc.Core;
import arc.func.Cons2;
import arc.graphics.Color;
import arc.scene.actions.Actions;
import arc.scene.ui.Button;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Align;
import arc.util.Strings;
import arc.util.Tmp;
import mindustry.content.Liquids;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.LaserBulletType;
import mindustry.entities.bullet.LiquidBulletType;
import mindustry.gen.Icon;
import mindustry.type.Liquid;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;

import java.util.HashMap;

import static mindustry.Vars.ui;

public class BulletDialog extends BaseDialog {
    public BaseDialog parent;
    public final Seq<String> types = new Seq<>(new String[]{"bullet", "laser", "liquid"});

    //rollback
    public String lastType = "";
    public HashMap<String, Object> typeLast = new HashMap<>();
    public HashMap<String, Object> baseLast = new HashMap<>();

    //update
    public String newType = "";
    public HashMap<String, Object> typeNew = new HashMap<>();
    public HashMap<String, Object> baseNew = new HashMap<>();

    //global
    public BulletType bullet;
    public float heavy = 0.5f;
    public Table typeOn;
    public Table baseOn;

    public BulletDialog(BaseDialog parent, String title, DialogStyle style) {
        super(title, style);
        shown(this::loadBase);
        this.parent = parent;

        buttons.button("@back", Icon.left, () -> {
            loadOut(typeLast, baseLast, lastType);
            hide();
        }).size(210f, 64f);
        buttons.button("@apply", Icon.flipY, () -> {
            loadOut(typeNew, baseNew, newType);
            hide();
        }).size(210f, 64f);
    }

    public BulletDialog(BaseDialog parent, String title) {
        super(title);
        shown(this::loadBase);
        this.parent = parent;

        buttons.button("@back", Icon.left, () -> {
            loadOut(typeLast, baseLast, lastType);
            hide();
        }).size(210f, 64f);
        buttons.button(Core.bundle.get("@apply"), Icon.right, () -> {
            loadOut(typeNew, baseNew, newType);
            hide();
        }).size(210f, 64f);
    }

    public void loadBase() {
        loadIn();
        updateHeavy();
        cont.pane(this::Front);
    }

    public void Front(Table table) {
        table.table(t -> {
            t.add(Core.bundle.get("dialog.bullet-type") + ":").size(40).pad(10);
            t.label(() -> Core.bundle.format("dialog." + newType)).size(40).pad(10);
            t.button(b -> {
                b.image(Icon.down).size(5);

                b.clicked(() -> createSelectDialog(b, (tb, hide) -> {
                    tb.top();
                    for (String name : types) {
                        tb.button(Core.bundle.get("dialog." + name), () -> {
                            newType = name;
                            typeNew = typeLast = Core.settings.getJson("floor-bulletDialog-" + newType, HashMap.class, HashMap::new);
                            hide.run();
                            updateHeavy();
                            rebuildType();
                        }).growX().width(200);
                        tb.row();
                    }
                }));
            }, Styles.logici, () -> {
            }).size(40);
        }).pad(2).left().growX().row();

        table.table(t -> typeOn = t).pad(2).left().fillX();
        rebuildType();
        table.row();
        table.table(t -> baseOn = t).pad(2).left().fillX();
        rebuildBase();
    }

    public void rebuildType() {
        typeOn.clear();
        switch (newType) {
            case "bullet" -> {
                createNumberDialog("bulletWide", typeOn, 12, 0, 45);
                createNumberDialog("bulletHeight", typeOn, 12, 0, 45);
                typeOn.row();
            }
            case "laser" -> {

            }
            case "liquid" -> {
                createNumberDialog("liquidWide", typeOn, 12, 0, 45);
                createNumberDialog("liquidHeight", typeOn, 12, 0, 45);
                typeOn.row();
            }
        }
    }

    public void rebuildBase() {
        baseOn.clear();
        createNumberDialog("fragX", baseOn, 0, -12, 12);
        createNumberDialog("fragY", baseOn, 0, -12, 12);
        baseOn.row();
        createLevDialog("frags", baseOn, 0);
    }

    public void createLevDialog(String name, Table t, float def) {
        t.table(type -> {
            type.add(Core.bundle.get("dialog.bullet." + name) + ":").pad(3).color(Color.red);
            type.label(() -> typeNew.computeIfAbsent(name, s -> def) + "");
            type.button(Icon.pencil, Styles.flati, () -> ui.showTextInput(Core.bundle.get("dialog.bullet." + name), "", 15, typeNew.get(name) + "", true, str -> {
                if (Strings.canParsePositiveFloat(str)) {
                    float amount = Strings.parseFloat(str);
                    if (ProjectsLocated.couldUse(name, amount) && ProjectsLocated.getHeavy(name, amount) + heavy <= ProjectsLocated.freeSize) {
                        typeNew.put(name, amount);
                        heavy += ProjectsLocated.getHeavy(name, amount);
                        return;
                    }
                }
                ui.showInfo(Core.bundle.format("@levelOutOfBounds"));
            })).size(55);
        }).pad(10).fillX();
    }

    public void createNumberDialog(String name, Table t, float def, float min, float max) {
        t.table(base -> {
            base.add(Core.bundle.get("dialog.bullet." + name) + ":");
            base.label(() -> typeNew.computeIfAbsent(name, s -> def) + "");
            base.button(Icon.pencil, Styles.flati, () -> ui.showTextInput(Core.bundle.get("dialog.bullet." + name), "", 15, typeNew.get(name) + "", true, str -> {
                if (Strings.canParsePositiveFloat(str)) {
                    float amount = Strings.parseFloat(str);
                    if ((amount >= min && amount <= max) || max < min) {
                        typeNew.put(name, amount);
                        return;
                    }
                }
                ui.showInfo(Core.bundle.format("configure.invalid", 45));
            })).size(55);
        }).pad(10).fillX();
    }

    public BulletType Bullet() {
        switch (newType) {
            case "bullet" -> {
                bullet = new PercentBulletType();
                PercentBulletType pbt = (PercentBulletType) bullet;
                for (String val : typeNew.keySet()) {
                    switch (val) {
                        case "bulletWide" -> pbt.width = (float) typeNew.get(val);
                        case "bulletHeight" -> pbt.height = (float) typeNew.get(val);
                    }
                }
            }
            case "laser" -> {
                bullet = new LaserBulletType();

            }
            case "liquid" -> {
                bullet = new LiquidBulletType();

            }
            default -> {
                return new BulletType(0, 0);
            }
        }
        return bullet;
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

    public void loadFromBullet(BulletType bu) {
        if (bu instanceof PercentBulletType pbt) {
            newType = lastType = "bullet";
        } else if (bu instanceof LaserBulletType lbt) {
            newType = lastType = "laser";
        } else if (bu instanceof LiquidBulletType lbt) {
            newType = lastType = "liquid";
        }
    }

    public void updateHeavy() {
        heavy = 0.5f;
        for (String type : baseNew.keySet()) {
            if (ProjectsLocated.levels.get(type) != null) {
                Object obj = baseNew.get(type);
                if (obj instanceof Float f) {
                    heavy += ProjectsLocated.getHeavy(type, f);
                }
            }
        }

        switch (newType) {
            case "bullet" -> {
                heavy += ProjectsLocated.getHeavy("damage",
                        (float) typeNew.computeIfAbsent("damage", k -> 0f));
                heavy += ProjectsLocated.getHeavy("pass",
                        (float) typeNew.computeIfAbsent("lifetime", k -> 0f) *
                                (float) typeNew.computeIfAbsent("speed", k -> 0f) * 17.5f);
            }
            case "laser" -> {
                heavy += ProjectsLocated.getHeavy("damage",
                        (float) typeNew.computeIfAbsent("damage", k -> 0f) *
                                (float) typeNew.computeIfAbsent("laserWidth", k -> 0f) *
                                (float) typeNew.computeIfAbsent("laserLong", k -> 0f) / 128);
                heavy += ProjectsLocated.getHeavy("pass",
                        (float) typeNew.computeIfAbsent("laserLong", k -> 0f) / 4);
            }
            case "liquid" -> {
                heavy += ProjectsLocated.getHeavy("damage",
                        (float) typeNew.computeIfAbsent("damage", k -> 0f) +
                                ((Liquid) typeNew.computeIfAbsent("liquid", k -> Liquids.water)).effect.damage);
                heavy += ProjectsLocated.getHeavy("pass",
                        (float) typeNew.computeIfAbsent("lifetime", k -> 0f) *
                                (float) typeNew.computeIfAbsent("speed", k -> 0f) * 17.5f);
            }
        }
    }

    public void loadIn() {
        newType = lastType = Core.settings.getString("floor-bulletName");
        if (newType == null || newType.isEmpty()) {
            newType = lastType = "bullet";
            Core.settings.put("floor-bulletName", "bullet");
        }

        typeNew = typeLast = Core.settings.getJson("floor-bulletDialog-" + newType, HashMap.class, HashMap::new);
        if (typeNew == null) {
            typeNew = typeLast = new HashMap<>();
            Core.settings.putJson("floor-bulletDialog-" + newType, HashMap.class, typeNew);
        }

        baseNew = baseLast = Core.settings.getJson("floor-bulletDialog-base", HashMap.class, HashMap::new);
        if (baseNew == null) {
            baseNew = baseLast = new HashMap<>();
            Core.settings.putJson("floor-bulletDialog-base", HashMap.class, baseNew);
        }
    }

    public void loadOut(HashMap<String, Object> typeMap, HashMap<String, Object> baseMap, String type) {
        Core.settings.put("floor-bulletName", type);
        Core.settings.putJson("floor-bulletDialog-" + type, HashMap.class, typeMap);
        Core.settings.putJson("floor-bulletDialog-base", HashMap.class, baseMap);
    }
}