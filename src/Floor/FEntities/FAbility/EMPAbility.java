package Floor.FEntities.FAbility;

import Floor.FContent.FStatusEffects;
import Floor.FEntities.FUnit.F.ENGSWEISUnitEntity;
import arc.math.geom.Position;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;


public class EMPAbility extends Ability {
    private float timer = 3600;
    public float reload = 1800;
    public float range = 80;
    public float time = 600;
    public float delay = 900;
    public Effect stopEffect = Fx.none;
    public Effect waveEffect = Fx.healWave;
    private static int number;
    private float delayTimer = 0;
    private boolean effectOnly = false;
    private boolean start = false;
    private final Seq<Position> maps = new Seq<>();

    @Override
    public void update(Unit unit) {
        if (start) {
            delayTimer += Time.delta;
            if (delayTimer >= delay) {
                Units.nearbyBuildings(unit.x, unit.y, range, b -> b.applyBoost(0, time + 1));
                Units.nearby(null, unit.x, unit.y, range, u -> u.apply(FStatusEffects.StrongStop, time + 1));
                unit.apply(FStatusEffects.StrongStop, time + 1);
                waveEffect.at(unit);
                effectOnly = true;
                start = false;
            }
        } else if (effectOnly) {
            for (Position p : maps) {
                stopEffect.at(p);
            }
        } else if (!(unit instanceof ENGSWEISUnitEntity eu && eu.first)) {
            timer = timer + Time.delta;
            if (timer >= reload) {
                maps.clear();
                Units.closestTarget(unit.team, unit.x, unit.y, range, u -> {
                    maps.add(u);
                    number++;
                    return false;
                }, b -> {
                    maps.add(b);
                    number++;
                    return false;
                });
                if (number >= 5) {
                    start = true;
                    delayTimer = 0;
                }
            }
        } else {
            timer = reload;
        }
    }
}
