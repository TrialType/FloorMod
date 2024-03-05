package Floor.FEntities.FUnitType;

import Floor.FEntities.FUnit.F.TileMiner;
import mindustry.game.Team;
import mindustry.gen.TimedKillc;
import mindustry.type.UnitType;

public class TileMinerUnitType extends UnitType {
    public TileMinerUnitType(String name) {
        super(name);
    }
    public TileMiner create(Team team) {
        TileMiner unit = (TileMiner) constructor.get();
        unit.team = team;
        unit.setType(this);
        unit.ammo = ammoCapacity; //fill up on ammo upon creation
        unit.elevation = flying ? 1f : 0;
        unit.heal();
        if (unit instanceof TimedKillc u) {
            u.lifetime(lifetime);
        }
        return unit;
    }
    @Override
    public void setStats(){
        super.setStats();
    }
}
