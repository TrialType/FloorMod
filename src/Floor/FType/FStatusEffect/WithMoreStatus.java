package Floor.FType.FStatusEffect;

import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;

public class WithMoreStatus extends StatusEffect {
    public final Seq<StatusEffect> with = new Seq<>();
    public boolean sign = false;
    public WithMoreStatus(String name) {
        super(name);
    }
    @Override
    public void update(Unit unit, float time) {
        float boost = 1;

        unit.damageContinuousPierce(Math.abs(damage) * boost);
        if (effect != Fx.none && Mathf.chanceDelta(effectChance)) {
            Tmp.v1.rnd(Mathf.range(unit.type.hitSize / 2f));
            effect.at(unit.x + Tmp.v1.x, unit.y + Tmp.v1.y, 0, color, parentizeEffect ? unit : null);
        }
    }

    public void applied(Unit unit, float time, boolean extend) {
        for (StatusEffect se : with) {
            unit.apply(se, time);
        }
        if (sign) {
            unit.unapply(this);
        }
    }
}
