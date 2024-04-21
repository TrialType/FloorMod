package Floor.FEntities.FUnit.Geodetic;

import Floor.FEntities.FUnit.Override.FLegsUnit;
import arc.Core;
import arc.math.Rand;
import arc.scene.ui.Button;
import arc.scene.ui.layout.Cell;
import arc.struct.ObjectMap;
import arc.util.Time;
import mindustry.Vars;
import mindustry.ui.dialogs.BaseDialog;

public class WuMa extends FLegsUnit {
    public static float w, h;
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
        w = Core.scene.getWidth();
        h = Core.scene.getHeight();

        super.update();

        hideTimer += Time.delta;

        for (int i = 0; i < dialogs.size; i++) {
            float timer = dialogs.values().toSeq().get(i);
            BaseDialog dialog = dialogs.keys().toSeq().get(i);
            if (timer - Time.delta <= 0) {
                dialogs.put(dialog, 60f);
                BaseDialog cover = new BaseDialog("");
                cover.setLayoutEnabled(false);
                cover.cont.setLayoutEnabled(false);
                dialogs.put(cover, 60f);

                cover.cont.button(Core.bundle.get("@one"), () -> {
                    dialogs.remove(cover);
                    cover.hide();
                }).setBounds(ra.range(w), ra.range(h), 40, 10);
                cover.show();
            } else {
                dialogs.put(dialog, timer - Time.delta);
            }
        }
    }

    @Override
    public void draw() {
        super.draw();
        if (hideTimer > 60) {
            if (team == Vars.player.team()) {
                BaseDialog cover = new BaseDialog("");
                cover.setLayoutEnabled(false);
                cover.cont.setLayoutEnabled(false);
                dialogs.put(cover, 60f);

                cover.cont.button(Core.bundle.get("@one"), () -> {
                    dialogs.remove(cover);
                    cover.hide();
                }).setBounds(ra.range(w), ra.range(h), 40, 10);
                cover.show();
            }
            hideTimer = 0;
        }
    }
}
