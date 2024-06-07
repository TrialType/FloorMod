package Floor.FType.FDialog;

import arc.Core;
import arc.func.*;
import arc.scene.ui.layout.Table;
import arc.util.Strings;
import mindustry.entities.pattern.*;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.ui.dialogs.BaseDialog;

import static Floor.FType.FDialog.ProjectUtils.*;
import static mindustry.Vars.ui;

public class ShootDialog extends BaseDialog {
    public String shot = "pattern";
    public ShootPattern shoot;
    public Cons<ShootPattern> apply;
    public Boolp heavy;
    public Boolf<String> use;
    public Runnable heavyUp;
    public Table base, type, flo;
    protected static String dia = "shoot";

    public ShootDialog(String title, Prov<ShootPattern> def, Cons<ShootPattern> apply, Boolf<String> couldUse, Boolp heavyIn, Runnable heavyUp) {
        super(title);

        setShoot(def.get());
        this.apply = apply;
        use = couldUse;
        heavy = heavyIn;
        this.heavyUp = heavyUp;
        shown(this::rebuild);

        buttons.button("@back", Icon.left, this::hide).width(100);
        buttons.button(Core.bundle.get("@apply"), Icon.right, () -> {
            apply.get(shoot);
            heavyUp.run();
            if (!heavy.get()) {
                ui.showInfo(Core.bundle.get("@tooHeavy"));
            } else {
                hide();
            }
        }).width(100);
    }

    public void rebuild() {
        cont.clear();
        cont.pane(t -> {
            t.table(ta -> {
                ta.setBackground(Tex.buttonEdge1);

                ta.label(() -> Core.bundle.get("dialog.shoot." + shot)).width(200);
                ta.button(b -> {
                    b.image(Icon.rotate);

                    b.clicked(() -> createSelectDialog(b, (tb, hide) -> {
                        tb.button(Core.bundle.get("dialog.shoot.pattern"), () -> {
                            if (!shot.equals("pattern")) {
                                setShoot(new ShootPattern() {{
                                    shots = shoot.shots;
                                    firstShotDelay = shoot.firstShotDelay;
                                    shotDelay = shoot.shotDelay;
                                }});
                            }
                            rebuildType();
                            hide.run();
                        }).width(200).row();
                        tb.button(Core.bundle.get("dialog.shoot.summon"), () -> {
                            if (!shot.equals("summon")) {
                                setShoot(new ShootSummon(0, 0, 0, 0) {{
                                    shots = shoot.shots;
                                    firstShotDelay = shoot.firstShotDelay;
                                    shotDelay = shoot.shotDelay;
                                }});
                            }
                            rebuildType();
                            hide.run();
                        }).width(200).row();
                        tb.button(Core.bundle.get("dialog.shoot.spread"), () -> {
                            if (!shot.equals("spread")) {
                                setShoot(new ShootSpread() {{
                                    shots = shoot.shots;
                                    firstShotDelay = shoot.firstShotDelay;
                                    shotDelay = shoot.shotDelay;
                                }});
                            }
                            rebuildType();
                            hide.run();
                        }).width(200).row();
                        tb.button(Core.bundle.get("dialog.shoot.sine"), () -> {
                            if (!shot.equals("sine")) {
                                setShoot(new ShootSine() {{
                                    shots = shoot.shots;
                                    firstShotDelay = shoot.firstShotDelay;
                                    shotDelay = shoot.shotDelay;
                                }});
                            }
                            rebuildType();
                            hide.run();
                        }).width(200).row();
                        tb.button(Core.bundle.get("dialog.shoot.multi"), () -> {
                            if (!shot.equals("multi")) {
                                setShoot(new ShootMulti() {{
                                    shots = shoot.shots;
                                    firstShotDelay = shoot.firstShotDelay;
                                    shotDelay = shoot.shotDelay;
                                }});
                            }
                            rebuildType();
                            hide.run();
                        }).width(200).row();
                        tb.button(Core.bundle.get("dialog.shoot.helix"), () -> {
                            if (!shot.equals("helix")) {
                                setShoot(new ShootHelix() {{
                                    shots = shoot.shots;
                                    firstShotDelay = shoot.firstShotDelay;
                                    shotDelay = shoot.shotDelay;
                                }});
                            }
                            rebuildType();
                            hide.run();
                        }).width(200).row();
                        tb.button(Core.bundle.get("dialog.shoot.barrel"), () -> {
                            if (!shot.equals("barrel")) {
                                setShoot(new ShootBarrel() {{
                                    shots = shoot.shots;
                                    firstShotDelay = shoot.firstShotDelay;
                                    shotDelay = shoot.shotDelay;
                                }});
                            }
                            rebuildType();
                            hide.run();
                        }).width(200).row();
                        tb.button(Core.bundle.get("dialog.shoot.alternate"), () -> {
                            if (!shot.equals("alternate")) {
                                setShoot(new ShootAlternate() {{
                                    shots = shoot.shots;
                                    firstShotDelay = shoot.firstShotDelay;
                                    shotDelay = shoot.shotDelay;
                                }});
                            }
                            rebuildType();
                            hide.run();
                        }).width(200).row();
                    }));
                }, () -> {
                }).pad(5);
            }).width(1400);
            t.row();
            t.table(b -> base = b).width(1400).row();
            t.table(b -> type = b).width(1400);
            rebuildBase();
            rebuildType();
        }).width(1400);
    }

    public void rebuildBase() {
        base.clear();
        base.setBackground(Tex.buttonEdge1);
        createLevDialog(base, dia, "number", "shots", shoot.shots, f -> shoot.shots = (int) (f + 0),
                this::rebuildBase, heavyUp, use, heavy);
        createNumberDialog(base, dia, "shotDelay", shoot.shotDelay,
                f -> shoot.shotDelay = f, this::rebuildBase);
        createNumberDialog(base, dia, "firstShotDelay", shoot.firstShotDelay,
                f -> shoot.firstShotDelay = f, this::rebuildBase);
    }

    public void rebuildType() {
        type.clear();
        type.setBackground(Tex.buttonEdge1);
        switch (shot) {
            case "summon": {
                ShootSummon ss = (ShootSummon) shoot;
                createNumberDialog(type, dia, "x", ss.x,
                        f -> ss.x = f, this::rebuildType);
                createNumberDialog(type, dia, "y", ss.y,
                        f -> ss.y = f, this::rebuildType);
                createNumberDialog(type, dia, "radius", ss.radius,
                        f -> ss.radius = f, this::rebuildType);
                createNumberDialog(type, dia, "spread", ss.spread,
                        f -> ss.spread = f, this::rebuildType);
                break;
            }
            case "spread": {
                ShootSpread ss = (ShootSpread) shoot;
                createNumberDialog(type, dia, "spread1", ss.spread,
                        f -> ss.spread = f, this::rebuildType);
                break;
            }
            case "sine": {
                ShootSine ss = (ShootSine) shoot;
                createNumberDialog(type, dia, "scl", ss.scl,
                        f -> ss.scl = f, this::rebuildType);
                createNumberDialog(type, dia, "mag", ss.mag,
                        f -> ss.mag = f, this::rebuildType);
                break;
            }
            case "multi": {
                ShootMulti sm = (ShootMulti) shoot;
                createShootList(type, dia, "dest", () -> sm.dest, s -> sm.dest = s, () -> use.get("number"), heavy);
                createShootDialog(type, dia, "", getHeavy("number", getShootVal(sm.source)), () -> sm.source,
                        s -> sm.source = s, s -> couldUse("number", getShootVal(sm.source)), heavy, heavyUp);
                break;
            }
            case "helix": {
                ShootHelix sh = (ShootHelix) shoot;
                createNumberDialog(type, dia, "scl", sh.scl,
                        f -> sh.scl = f, this::rebuildType);
                createNumberDialog(type, dia, "mag", sh.mag,
                        f -> sh.mag = f, this::rebuildType);
                createNumberDialog(type, dia, "offset", sh.offset,
                        f -> sh.offset = f, this::rebuildType);
                break;
            }
            case "barrel": {
                ShootBarrel sb = new ShootBarrel();
                createNumberDialog(type, dia, "offset", sb.barrelOffset,
                        f -> sb.barrelOffset = (int) (f + 0), this::rebuildType);
                type.table(t -> {
                    t.label(() -> Core.bundle.get("dialog.shoot.barrels")).width(100);
                    t.button(Icon.pencil, () -> {
                        float[] n = new float[sb.barrels.length];
                        System.arraycopy(sb.barrels, 0, n, 0, sb.barrels.length);
                        BaseDialog bd = new BaseDialog("");
                        bd.buttons.button("@back", Icon.left, () -> {
                            sb.barrels = n;
                            bd.hide();
                        }).width(100);
                        bd.buttons.button(Core.bundle.get("@apply"), Icon.right, bd::hide).width(100);
                        bd.buttons.button(Core.bundle.get("@add"), Icon.add, () -> {
                            float[] nn = new float[sb.barrels.length + 3];
                            System.arraycopy(sb.barrels, 0, nn, 0, n.length);
                            sb.barrels = nn;
                            rebuildFloatList(sb.barrels, f -> sb.barrels = f);
                        }).width(100);
                        bd.pane(tb -> flo = tb).width(1400);
                        rebuildFloatList(sb.barrels, f -> sb.barrels = f);
                        bd.show();
                    });
                });
                break;
            }
            case "alternate": {
                ShootAlternate sa = (ShootAlternate) shoot;
                createNumberDialog(type, dia, "barrels2", sa.barrels,
                        f -> sa.barrels = (int) (f + 0), this::rebuildType);
                createNumberDialog(type, dia, "spread2", sa.spread,
                        f -> sa.spread = f, this::rebuildType);
                createNumberDialog(type, dia, "barrelOffset2", sa.barrelOffset,
                        f -> sa.barrelOffset = (int) (f + 0), this::rebuildType);
            }
        }
    }

    public void rebuildFloatList(float[] values, Cons<float[]> apply) {
        flo.clear();
        for (int i = 0; i * 3 < values.length; i++) {
            int finalI = i;
            flo.table(t -> {
                t.label(() -> Core.bundle.get("dialog.shoot.shootX") + ":" + values[finalI * 3]).width(200);
                t.label(() -> Core.bundle.get("dialog.shoot.shootY") + ":" + values[finalI * 3 + 1]).width(200);
                t.label(() -> Core.bundle.get("dialog.shoot.shootRo") + ":" + values[finalI * 3 + 2]).width(200);
                t.button(Icon.pencil, () -> ui.showTextInput(Core.bundle.get("dialog.shoot.input"), Core.bundle.get("dialog.shoot.input"),
                        values[finalI * 3] + "," + values[finalI * 3 + 1] + "," + values[finalI * 3 + 2], str -> {
                            String[] s = str.split(",");
                            for (int j = 0; j < 3; j++) {
                                if (Strings.canParseFloat(s[j])) {
                                    values[finalI * 3 + j] = Float.parseFloat(s[j]);
                                } else {
                                    ui.showInfo(Core.bundle.get("@inputError"));
                                    return;
                                }
                            }
                            apply.get(values);
                            rebuildFloatList(values, apply);
                        })).size(25).pad(5);
                t.button(Icon.trash, () -> {
                    float[] n = new float[values.length - 3];
                    for (int j = 0, k = 0; j * 3 < values.length; j++) {
                        if (j != finalI) {
                            n[k * 3] = values[j * 3];
                            n[k * 3 + 1] = values[j * 3 + 1];
                            n[k * 3 + 2] = values[j * 3 + 2];
                            k++;
                        }
                    }
                    apply.get(n);
                    rebuildFloatList(n, apply);
                }).size(25).pad(5);
            });
            flo.row();
        }
    }

    public void setShoot(ShootPattern shoot) {
        if (shoot instanceof ShootSummon ss) {
            this.shoot = new ShootSummon(ss.x, ss.y, ss.radius, ss.spread);
            this.shoot.firstShotDelay = ss.firstShotDelay;
            this.shoot.shotDelay = ss.shotDelay;
            this.shoot.shots = ss.shots;
            shot = "summon";
        } else if (shoot instanceof ShootSpread ss) {
            this.shoot = new ShootSpread(ss.shots, ss.spread);
            this.shoot.firstShotDelay = ss.firstShotDelay;
            this.shoot.shotDelay = ss.shotDelay;
            shot = "spread";
        } else if (shoot instanceof ShootSine ss) {
            this.shoot = new ShootSine(ss.scl, ss.mag);
            this.shoot.firstShotDelay = ss.firstShotDelay;
            this.shoot.shotDelay = ss.shotDelay;
            this.shoot.shots = ss.shots;
            shot = "sine";
        } else if (shoot instanceof ShootMulti sm) {
            this.shoot = new ShootMulti(sm.source == null ? new ShootPattern() : sm.source, sm.dest);
            this.shoot.firstShotDelay = sm.firstShotDelay;
            this.shoot.shotDelay = sm.shotDelay;
            this.shoot.shots = sm.shots;
            shot = "multi";
        } else if (shoot instanceof ShootHelix sh) {
            ShootHelix s = new ShootHelix();
            s.scl = sh.scl;
            s.mag = sh.mag;
            s.offset = sh.offset;
            s.firstShotDelay = sh.firstShotDelay;
            s.shotDelay = sh.shotDelay;
            s.shots = sh.shots;
            this.shoot = s;
            shot = "helix";
        } else if (shoot instanceof ShootBarrel sb) {
            ShootBarrel s = new ShootBarrel();
            s.barrelOffset = sb.barrelOffset;
            s.barrels = sb.barrels.clone();
            s.firstShotDelay = sb.firstShotDelay;
            s.shotDelay = sb.shotDelay;
            s.shots = sb.shots;
            this.shoot = s;
            shot = "barrel";
        } else if (shoot instanceof ShootAlternate sa) {
            ShootAlternate s = new ShootAlternate();
            s.barrels = sa.barrels;
            s.barrelOffset = sa.barrelOffset;
            s.spread = sa.spread;
            s.firstShotDelay = sa.firstShotDelay;
            s.shotDelay = sa.shotDelay;
            s.shots = sa.shots;
            this.shoot = s;
            shot = "alternate";
        } else {
            this.shoot = new ShootPattern();
            this.shoot.firstShotDelay = shoot.firstShotDelay;
            this.shoot.shotDelay = shoot.shotDelay;
            this.shoot.shots = shoot.shots;
            shot = "pattern";
        }
    }
}
