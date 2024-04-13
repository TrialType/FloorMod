package Floor.FType.FStatusEffect;

import Floor.FContent.FStatusEffects;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;

public class corrosionStatus extends StatusEffect {
    public final Seq<StatusEffect> with = new Seq<>();
    public float damageBoost = 1;

    @Override
    public void init() {
        transitions.put(FStatusEffects.pureA, (u, r, t) -> {
            u.unapply(this);
        });
        transitions.put(FStatusEffects.pureT, (u, r, t) -> {
            u.unapply(this);
        });

        if (initblock != null) {
            initblock.run();
        }
    }

    public corrosionStatus(String name) {
        super(name);
    }

    @Override
    public void update(Unit unit, float time) {
        float boost = 1;

        for (corrosionStatus cs : FStatusEffects.catalyze) {
            if (unit.hasEffect(cs)) {
                boost *= cs.damageBoost;
            }
        }

        if (with != null && with.size > 0) {
            for (StatusEffect s : with) {
                unit.apply(s, 2);
            }
        }

        unit.damageContinuousPierce(Math.abs(damage) * boost);
        if (effect != Fx.none && Mathf.chanceDelta(effectChance)) {
            Tmp.v1.rnd(Mathf.range(unit.type.hitSize / 2f));
            effect.at(unit.x + Tmp.v1.x, unit.y + Tmp.v1.y, 0, color, parentizeEffect ? unit : null);
        }
    }
}
