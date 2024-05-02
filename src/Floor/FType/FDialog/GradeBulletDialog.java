package Floor.FType.FDialog;

import arc.Core;
import arc.math.Interp;
import arc.scene.actions.Actions;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Strings;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Icon;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;

import java.util.HashMap;

import static mindustry.Vars.ui;

public class GradeBulletDialog extends BaseDialog {
    public final Seq<String> types = new Seq<>(new String[]{"bullet", "laser", "liquid"});
    public static BulletType bullet;

    //rollback
    public String lastType = "";
    public HashMap<String, Object> use = new HashMap<>();

    //update
    public String type = "";
    public HashMap<String, Object> change = new HashMap<>();

    public GradeBulletDialog(String title, DialogStyle style) {
        super(title, style);
        shown(this::loadBase);

        buttons.button("@back", Icon.left, () -> {
            loadOut(use, type);
            hide();
        }).size(210f, 64f);
        buttons.button("@apply", Icon.flipY, () -> {
            loadOut(change, lastType);
            hide();
        }).size(210f, 64f);
    }

    public GradeBulletDialog(String title) {
        super(title);
        shown(this::loadBase);

        buttons.button("@back", Icon.left, () -> {
            loadOut(use, type);
            hide();
        }).size(210f, 64f);
        buttons.button("@apply", Icon.right, () -> {
            loadOut(change, lastType);
            hide();
        }).size(210f, 64f);
    }

    public void loadBase() {
        loadIn();

        cont.pane(this::BulletBase);
    }

    public void BulletBase(Table table) {
        table.table(t -> {
            t.add(Core.bundle.get("dialog.bullet-type") + ":");
            t.label(() -> Core.bundle.format("dialog." + type));
            t.button(Icon.down, () -> {
                Table select = new Table();
                select.pack();
                select.setTransform(true);
                select.actions(Actions.scaleTo(0f, 1f), Actions.visible(true),
                        Actions.scaleTo(1f, 1f, 0.07f, Interp.pow3Out));

                for (String name : types) {
                    select.button(Core.bundle.get("dialog." + name), () -> {
                        type = name;
                        use = change = Core.settings.getJson("floor-bulletDialog-" + type, HashMap.class, HashMap::new);
                        select.actions(Actions.scaleTo(0f, 1f, 0.06f, Interp.pow3Out), Actions.visible(false));
                    });
                    select.row();
                }
            });
        }).pad(2).left().fillX().row();

        switch (type) {
            case "bullet" -> table.table(t -> {
                t.table(base -> {
                    base.add(Core.bundle.get("dialog.bullet.bulletWide") + ":");
                    base.label(() -> change.computeIfAbsent("bulletWide", s -> 12) + "");
                    base.button(Icon.pencil, Styles.flati, () -> ui.showTextInput(Core.bundle.get("dialog.bullet.bulletWide"), "", 10, change.get("bulletWide") + "", true, str -> {
                        if (Strings.canParsePositiveInt(str)) {
                            int amount = Strings.parseInt(str);
                            if (amount > 0 && amount <= 45) {
                                change.put("bulletWide", amount);
                                return;
                            }
                        }
                        ui.showInfo(Core.bundle.format("configure.invalid", 45));
                    })).size(20);
                });

                t.table(base -> {
                    base.add(Core.bundle.get("dialog.bullet.bulletHeight") + ":");
                    base.label(() -> change.computeIfAbsent("bulletHeight", s -> 12) + "");
                    base.button(Icon.pencil, Styles.flati, () -> ui.showTextInput(Core.bundle.get("dialog.bullet.bulletHeight"), "", 10, change.get("bulletWide") + "", true, str -> {
                        if (Strings.canParsePositiveInt(str)) {
                            int amount = Strings.parseInt(str);
                            if (amount > 0 && amount <= 45) {
                                change.put("bulletHeight", amount);
                                return;
                            }
                        }
                        ui.showInfo(Core.bundle.format("configure.invalid", 45));
                    })).size(20);
                });
            }).pad(2).left().fillX();
            case "laser" -> table.table(t -> {

            }).pad(2).left().fillX();
            case "liquid" -> table.table(t -> {

            }).pad(2).left().fillX();
        }
    }

    public void loadIn() {
        type = lastType = Core.settings.getString("floor-bulletName");
        if (type == null || type.isEmpty()) {
            type = lastType = "bullet";
            Core.settings.put("floor-bulletName", "bullet");
        }
        switch (type) {
            case "bullet" -> {
                use = change = Core.settings.getJson("floor-bulletDialog-bullet", HashMap.class, HashMap::new);
                if (use == null) {
                    use = change = new HashMap<>();
                    Core.settings.putJson("floor-bulletDialog-bullet", HashMap.class, use);
                }
            }
            case "laser" -> {
                use = change = Core.settings.getJson("floor-bulletDialog-laser", HashMap.class, HashMap::new);
                if (use == null) {
                    use = change = new HashMap<>();
                    Core.settings.putJson("floor-bulletDialog-laser", HashMap.class, use);
                }
            }
            case "liquid" -> {
                use = change = Core.settings.getJson("floor-bulletDialog-liquid", HashMap.class, HashMap::new);
                if (use == null) {
                    use = change = new HashMap<>();
                    Core.settings.putJson("floor-bulletDialog-liquid", HashMap.class, use);
                }
            }
        }
    }

    public void loadOut(HashMap<String, Object> maps, String type) {
        Core.settings.put("floor-bulletName", type);
        switch (type) {
            case "bullet" -> Core.settings.putJson("floor-bulletDialog-bullet", HashMap.class, maps);
            case "laser" -> Core.settings.putJson("floor-bulletDialog-laser", HashMap.class, maps);
            case "liquid" -> Core.settings.putJson("floor-bulletDialog-liquid", HashMap.class, maps);
        }
    }
}
