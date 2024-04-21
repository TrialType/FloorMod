package Floor.FEntities.FUnit.Geodetic;

import Floor.FEntities.FUnit.Override.FLegsUnit;
import arc.Core;
import arc.math.Rand;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.ui.dialogs.BaseDialog;

public class WuMa extends FLegsUnit {
    public static float w, h;
    public static final Rand ra = new Rand();
    public Seq<Float> cloneTimers = new Seq<>();
    public Seq<BaseDialog> hideDialogs = new Seq<>();
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

        for (int i = 0; i < cloneTimers.size; i++) {
            float timer = cloneTimers.get(i);
            if (timer - Time.delta <= 0) {
                cloneTimers.items[i] = 3f;
                BaseDialog bd = new BaseDialog("");
                bd.cont.button(Core.bundle.get("@three"), bd::hide).
                        setBounds(ra.range(w), ra.range(h), 40, 10);
                hideDialogs.add(bd);
                cloneTimers.add(3f);
                bd.show();
            } else {
                cloneTimers.items[i] = timer - Time.delta;
            }
        }
    }

    @Override
    public void draw() {
        super.draw();
        if (hideTimer > 180) {
//            if (team != Vars.player.team()) {
            BaseDialog hide = new BaseDialog("你好");
            cloneTimers.add(3f);
            hideDialogs.add(hide);
            hide.cont.button(Core.bundle.get("@three"), hide::hide).
                    setBounds(ra.range(w), ra.range(h), 40, 10);
            hide.show();
//            }
            hideTimer = 0;
        }
    }
}
