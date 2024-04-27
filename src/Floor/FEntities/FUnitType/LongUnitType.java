package Floor.FEntities.FUnitType;

import Floor.FEntities.FUnit.F.LongUnitTogether;
import mindustry.game.Team;
import mindustry.gen.Unit;
import mindustry.type.UnitType;


//just waiting for v8
public class LongUnitType extends UnitType {
    public boolean together = false;
    public int bodyNumber = 3;
    public float hitDamage = 0;
    public float hitInterval = 60;

    public LongUnitType(String name) {
        super(name);
    }

    @Override
    public Unit create(Team team) {
        Unit head = constructor.get();
        LongUnitTogether l = (LongUnitTogether) head;
        l.lastLen = bodyNumber - 1;
        l.toThis = true;

        return head;
    }
}
