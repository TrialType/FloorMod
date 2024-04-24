package Floor.FEntities.FUnit.Geodetic;

import Floor.FAI.GeodeticAI.WuAI;
import Floor.FContent.FEvents;
import Floor.FEntities.FUnit.Override.FLegsUnit;
import arc.Core;
import arc.Events;
import arc.math.Angles;
import arc.math.Rand;
import arc.struct.ObjectMap;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.entities.Units;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.ui.dialogs.BaseDialog;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

public class WuMa extends FLegsUnit {
    public boolean isShadow = false;
    public float shadowTimer = 0;

    public static final Rand ra = new Rand();
    public int dialogsNumber = -1;
    public ObjectMap<BaseDialog, Float> dialogs = new ObjectMap<>();
    public float hideTimer = 0;
    public float summonTimer = 3600;

    public static WuMa create() {
        return new WuMa();
    }

    @Override
    public int classId() {
        return 127;
    }

    @Override
    public void update() {
        hideTimer += Time.delta;
        summonTimer += Time.delta;

        if (isShadow) {
            shadowTimer += Time.delta;
            if (shadowTimer >= 3600) {
                shadowTimer = 0;
                kill();
                return;
            }
        }

        if (!isShadow && summonTimer >= 3600 && health / maxHealth <= 0.2f && !dead && health > 0) {
            for (int i = 1; i <= 8; i++) {
                Unit u = type.create(team);
                WuMa wm = (WuMa) u;
                wm.isShadow = true;
                u.set((float) (x + hitSize * Math.cos(Math.toRadians(60 * i))),
                        (float) (y + hitSize * Math.sin(Math.toRadians(60 * i))));
                u.rotation(60 * i);
                u.add();
            }
            summonTimer = 0;
        }

        if (dialogsNumber > 0) {
            for (int i = 0; i < dialogsNumber; i++) {
                createCover();
            }
            dialogsNumber = -1;
        }

        if (speed() > super.speed() * 6) {
            Units.nearbyEnemies(team, x, y, hitSize, u -> {
                if (abs(this.angleTo(u) - rotation) <= 15 && sqrt((x - u.x) * (x - u.x) + (y - u.y) * (y - u.y)) < hitSize / 1.8) {
                    Events.fire(new FEvents.UnitDestroyOtherEvent(this, u));
                    u.kill();
                }
            });

            Units.nearbyBuildings(x, y, hitSize, b -> {
                if (b.team != team && abs(this.angleTo(b) - rotation) <= 5 &&
                        sqrt((x - b.x) * (x - b.x) + (y - b.y) * (y - b.y)) < hitSize / 1.8) {
                    b.kill();
                }
            });
        }

        super.update();

        for (int i = 0; i < dialogs.size; i++) {
            float timer = dialogs.values().toSeq().get(i);
            BaseDialog dialog = dialogs.keys().toSeq().get(i);
            if (timer - Time.delta <= 0) {
                dialogs.put(dialog, 120f);
                createCover();
            } else {
                dialogs.put(dialog, timer - Time.delta);
            }
        }
    }

    public void createCover() {
        BaseDialog cover = new BaseDialog("");
        dialogs.put(cover, 120f);
        int located = ra.nextInt(10) + 2;
        boolean left = located % 2 == 0;
        for (int j = 1; j <= 5; j++) {
            cover.cont.row();
            if (j == located / 2) {
                if (left) {
                    cover.cont.table(t -> t.button(Core.bundle.get("@two"), () -> {
                        dialogs.remove(cover);
                        cover.hide();
                    }).growX()).growX().growY();
                    cover.cont.table(t -> t.button(Core.bundle.get("???妈妈省的???"), () -> {
                    }).growX()).growX().growY();
                } else {
                    cover.cont.table(t -> t.button(Core.bundle.get("???妈妈省的???"), () -> {
                    }).growX()).growX().growY();
                    cover.cont.table(t -> t.button(Core.bundle.get("@two"), () -> {
                        dialogs.remove(cover);
                        cover.hide();
                    }).growX()).growX().growY();
                }
            } else {
                cover.cont.table(t -> t.button("???妈妈省的???", () -> {
                }).growX()).growX().growY();
                cover.cont.table(t -> t.button("???妈妈省的???", () -> {
                }).growX()).growX().growY();
            }
        }
        cover.show();
    }

    @Override
    public void draw() {
        super.draw();
        if (hideTimer > 120) {
            if (!Vars.state.isEditor() && team != Vars.player.team()) {
                createCover();
            }
            hideTimer = 0;
        }
    }

    @Override
    public float speed() {
        float s = super.speed();
        Teamc t;
        if (controller instanceof WuAI wa && (t = wa.hitTarget) != null && t instanceof Unit u) {
            float angle = Angles.angle(x, y, t.x(), t.y());
            if (Math.abs(angle - u.rotation) <= 15) {
                s = s * 8;
            }
        }
        return s;
    }

    @Override
    public void write(Writes write) {
        super.write(write);
        write.i(dialogs.size);
        write.f(hideTimer);
        write.f(summonTimer);
        write.bool(isShadow);
        write.f(shadowTimer);
    }

    @Override
    public void read(Reads read) {
        super.read(read);

        dialogsNumber = read.i();
        hideTimer = read.f();
        summonTimer = read.f();
        isShadow = read.bool();
        shadowTimer = read.f();
    }
}
