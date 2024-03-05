package Floor.FType.FStatusEffect;

import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;

public class ADH extends StatusEffect {

    public ADH(String name) {
        super(name);
    }
    @Override
    public void update(Unit unit, float time){
        if(damage > 0){
            unit.damageContinuousPierce(damage * unit.maxHealth() / 100.0F);
        }else if(damage < 0){ //heal unit
            unit.heal(-1f * damage * unit.maxHealth() * Time.delta / 100.0F);
        }

        if(effect != Fx.none && Mathf.chanceDelta(effectChance)){
            Tmp.v1.rnd(Mathf.range(unit.type.hitSize/2f));
            effect.at(unit.x + Tmp.v1.x, unit.y + Tmp.v1.y, 0, color, parentizeEffect ? unit : null);
        }
    }
}
