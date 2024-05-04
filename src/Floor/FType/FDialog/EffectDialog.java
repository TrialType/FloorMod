package Floor.FType.FDialog;

import arc.Core;
import arc.func.Cons;
import arc.func.Cons2;
import arc.math.Interp;
import arc.scene.Element;
import arc.scene.actions.Actions;
import arc.scene.ui.Button;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Align;
import arc.util.Strings;
import arc.util.Tmp;
import mindustry.entities.Effect;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;

import static mindustry.Vars.ui;

public class EffectDialog extends BaseDialog {
    public Effect effect;
    public Seq<effectAction> renderers = new Seq<>();
    public Seq<action> all = new Seq<>();
    public Table base;
    public Table action;
    public Table list;
    public Table now;
    public String typeNow = "circle";
    public Seq<String> cls = new Seq<>();
    public Float[] acs = new Float[7];

    public EffectDialog(Effect effect, String title) {
        super(title);
        this.effect = effect;

        buttons.button("@back", Icon.left, this::hide).size(210f, 64f);
        buttons.button(Core.bundle.get("@apply"), Icon.right, () -> {
            effect.renderer = e -> {
                for (effectAction ea : renderers) {
                    ea.get(e);
                }
            };
            hide();
        }).size(210f, 64f);
        cont.pane(t -> {
            t.row();
            t.table(table -> base = table).growX();
            t.row();
            t.table(table -> action = table).growX();
        });
        shown(this::rebuild);
    }

    public void rebuild() {
        rebuildBase();
        rebuildAction();
    }

    public void rebuildBase() {
        base.clear();
        base.background(Tex.whiteui);
        base.table(table -> createNumberTable(table, "lifetime", effect.lifetime, f -> effect.lifetime = f));
        base.row();
        base.table(table -> createNumberTable(table, "startDelay", effect.startDelay, f -> effect.startDelay = f));
        base.row();
        base.table(table -> createNumberTable(table, "clip", effect.clip, f -> effect.clip = f));
        base.row();
        base.table(table -> createNumberTable(table, "baseRotation", effect.baseRotation, f -> effect.baseRotation = f));
        base.row();
        base.table(table -> {
            table.label(() -> Core.bundle.get("dialog.effect.followParent") + "->").left().size(40);
            table.button(button -> {
                button.image(Icon.add);
                if (effect.followParent) {
                    button.setBackground(Tex.buttonDown);
                } else {
                    button.setBackground(Tex.scroll);
                }
            }, () -> {
                if (effect.followParent) {
                    effect.followParent = false;
                    effect.rotWithParent = false;
                } else {
                    effect.followParent = true;
                }
                rebuildBase();
            }).left().size(40).pad(5);
            table.label(() -> Core.bundle.get("dialog.effect.rotWithParent") + "->").left().size(40).pad(10);
            table.button(button -> {
                button.image(Icon.add);
                if (effect.rotWithParent) {
                    button.setBackground(Tex.buttonDown);
                } else {
                    button.setBackground(Tex.scroll);
                }
            }, () -> {
                if (effect.rotWithParent) {
                    effect.rotWithParent = false;
                } else if (effect.followParent) {
                    effect.rotWithParent = true;
                }
                rebuildBase();
            }).left().size(40).pad(5);
        });
    }

    public void rebuildAction() {
        action.clear();
        action.table(table -> list = table).growX();
        rebuildList();

        action.button(Core.bundle.get("dialog.effect.addAction"), Icon.add, () -> {
            cls.clear();
            BaseDialog bd = new BaseDialog("");
            bd.cont.pane(t -> {
                t.table(select -> {
                    select.label(() -> Core.bundle.get("dialog.effect.type") + ": " + typeNow);
                    select.button(b -> {
                        b.image(Icon.pencilSmall);

                        b.clicked(() -> createSelectDialog(b, (ta, h) -> {
                            ta.top();
                            ta.button(Core.bundle.get("dialog.effect.circle"), () -> {
                                h.run();
                                typeNow = "circle";
                                rebuildNow();
                            }).growX().row();
                            ta.button(Core.bundle.get("dialog.effect.line"), () -> {
                                h.run();
                                typeNow = "line";
                                rebuildNow();
                            }).growX().row();
                            ta.button(Core.bundle.get("dialog.effect.wave"), () -> {
                                h.run();
                                typeNow = "wave";
                                rebuildNow();
                            }).growX().row();
                            ta.button(Core.bundle.get("dialog.effect.angles"), () -> {
                                h.run();
                                typeNow = "angles";
                                rebuildNow();
                            }).growX().row();
                        }));
                    }, () -> {
                    }).row();
                });

                t.table(ta -> now = ta);
                rebuildNow();
            }).grow();

            bd.buttons.button(Core.bundle.get("@apply"), () -> {
                all.add(new action("", acs, cls.items));
                rebuildList();
                bd.remove();
                bd.hide();
            }).width(100);
            bd.buttons.button(Core.bundle.get("@back"), Icon.left, () -> {
                bd.remove();
                bd.hide();
            }).width(100).pad(5);
            bd.show();
        }).center().growX().row();
    }

    public void rebuildNow() {
        now.clear();
        switch (typeNow) {
            case "circle": {
                createNumberTable(now, "circleLengthFrom", acs[0], f -> acs[0] = f);
                createNumberTable(now, "circleLengthTo", acs[1], f -> acs[1] = f);
                createNumberTable(now, "circleRange", acs[2], f -> acs[2] = f);
                createNumberTable(now, "circleRangeRand", acs[3], f -> acs[3] = f);
                createNumberTable(now, "circleAngle", acs[4], f -> acs[4] = f);
                createNumberTable(now, "circleAngleRand", acs[5], f -> acs[5] = f);
                createNumberTable(now, "circleMinRange", acs[6], f -> acs[6] = f);
            }
            case "line": {
                createNumberTable(now, "lineLengthFrom", acs[0], f -> acs[0] = f);
                createNumberTable(now, "lineLengthTo", acs[1], f -> acs[1] = f);
                createNumberTable(now, "lineRange", acs[2], f -> acs[2] = f);
                createNumberTable(now, "lineRangeRand", acs[3], f -> acs[3] = f);
                createNumberTable(now, "lineAngle", acs[4], f -> acs[4] = f);
                createNumberTable(now, "lineAngleRand", acs[5], f -> acs[5] = f);
                createNumberTable(now, "lineMinRange", acs[6], f -> acs[6] = f);
            }
            case "wave": {
                createNumberTable(now, "waveLengthFrom", acs[0], f -> acs[0] = f);
                createNumberTable(now, "waveLengthTo", acs[1], f -> acs[1] = f);
                createNumberTable(now, "waveRange", acs[2], f -> acs[2] = f);
                createNumberTable(now, "waveRangeRand", acs[3], f -> acs[3] = f);
                createNumberTable(now, "waveAngle", acs[4], f -> acs[4] = f);
                createNumberTable(now, "waveAngleRand", acs[5], f -> acs[5] = f);
                createNumberTable(now, "waveMinRange", acs[6], f -> acs[6] = f);
            }
            case "angles": {
                createNumberTable(now, "anglesLengthFrom", acs[0], f -> acs[0] = f);
                createNumberTable(now, "anglesLengthTo", acs[1], f -> acs[1] = f);
                createNumberTable(now, "anglesRange", acs[2], f -> acs[2] = f);
                createNumberTable(now, "anglesRangeRand", acs[3], f -> acs[3] = f);
                createNumberTable(now, "anglesAngle", acs[4], f -> acs[4] = f);
                createNumberTable(now, "anglesAngleRand", acs[5], f -> acs[5] = f);
                createNumberTable(now, "anglesMinRange", acs[6], f -> acs[6] = f);
            }
        }
    }

    public void rebuildList() {
        list.clear();
    }

    public void createNumberTable(Table t, String name, float value, Cons<Float> apply) {
        t.label(() -> Core.bundle.get("dialog.effect." + name) + ": " + value);
        t.button(Icon.pencil, Styles.flati, () -> ui.showTextInput(Core.bundle.get("dialog.effect." + name), "", 15, value + "", true, str -> {
            if (Strings.canParsePositiveFloat(str)) {
                apply.get(Float.parseFloat(str));
                rebuildBase();
                return;
            }
            ui.showInfo(Core.bundle.format("configure.invalid"));
        })).size(55);
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

    public interface effectAction {
        void get(Effect.EffectContainer e);
    }

    public static class action {
        String type;
        Float[] values;
        String[] colors;

        public action(String type, Float[] values, String[] colors) {
            this.type = type;
            this.values = values;
            this.colors = colors;
        }
    }
}
