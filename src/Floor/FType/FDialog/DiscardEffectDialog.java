package Floor.FType.FDialog;

import Floor.FEntities.FEffect.IOEffect;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.geom.Vec2;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Strings;
import mindustry.entities.Effect;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.ui.dialogs.BaseDialog;

import static Floor.FType.FDialog.DialogUtils.*;
import static mindustry.Vars.ui;


//discard , because of poor applicability , the goal is to get closer to the principle at the first time but now...
//discard by xie100.
abstract class DiscardEffectDialog extends BaseDialog {
    protected IOEffect effect = new IOEffect();
    protected Seq<effectAction> renderers = new Seq<>();
    protected Seq<EAction> all = new Seq<>();
    protected Table base;
    protected Table action;
    protected Table list;
    protected Table now;
    protected String typeNow = "circle";
    protected String cls = "";
    protected float[] acs = new float[10];

    protected static String dia = "effect";

    public DiscardEffectDialog(String title) {
        super(title);
    }

    public DiscardEffectDialog setEffect(IOEffect effect) {
        this.effect = effect;
        for (String v : effect.values) {
            all.add(EAction.in(v));
        }

        buttons.button("@back", Icon.left, this::hide).size(210f, 64f);
        buttons.button(Core.bundle.get("@apply"), Icon.chat, () -> {
            effect.values = loadOut();
            this.effect.renderer = e -> {
                for (effectAction ea : renderers) {
                    ea.get(e);
                }
            };
            hide();
        }).size(210f, 64f);
        shown(this::reb);
        return this;
    }

    public void reb() {
        cont.clear();
        cont.pane(t -> {
            t.row();
            t.table(table -> base = table).grow();
            t.row();
            t.table(table -> action = table).grow();
        }).grow();
        rebuildBase();
        rebuildAction();
    }

    public void rebuildBase() {
        base.clear();
        base.background(Tex.buttonEdge3);
        createNumberDialog(base, dia, "lifetime", effect.lifetime,
                f -> effect.lifetime = f, this::rebuildBase);
        createNumberDialog(base, dia, "startDelay", effect.startDelay,
                f -> effect.startDelay = f, this::rebuildBase);
        base.row();
        createNumberDialog(base, dia, "clip", effect.clip,
                f -> effect.clip = f, this::rebuildBase);
        createNumberDialog(base, dia, "baseRotation", effect.baseRotation,
                f -> effect.baseRotation = f, this::rebuildBase);
        base.row();
        createBooleanDialog(base, dia, "followParent", effect.followParent,
                b -> effect.followParent = b, this::rebuildBase);
        createBooleanDialog(base, dia, "rotWithParent", effect.rotWithParent,
                b -> effect.rotWithParent = b, this::rebuildBase);
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
                createNumberDialog(now, dia, "circleNumber", acs[0], f -> acs[0] = f, this::rebuildNow);
                createNumberDialog(now, dia, "circleLengthFrom", acs[1], f -> acs[1] = f, this::rebuildNow);
                createNumberDialog(now, dia, "circleLengthTo", acs[2], f -> acs[2] = f, this::rebuildNow);
                now.row();
                createNumberDialog(now, dia, "circleRangeFrom", acs[3], f -> acs[3] = f, this::rebuildNow);
                createNumberDialog(now, dia, "circleRangeTo", acs[4], f -> acs[4] = f, this::rebuildNow);
                createNumberDialog(now, dia, "circleAngle", acs[5], f -> acs[5] = f, this::rebuildNow);
                now.row();
                createNumberDialog(now, dia, "circleAngleRand", acs[6], f -> acs[6] = f, this::rebuildNow);
                createNumberDialog(now, dia, "circleMinRange", acs[7], f -> acs[7] = f, this::rebuildNow);
                break;
            }
            case "line": {
                createNumberDialog(now, dia, "lineNumber", acs[0], f -> acs[0] = f, this::rebuildNow);
                createNumberDialog(now, dia, "lineLengthFrom", acs[1], f -> acs[1] = f, this::rebuildNow);
                now.row();
                createNumberDialog(now, dia, "lineLengthTo", acs[2], f -> acs[2] = f, this::rebuildNow);
                createNumberDialog(now, dia, "lineStrokeFrom", acs[3], f -> acs[3] = f, this::rebuildNow);
                now.row();
                createNumberDialog(now, dia, "lineStrokeTo", acs[4], f -> acs[4] = f, this::rebuildNow);
                createNumberDialog(now, dia, "lineRangeFrom", acs[5], f -> acs[5] = f, this::rebuildNow);
                now.row();
                createNumberDialog(now, dia, "lineRangeTo", acs[6], f -> acs[6] = f, this::rebuildNow);
                createNumberDialog(now, dia, "lineAngle", acs[7], f -> acs[7] = f, this::rebuildNow);
                now.row();
                createNumberDialog(now, dia, "lineAngleRand", acs[8], f -> acs[8] = f, this::rebuildNow);
                createNumberDialog(now, dia, "lineMinRange", acs[9], f -> acs[9] = f, this::rebuildNow);
                break;
            }
            case "wave": {
                createNumberDialog(now, dia, "waveStrokeFrom", acs[0], f -> acs[0] = f, this::rebuildNow);
                createNumberDialog(now, dia, "waveStrokeTo", acs[1], f -> acs[1] = f, this::rebuildNow);
                now.row();
                createNumberDialog(now, dia, "waveRangeFrom", acs[2], f -> acs[2] = f, this::rebuildNow);
                createNumberDialog(now, dia, "waveRangeTo", acs[3], f -> acs[3] = f, this::rebuildNow);
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
                            if (c.length() != 6 && c.length() != 8) {
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
                all.add(new EAction(typeNow, acs, cls.isEmpty() ? new String[0] : cls.split(",")));
            } else {
                all.set(index, new EAction(typeNow, acs, cls.isEmpty() ? new String[0] : cls.split(",")));
            }
            rebuildList();
            bd.remove();
            bd.hide();
        }).width(100);
        bd.show();
    }

    public String[] loadOut() {
        renderers.clear();
        String[] allValues = new String[all.size];
        for (int i = 0; i < all.size; i++) {
            EAction a = all.get(i);
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
            allValues[i] = a.out();
        }
        return allValues;
    }

    public static void setColor(String[] colors, float fin) {
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

    public static class EAction {
        String type;
        float[] values;
        String[] colors;

        public EAction(String type, float[] values, String[] colors) {
            this.type = type;
            this.values = values;
            this.colors = colors;
        }

        protected EAction() {
        }

        public String out() {
            return type + "&&" + values() + "&&" + colors();
        }

        public String values() {
            StringBuilder s = new StringBuilder();
            for (int i = 0; i < values.length; i++) {
                if (i == 0) {
                    s.append(values[i]);
                } else {
                    s.append(",").append(values[i]);
                }
            }
            return s.toString();
        }

        public String colors() {
            StringBuilder s = new StringBuilder();
            for (int i = 0; i < colors.length; i++) {
                if (i == 0) {
                    s.append(colors[i]);
                } else {
                    s.append(",").append(colors[i]);
                }
            }
            return s.toString();
        }

        public static EAction in(String s) {
            EAction e = new EAction();
            String[] ss = s.split("&&");
            e.type = ss[0];
            String[] vs = ss[1].split(",");
            e.values = new float[vs.length];
            for (int i = 0; i < vs.length; i++) {
                if (Strings.canParseFloat(vs[i])) {
                    e.values[i] = Strings.parseFloat(vs[i]);
                }
            }
            if (ss.length == 3) {
                e.colors = ss[2].split(",");
            } else {
                e.colors = new String[0];
            }
            return e;
        }
    }
}
