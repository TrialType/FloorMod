package Floor.FType.FStatusEffect;

import arc.Events;
import arc.math.Mathf;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.game.EventType;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;

public class Again extends StatusEffect {
    private Unit Cu;
    private boolean couldAgain = true;
    private boolean dead = false;

    public Again(String name) {
        super(name);
    }

    @Override
    public void update(Unit unit, float time) {
        if (unit == null || (unit.dead && !dead)) {
            if (couldAgain) {
                couldAgain = false;
                Unit u = Cu.type.create(Cu.team);
                u.set(Cu.x, Cu.y);
                u.rotation = Cu.rotation;
                Fx.spawn.at(Cu.x, Cu.y, Cu.rotation, Cu);
                Events.fire(new EventType.UnitCreateEvent(u, null, null));
                if (!Vars.net.client()) {
                    u.add();
                }
            }
        } else {
            dead = unit.dead;
            Cu = unit.type.create(unit.team);

            Cu.x(unit.x());
            Cu.y(unit.y());
            Cu.rotation(unit.rotation());
        }

        if (effect != Fx.none && Mathf.chanceDelta(effectChance) && unit != null) {
            Tmp.v1.rnd(Mathf.range(unit.type.hitSize / 2f));
            effect.at(unit.x + Tmp.v1.x, unit.y + Tmp.v1.y, 0, color, parentizeEffect ? unit : null);
        }
    }
}