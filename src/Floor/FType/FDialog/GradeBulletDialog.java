package Floor.FType.FDialog;

import Floor.FContent.FItems;
import arc.Core;
import arc.math.Interp;
import arc.scene.actions.Actions;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Icon;
import mindustry.type.Item;
import mindustry.ui.dialogs.BaseDialog;

public class GradeBulletDialog extends BaseDialog {
    public static BulletType bullet;
    public int nameUsing = 0;

    public final Seq<String> typeNames = new Seq<>(new String[]{"copper", "laser", "liquid"});
    public final static int bulletProjectNumber = 5;
    //copperLev 0,splashLev 2,pricesLev 3,knockLev 5,percentLev 6
    public Seq<Integer> use = new Seq<>(new Integer[bulletProjectNumber]);
    public Seq<Integer> change = new Seq<>(new Integer[bulletProjectNumber]);

    public GradeBulletDialog(String title, DialogStyle style) {
        super(title, style);
        shown(this::loadBase);

        buttons.button("@back", Icon.left, () -> {
            hide();
            loadOut(use);
        }).size(210f, 64f);
        buttons.button("@apply", Icon.flipY, () -> {
            hide();
            loadOut(change);
            use = change;
        }).size(210f, 64f);
    }

    public GradeBulletDialog(String title) {
        super(title);
    }

    public void loadBase() {
        loadIn();
        Seq<Integer> max = new Seq<>(new Integer[bulletProjectNumber]);
        for (int j = 0; j < bulletProjectNumber; j++) {
            Item[] items = FItems.allBullet[j];
            for (int i = items.length - 1; i >= 0; i--) {
                if (items[i].unlocked()) {
                    max.items[j] = i + 1;
                }
            }
        }

        for (int i = 0; i < bulletProjectNumber; i++) {
            if (max.get(i) < use.get(i)) {
                use.items[i] = change.items[i] = max.get(i);
            }
        }

        cont.pane(table -> {
            table.table(t -> {
                t.add(Core.bundle.get("dialog.bullet-type"));
                t.label(() -> Core.bundle.format("dialog." + typeNames.get(nameUsing)));
                t.button(Icon.down, () -> {
                    Table select = new Table();
                    select.pack();
                    select.setTransform(true);
                    select.actions(Actions.scaleTo(0f, 1f), Actions.visible(true),
                            Actions.scaleTo(1f, 1f, 0.07f, Interp.pow3Out));

                    for (String name : typeNames) {
                        select.button(Core.bundle.get("dialog." + name), () -> {
                            nameUsing = typeNames.indexOf(name);
                            select.actions(Actions.scaleTo(0f, 1f, 0.06f, Interp.pow3Out), Actions.visible(false));
                        });
                        select.row();
                    }
                });
            }).pad(2).left().fillX();

            if (nameUsing == 0) {
                table.table(t -> {
                    t.add(Core.bundle.get("dialog.bullet-type"));
                    t.label(() -> Core.bundle.format("dialog." + typeNames.get(nameUsing)));
                    t.button(Icon.down, () -> {
                        Table select = new Table();
                        select.pack();
                        select.setTransform(true);
                        select.actions(Actions.scaleTo(0f, 1f), Actions.visible(true),
                                Actions.scaleTo(1f, 1f, 0.07f, Interp.pow3Out));

                        for (String name : typeNames) {
                            select.button(Core.bundle.get("dialog." + name), () -> {
                                nameUsing = typeNames.indexOf(name);
                                select.actions(Actions.scaleTo(0f, 1f, 0.06f, Interp.pow3Out), Actions.visible(false));
                            });
                            select.row();
                        }
                    });
                }).pad(2).left().fillX();
            } else if (nameUsing == 1) {

            }
        });
    }

    public void loadIn() {
        Integer[] values = Core.settings.getJson("floor-bulletDialog",
                Integer[].class, () -> new Integer[use.size]);
        nameUsing = Core.settings.getInt("floor-bulletName");
        if (values != null) {
            use = new Seq<>(values);
            change = new Seq<>(values);
        }
    }

    public void loadOut(Seq<Integer> save) {
        Core.settings.putJson("floor-bulletDialog", Integer[].class, save.items);
        Core.settings.put("floor-bulletName", nameUsing);
    }
}
