package Floor.FType.FDialog;

import arc.Core;
import arc.func.Cons;
import arc.func.Cons2;
import arc.math.Interp;
import arc.scene.Element;
import arc.scene.actions.Actions;
import arc.scene.ui.Button;
import arc.scene.ui.layout.Table;
import arc.util.Align;
import arc.util.Strings;
import arc.util.Tmp;
import mindustry.entities.Effect;
import mindustry.entities.effect.MultiEffect;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;

import static mindustry.Vars.ui;

abstract class DialogUtils {
    public static void createNumberDialog(Table on, String dia, String tile, float def, Cons<Float> apply, Runnable rebuild) {
        on.table(t -> {
            t.label(() -> Core.bundle.get("dialog." + dia + "." + tile) + ": ");
            t.label(() -> def + "").pad(3);
            t.button(Icon.pencilSmall, Styles.flati, () -> ui.showTextInput(Core.bundle.get("dialog." + dia + "." + tile), "", 15, def + "", true, str -> {
                if (Strings.canParsePositiveFloat(str)) {
                    float amount = Strings.parseFloat(str);
                    apply.get(amount);
                    rebuild.run();
                } else {
                    ui.showInfo(Core.bundle.get("@inputError"));
                }
            })).size(55);
        }).pad(10).fillX();
        ;
    }

    public static void createBooleanDialog(Table on, String dia, String tile, boolean def, Cons<Boolean> apply, Runnable rebuild) {
        on.table(t -> {
            t.label(() -> Core.bundle.get("dialog." + dia + "." + tile) + ": ").pad(5);
            t.label(() -> Core.bundle.get("@" + def)).pad(5);
            t.button(Icon.rotate, () -> {
                apply.get(!def);
                rebuild.run();
            });
        });
    }

    public static void createNumberDialogWithLimit(Table on, String dia, String tile, float def, float max, float min, Cons<Float> apply, Runnable rebuild) {
        on.table(t -> {
            t.label(() -> Core.bundle.get("dialog." + dia + "." + tile) + ": ");
            t.label(() -> def + "").pad(3);
            t.button(Icon.pencilSmall, Styles.flati, () -> ui.showTextInput(Core.bundle.get("dialog." + dia + "." + tile), "", 15, def + "", true, str -> {
                if (Strings.canParsePositiveFloat(str)) {
                    float amount = Strings.parseFloat(str);
                    if (min <= amount && max >= amount) {
                        apply.get(amount);
                        rebuild.run();
                    } else {
                        ui.showInfo(Core.bundle.format("configure.invalid", min, max));
                    }
                } else {
                    ui.showInfo(Core.bundle.get("@inputError"));
                }
            })).size(55);
        });
    }

    public static void createLevDialog(Table on, String dia, String type, String tile, float def, Cons<Float> apply, Runnable rebuild, Runnable updateHeavy, StrBool levUser, BoolGetter hevUser) {
        on.table(t -> {
            t.label(() -> Core.bundle.get("dialog." + dia + "." + tile) + ": ");
            t.label(() -> def + "").pad(3);
            t.button(Icon.pencilSmall, Styles.flati, () -> ui.showTextInput(Core.bundle.get("dialog." + dia + "." + tile), "", 15, def + "", true, str -> {
                if (Strings.canParsePositiveFloat(str)) {
                    float amount = Strings.parseFloat(str);
                    apply.get(amount);
                    updateHeavy.run();
                    if (!levUser.get(type)) {
                        ui.showInfo(Core.bundle.get("@levelOutOfBounds"));
                        apply.get(def);
                        return;
                    } else if (!hevUser.get()) {
                        ui.showInfo(Core.bundle.get("@tooHeavy"));
                        apply.get(def);
                        return;
                    }
                    rebuild.run();
                } else {
                    ui.showInfo(Core.bundle.get("@inputError"));
                }
            })).size(55);
        }).pad(10).fillX();
    }

    public static void createEffectLine(Table t, EffectTableGetter on, String dia, String name, Effect list) {
        MultiEffect multi = (MultiEffect) list;
        if (multi != null) {
            t.label(() -> Core.bundle.get("dialog." + dia + "." + name) + "->");
            t.button(Icon.pencil, () -> {
                BaseDialog bd = new BaseDialog("");
                bd.cont.pane(li -> {
                    li.table(ta -> {
                        on.set(ta);
                        rebuildEffectList(on.get(), list);
                    }).grow();
                    li.row();
                    li.button(Icon.add, () -> {
                        Effect effect = new Effect();
                        Effect[] effects = new Effect[multi.effects.length + 1];
                        System.arraycopy(multi.effects, 0, effects, 0, multi.effects.length);
                        effects[effects.length - 1] = effect;
                        multi.effects = effects;
                        rebuildEffectList(on.get(), list);
                    });
                }).growX().growY();
                bd.row();
                bd.buttons.button(Icon.left, bd::hide);
                bd.show();
            });
        }
    }

    private static void rebuildEffectList(Table on, Effect list) {
        on.clear();
        MultiEffect effect = (MultiEffect) list;
        if (effect != null) {
            for (int i = 0; i < effect.effects.length; i++) {
                int finalI = i;
                on.table(t -> {
                    t.label(() -> finalI + "").growX();
                    t.button(Icon.pencil, () -> createEffectDialog(e -> effect.effects[finalI] = e,
                            () -> rebuildEffectList(on, list))).growX();
                    t.button(Icon.trash, () -> {
                        Effect[] effects = new Effect[effect.effects.length - 1];
                        for (int j = 0; j < effect.effects.length; j++) {
                            if (j != finalI) {
                                effects[j] = effect.effects[j];
                            }
                        }
                        effect.effects = effects;
                        rebuildEffectList(on, list);
                    }).growX();
                }).growX();
                on.row();
            }
        }
    }

    private static void createEffectDialog(Cons<Effect> apply, Runnable hide) {
        EffectDialog ed = new EffectDialog("", apply);
        ed.hidden(hide);
    }

    public static void createSelectDialog(Button b, Cons2<Table, Runnable> table) {
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

        Element hitter = new Element();

        Runnable hide = () -> {
            Core.app.post(hitter::remove);
            ta.actions(Actions.fadeOut(0.3f, Interp.fade), Actions.remove());
        };

        hitter.fillParent = true;
        hitter.tapped(hide);

        Core.scene.add(hitter);

        ta.update(() -> {
            if (b.parent == null || !b.isDescendantOf(Core.scene.root)) {
                Core.app.post(() -> {
                    hitter.remove();
                    ta.remove();
                });
                return;
            }

            b.localToStageCoordinates(Tmp.v1.set(b.getWidth() / 2f, b.getHeight() / 2f));
            ta.setPosition(Tmp.v1.x, Tmp.v1.y, Align.center);
            if (ta.getWidth() > Core.scene.getWidth()) ta.setWidth(Core.graphics.getWidth());
            if (ta.getHeight() > Core.scene.getHeight()) ta.setHeight(Core.graphics.getHeight());
            ta.keepInStage();
            ta.invalidateHierarchy();
            ta.pack();
        });

        Core.scene.add(ta);

        ta.top().pane(select -> table.get(select, hide)).pad(0f).top().scrollX(false);
        ta.actions(Actions.alpha(0), Actions.fadeIn(0.001f));

        ta.pack();
    }

    public static void createTypeLine(Table t, String dia, String type, float value) {
        t.row();
        t.table(table -> {
            table.background(Tex.scroll);
            table.label(() -> Core.bundle.get("dialog." + dia + "." + type)).left();
            table.row();
            table.label(() -> Core.bundle.get("@heavyUse") + ":  " + ProjectsLocated.getHeavy(type, value)).left().pad(5);
            table.label(() -> Core.bundle.get("@maxLevel") + ":  " + ProjectsLocated.maxLevel.get(type)).left().pad(5);
        });
        t.row();
    }

    public static void createMessageLine(Table on, String dia, String name) {
        on.row();
        on.label(() -> Core.bundle.get("dialog." + dia + "." + name)).width(35).pad(5);
    }

    public interface BoolGetter {
        boolean get();
    }

    public interface StrBool {
        boolean get(String str);
    }

    public interface EffectTableGetter {
        Table get();

        void set(Table table);
    }
}
