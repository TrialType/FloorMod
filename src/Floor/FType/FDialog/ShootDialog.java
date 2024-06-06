package Floor.FType.FDialog;

import arc.Core;
import arc.func.Boolp;
import arc.func.Cons;
import arc.func.Prov;
import arc.scene.ui.layout.Table;
import mindustry.entities.pattern.*;
import mindustry.gen.Icon;
import mindustry.ui.dialogs.BaseDialog;

import static Floor.FType.FDialog.ProjectUtils.*;

public class ShootDialog extends BaseDialog {
    public String shot = "pattern";
    public ShootPattern shoot;
    public Cons<ShootPattern> apply;
    public Boolp use, heavy;
    public Table base, type;
    protected static String dia = "shoot";

    public ShootDialog(String title, Prov<ShootPattern> def, Cons<ShootPattern> apply, Boolp couldUse, Boolp heavyIn) {
        super(title);

        setShoot(def.get());
        this.apply = apply;
        use = couldUse;
        heavy = heavyIn;
        shown(this::rebuild);
    }

    public void rebuild() {
        cont.clear();
        cont.pane(t -> {
            t.label(() -> Core.bundle.get("dialog.shoot.") + shot).width(200);
            t.button(b -> {
                b.image(Icon.pencil);

                createSelectDialog(b, (tb, hide) -> {
                    tb.button(Core.bundle.get("dialog.shoot.pattern"), () -> {
                        if (!shot.equals("pattern")) {
                            setShoot(new ShootPattern() {{
                                shots = shoot.shots;
                                firstShotDelay = shoot.firstShotDelay;
                                shotDelay = shoot.shotDelay;
                            }});
                        }
                        hide.run();
                    }).width(200).row();
                    tb.button(Core.bundle.get("dialog.shoot.summon"), () -> {
                        if (shot.equals("summon")) {
                            setShoot(new ShootSummon(0, 0, 0, 0) {{
                                shots = shoot.shots;
                                firstShotDelay = shoot.firstShotDelay;
                                shotDelay = shoot.shotDelay;
                            }});
                        }
                        hide.run();
                    }).width(200).row();
                    tb.button(Core.bundle.get("dialog.shoot.spread"), () -> {
                        if (shot.equals("spread")) {
                            setShoot(new ShootSpread() {{
                                shots = shoot.shots;
                                firstShotDelay = shoot.firstShotDelay;
                                shotDelay = shoot.shotDelay;
                            }});
                        }
                        hide.run();
                    }).width(200).row();
                    tb.button(Core.bundle.get("dialog.shoot.sine"), () -> {
                        if (shot.equals("sine")) {
                            setShoot(new ShootSine() {{
                                shots = shoot.shots;
                                firstShotDelay = shoot.firstShotDelay;
                                shotDelay = shoot.shotDelay;
                            }});
                        }
                        hide.run();
                    }).width(200).row();
                    tb.button(Core.bundle.get("dialog.shoot.helix"), () -> {
                        if (shot.equals("helix")) {
                            setShoot(new ShootHelix() {{
                                shots = shoot.shots;
                                firstShotDelay = shoot.firstShotDelay;
                                shotDelay = shoot.shotDelay;
                            }});
                        }
                        hide.run();
                    }).width(200).row();
                    tb.button(Core.bundle.get("dialog.shoot.barrel"), () -> {
                        if (shot.equals("barrel")) {
                            setShoot(new ShootBarrel() {{
                                shots = shoot.shots;
                                firstShotDelay = shoot.firstShotDelay;
                                shotDelay = shoot.shotDelay;
                            }});
                        }
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
                        hide.run();
                    }).width(200).row();
                });
            }, () -> {
            }).pad(5);
            t.row();
            t.table(b -> base = b).width(1400);
            t.table(b -> type = b).width(1400);
        }).width(1400);
    }

    public void rebuildBase() {

    }

    public void rebuildType() {

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
            this.shoot = new ShootMulti(sm.source, sm.dest);
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
