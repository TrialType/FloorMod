package Floor.FType.FDialog;

import arc.Core;
import arc.func.Cons;
import arc.func.Cons2;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.geom.Vec2;
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
    public Seq<EAction> all = new Seq<>();
    public Table base;
    public Table action;
    public Table list;
    public Table now;
    public String typeNow = "circle";
    public String cls = "";
    public float[] acs = new float[10];
    public EffectDialog(Effect effect, String title) {
        super(title);
        this.effect = effect;

        buttons.button("@back", Icon.left, this::hide).size(210f, 64f);
        buttons.button(Core.bundle.get("@apply"), Icon.right, () -> {
            loadOut();
            this.effect.renderer = e -> {
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
        }).grow();
        shown(this::rebuild);
    }

    public void rebuild() {
        rebuildBase();
        rebuildAction();
    }

    public void rebuildBase() {
        base.clear();
        base.background(Tex.buttonEdge3);
        base.table(table -> createNumberTable(table, "lifetime", effect.lifetime,
                f -> effect.lifetime = f, this::rebuildBase)).growX();
        base.table(table -> createNumberTable(table, "startDelay", effect.startDelay,
                f -> effect.startDelay = f, this::rebuildBase)).growX();
        base.row();
        base.table(table -> createNumberTable(table, "clip", effect.clip,
                f -> effect.clip = f, this::rebuildBase)).growX();
        base.table(table -> createNumberTable(table, "baseRotation", effect.baseRotation,
                f -> effect.baseRotation = f, this::rebuildBase)).growX();
        base.row();
        base.table(table -> {
            table.label(() -> Core.bundle.get("dialog.effect.followParent") + "->");
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
            }).pad(5);
        }).growX();
        base.table(table -> {
            table.label(() -> Core.bundle.get("dialog.effect.rotWithParent") + "->");
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
            }).pad(5);
        }).growX();
    }

    public void rebuildAction() {
        action.clear();
        action.table(table -> list = table).grow();
        rebuildList();

        action.row();
        action.button(Core.bundle.get("dialog.effect.addAction"), Icon.add, () -> createNowDialog(-1)).center().growX().row();
    }

    public void rebuildNow() {
        now.clear();
        switch (typeNow) {
            case "circle": {
                createNumberTable(now, "circleNumber", acs[0], f -> acs[0] = f, this::rebuildNow);
                createNumberTable(now, "circleLengthFrom", acs[1], f -> acs[1] = f, this::rebuildNow);
                createNumberTable(now, "circleLengthTo", acs[2], f -> acs[2] = f, this::rebuildNow);
                now.row();
                createNumberTable(now, "circleRangeFrom", acs[3], f -> acs[3] = f, this::rebuildNow);
                createNumberTable(now, "circleRangeTo", acs[4], f -> acs[4] = f, this::rebuildNow);
                createNumberTable(now, "circleAngle", acs[5], f -> acs[5] = f, this::rebuildNow);
                now.row();
                createNumberTable(now, "circleAngleRand", acs[6], f -> acs[6] = f, this::rebuildNow);
                createNumberTable(now, "circleMinRange", acs[7], f -> acs[7] = f, this::rebuildNow);
                break;
            }
            case "line": {
                createNumberTable(now, "lineNumber", acs[0], f -> acs[0] = f, this::rebuildNow);
                createNumberTable(now, "lineLengthFrom", acs[1], f -> acs[1] = f, this::rebuildNow);
                now.row();
                createNumberTable(now, "lineLengthTo", acs[2], f -> acs[2] = f, this::rebuildNow);
                createNumberTable(now, "lineStrokeFrom", acs[3], f -> acs[3] = f, this::rebuildNow);
                now.row();
                createNumberTable(now, "lineStrokeTo", acs[4], f -> acs[4] = f, this::rebuildNow);
                createNumberTable(now, "lineRangeFrom", acs[5], f -> acs[5] = f, this::rebuildNow);
                now.row();
                createNumberTable(now, "lineRangeTo", acs[6], f -> acs[6] = f, this::rebuildNow);
                createNumberTable(now, "lineAngle", acs[7], f -> acs[7] = f, this::rebuildNow);
                now.row();
                createNumberTable(now, "lineAngleRand", acs[8], f -> acs[8] = f, this::rebuildNow);
                createNumberTable(now, "lineMinRange", acs[9], f -> acs[9] = f, this::rebuildNow);
                break;
            }
            case "wave": {
                createNumberTable(now, "waveStrokeFrom", acs[0], f -> acs[0] = f, this::rebuildNow);
                createNumberTable(now, "waveStrokeTo", acs[1], f -> acs[1] = f, this::rebuildNow);
                now.row();
                createNumberTable(now, "waveRangeFrom", acs[2], f -> acs[2] = f, this::rebuildNow);
                createNumberTable(now, "waveRangeTo", acs[3], f -> acs[3] = f, this::rebuildNow);
                break;
            }
        }
    }

    public void rebuildList() {
        list.clear();
        for (int i = 0; i < all.size; i++) {
            int finalI = i;
            EAction e = all.get(finalI);
            switch (e.type) {
                case "circle": {
                    list.table(c -> {
                        c.label(() -> Core.bundle.get("dialog.effect.circle"));
                        c.button(Icon.trash, () -> {
                            all.remove(finalI);
                            rebuildList();
                        });
                        c.button(Icon.pencil, () -> {
                            typeNow = e.type;
                            StringBuilder sb = new StringBuilder();
                            for (int j = 0; j < e.colors.length; j++) {
                                if (j == 0) {
                                    sb.append(e.colors[j]);
                                } else {
                                    sb.append(",").append(e.colors[j]);
                                }
                            }
                            cls = sb.toString();
                            System.arraycopy(e.values, 0, acs, 0, e.values.length);
                            createNowDialog(finalI);
                        });
                        c.row();
                        c.label(() -> Core.bundle.get("dialog.effect.circleNumber") + ": " + e.values[0]).growX();
                        c.label(() -> Core.bundle.get("dialog.effect.circleLengthFrom") + ": " + e.values[1]).growX().pad(2);
                        c.label(() -> Core.bundle.get("dialog.effect.circleLengthTo") + ": " + e.values[2]).growX().pad(2);
                        c.row();
                        c.label(() -> Core.bundle.get("dialog.effect.circleRangeFrom") + ": " + e.values[3]).growX();
                        c.label(() -> Core.bundle.get("dialog.effect.circleRangeTo") + ": " + e.values[4]).growX().pad(2);
                        c.label(() -> Core.bundle.get("dialog.effect.circleAngle") + ": " + e.values[5]).growX().pad(2);
                        c.row();
                        c.label(() -> Core.bundle.get("dialog.effect.circleAngleRand") + ": " + e.values[6]).growX().pad(2);
                        c.label(() -> Core.bundle.get("dialog.effect.circleMinRange") + ": " + e.values[7]).growX().pad(2);
                    });
                    break;
                }
                case "line": {
                    list.table(c -> {
                        c.label(() -> Core.bundle.get("dialog.effect.line"));
                        c.button(Icon.trash, () -> {
                            all.remove(finalI);
                            rebuildList();
                        });
                        c.button(Icon.pencil, () -> {
                            typeNow = e.type;
                            StringBuilder sb = new StringBuilder();
                            for (int j = 0; j < e.colors.length; j++) {
                                if (j == 0) {
                                    sb.append(e.colors[j]);
                                } else {
                                    sb.append(",").append(e.colors[j]);
                                }
                            }
                            cls = sb.toString();
                            System.arraycopy(e.values, 0, acs, 0, e.values.length);
                            createNowDialog(finalI);
                        });
                        c.row();
                        c.label(() -> Core.bundle.get("dialog.effect.lineNumber") + ": " + e.values[0]).growX();
                        c.label(() -> Core.bundle.get("dialog.effect.lineLengthFrom") + ": " + e.values[1]).growX().pad(2);
                        c.row();
                        c.label(() -> Core.bundle.get("dialog.effect.lineLengthTo") + ": " + e.values[2]).growX().pad(2);
                        c.label(() -> Core.bundle.get("dialog.effect.lineStrokeFrom") + ": " + e.values[3]).growX();
                        c.row();
                        c.label(() -> Core.bundle.get("dialog.effect.lineStrokeTo") + ": " + e.values[4]).growX().pad(2);
                        c.label(() -> Core.bundle.get("dialog.effect.lineRangeFrom") + ": " + e.values[5]).growX().pad(2);
                        c.row();
                        c.label(() -> Core.bundle.get("dialog.effect.lineRangeTo") + ": " + e.values[6]).growX().pad(2);
                        c.label(() -> Core.bundle.get("dialog.effect.lineAngle") + ": " + e.values[7]).growX().pad(2);
                        c.row();
                        c.label(() -> Core.bundle.get("dialog.effect.lineAngleRand") + ": " + e.values[8]).growX().pad(2);
                        c.label(() -> Core.bundle.get("dialog.effect.lineMinRange") + ": " + e.values[9]).growX().pad(2);
                    });
                    break;
                }
                case "wave": {
                    list.table(c -> {
                        c.label(() -> Core.bundle.get("dialog.effect.wave"));
                        c.button(Icon.trash, () -> {
                            all.remove(finalI);
                            rebuildList();
                        });
                        c.button(Icon.pencil, () -> {
                            typeNow = e.type;
                            StringBuilder sb = new StringBuilder();
                            for (int j = 0; j < e.colors.length; j++) {
                                if (j == 0) {
                                    sb.append(e.colors[j]);
                                } else {
                                    sb.append(",").append(e.colors[j]);
                                }
                            }
                            cls = sb.toString();
                            System.arraycopy(e.values, 0, acs, 0, e.values.length);
                            createNowDialog(finalI);
                        });
                        c.row();
                        c.label(() -> Core.bundle.get("dialog.effect.waveStrokeFrom") + ": " + e.values[0]).growX();
                        c.label(() -> Core.bundle.get("dialog.effect.waveStrokeTo") + ": " + e.values[1]).growX().pad(2);
                        c.row();
                        c.label(() -> Core.bundle.get("dialog.effect.waveRangeFrom") + ": " + e.values[2]).growX().pad(2);
                        c.label(() -> Core.bundle.get("dialog.effect.waveRangeTo") + ": " + e.values[3]).growX();
                    });
                    break;
                }
            }
            list.row();
        }
    }

    public void createNowDialog(int index) {
        BaseDialog bd = new BaseDialog("");
        bd.cont.pane(t -> {
            t.table(select -> {
                select.label(() -> Core.bundle.get("dialog.effect.type") + ": " + Core.bundle.get("dialog.effect." + typeNow));
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
                    }));
                }, () -> {
                }).row();
            });

            t.row();
            t.table(ta -> now = ta);
            rebuildNow();

            t.row();
            t.table(ta -> ta.button(Core.bundle.get("dialog.effect.inputForColor"), () -> ui.showTextInput("", "", 150, cls, true, str -> {
                        for (String c : str.split(",")) {
                            if (c.length() < 6 || c.length() > 8 || !c.matches("([a,f][0,9]){6,8}")) {
                                ui.showInfo(Core.bundle.format("@inputError"));
                                return;
                            }
                        }
                        cls = str;
                    })).growX()
            ).growX();
        }).grow();

        bd.buttons.button("@back", Icon.left, () -> {
            bd.remove();
            bd.hide();
        }).width(100).pad(5);
        bd.buttons.button(Core.bundle.get("@apply"), () -> {
            if (index < 0) {
                all.add(new EAction(typeNow, acs, cls.split(",")));
            } else {
                all.set(index, new EAction(typeNow, acs, cls.split(",")));
            }
            rebuildList();
            bd.remove();
            bd.hide();
        }).width(100);
        bd.show();
    }

    public void createNumberTable(Table t, String name, float value, Cons<Float> apply, updater updater) {
        t.label(() -> Core.bundle.get("dialog.effect." + name) + ": " + value);
        t.button(Icon.pencil, Styles.flati, () -> ui.showTextInput(Core.bundle.get("dialog.effect." + name), "", 15, value + "", true, str -> {
            if (Strings.canParsePositiveFloat(str)) {
                apply.get(Float.parseFloat(str));
                updater.update();
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

    public void loadOut() {
        renderers.clear();
        for (EAction a : all) {
            switch (a.type) {
                case "circle": {
                    renderers.add(e -> {
                        float r = a.values[1] + e.fin() * (a.values[2] - a.values[1]);
                        float len = a.values[3] + e.fin() * (a.values[4] - a.values[3]);
                        Angles.randLenVectors(e.id, e.fin(), (int) (a.values[0] + 0), len, a.values[5], a.values[6], (x, y, f, fl) -> {
                            Vec2 vec = new Vec2(x / e.fin(), y / e.fin());
                            vec.setLength(vec.len() + a.values[7]);
                            setColor(a.colors, e.fin());
                            Fill.circle(vec.x + e.x, vec.y + e.y, r);
                        });
                    });
                    break;
                }
                case "line": {
                    renderers.add(e -> {
                        float l = a.values[1] + e.fin() * (a.values[2] - a.values[1]);
                        float s = a.values[3] + e.fin() * (a.values[4] - a.values[3]);
                        float ra = a.values[5] + e.fin() * (a.values[6] - a.values[5]);
                        Angles.randLenVectors(e.id, e.fin(), (int) (a.values[0] + 0), ra, a.values[7], a.values[8], (x, y, f, fl) -> {
                            Vec2 vec = new Vec2(x / e.fin(), y / e.fin());
                            vec.setLength(vec.len() + a.values[9]);
                            float x1 = vec.x + e.x;
                            float y1 = vec.y + e.y;
                            vec.setLength(vec.len() + l);
                            float x2 = vec.x + e.x;
                            float y2 = vec.y + e.y;
                            setColor(a.colors, e.fin());
                            Lines.stroke(s);
                            Lines.line(x1 + e.x, y1 + e.y, x2 + e.y, y2 + e.y);
                        });
                    });
                    break;
                }
                case "wave": {
                    renderers.add(e -> {
                        float l = a.values[2] + e.fin() * (a.values[3] - a.values[2]);
                        float s = a.values[0] + e.fin() * (a.values[1] - a.values[0]);
                        setColor(a.colors, e.fin());
                        Lines.stroke(s);
                        Lines.poly(e.x, e.y, Lines.circleVertices(l), l);
                    });
                    break;
                }
            }
        }
    }

    public void setColor(String[] colors, float fin) {
        if (colors.length == 0) {
            Draw.color(Color.white);
            return;
        }
        float l1 = 1f / colors.length;
        if (fin <= l1) {
            Draw.color(Color.valueOf(colors[0]));
            return;
        }
        int fr = (int) (fin / l1);
        if (fr >= colors.length) {
            Draw.color(Color.valueOf(colors[colors.length - 1]));
            return;
        }
        int to = fr + 1;
        float fin2 = fin % l1;
        Draw.color(Color.valueOf(colors[fr]), Color.valueOf(colors[to]), fin2);
    }

    public interface effectAction {
        void get(Effect.EffectContainer e);
    }

    public interface updater {
        void update();
    }

    public static class EAction {
        String type;
        float[] values;
        String[] colors;

        public EAction(String type, float[] values, String[] colors) {
            this.type = type;
            this.values = values;
            this.colors = colors;
        }
    }
}
