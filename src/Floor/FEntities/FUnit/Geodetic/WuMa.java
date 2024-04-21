package Floor.FEntities.FUnit.Geodetic;

import Floor.FEntities.FUnit.Override.FLegsUnit;
import arc.Core;
import arc.math.Rand;
import arc.struct.ObjectMap;
import arc.util.Time;
import mindustry.Vars;
import mindustry.ui.dialogs.BaseDialog;

public class WuMa extends FLegsUnit {
    public static final Rand ra = new Rand();
    public ObjectMap<BaseDialog, Float> dialogs = new ObjectMap<>();
    public float hideTimer = 0;

    public static WuMa create() {
        return new WuMa();
    }

    @Override
    public int classId() {
        return 127;
    }

    @Override
    public void update() {
        super.update();

        hideTimer += Time.delta;

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
                    cover.cont.table(t -> t.button(Core.bundle.get("妈妈省的"), () -> {
                    }).growX()).growX().growY();
                } else {
                    cover.cont.table(t -> t.button(Core.bundle.get("妈妈省的"), () -> {
                    }).growX()).growX().growY();
                    cover.cont.table(t -> t.button(Core.bundle.get("@two"), () -> {
                        dialogs.remove(cover);
                        cover.hide();
                    }).growX()).growX().growY();
                }
            } else {
                cover.cont.table(t -> t.button("妈妈省的", () -> {
                }).growX()).growX().growY();
                cover.cont.table(t -> t.button("妈妈省的", () -> {
                }).growX()).growX().growY();
            }
        }
        cover.show();
    }

    @Override
    public void draw() {
        super.draw();
        if (hideTimer > 120) {
            if (team != Vars.player.team()) {
                createCover();
            }
            hideTimer = 0;
        }
    }
}
