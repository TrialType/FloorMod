package Floor.FEntities.FEffectState;

import Floor.FEntities.FEffect.WaterWave;
import arc.graphics.Color;
import mindustry.entities.EntityGroup;
import mindustry.gen.EffectState;

public class WaterEffectState extends EffectState {
    private boolean last = false;

    protected WaterEffectState() {
        this.color = new Color(Color.white);
        this.id = EntityGroup.nextId();
        this.index__all = -1;
        this.index__draw = -1;
    }

    public static WaterEffectState create() {
        return new WaterEffectState();
    }

    @Override
    public int classId() {
        return 98;
    }

    @Override
    public void update() {
        if (last != WaterWave.back) {
            if (!(time == 0)) {
                time = lifetime - time;
            }
        }
        last = WaterWave.back;
        super.update();
    }
}
