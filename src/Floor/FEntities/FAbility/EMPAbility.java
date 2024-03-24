package Floor.FEntities.FAbility;

import Floor.FContent.FStatusEffects;
import Floor.FEntities.FUnit.F.ENGSWEISUnitEntity;
import arc.Core;
import arc.math.Angles;
import arc.math.geom.Position;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Strings;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Building;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;


public class EMPAbility extends Ability {
    private float timer = 3600;
    public float reload = 1800;
    public float range = 80;
    public float time = 600;
    public float delay = 60;
    public Effect stopEffect = Fx.lightningShoot;
    public Effect waveEffect = Fx.missileTrailSmoke;
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
                for (Position p : maps) {
                    if (p instanceof Building b) {
                        waveEffect.at(b.x, b.y);
                        b.applySlowdown(0, time + 1);
                    } else if (p instanceof Unit u) {
                        waveEffect.at(u.x, u.y);
                        u.apply(FStatusEffects.StrongStop, time + 1);
                    }
                }
                unit.apply(FStatusEffects.StrongStop, time + 1);
                effectOnly = true;
                start = false;
            }
        } else if (effectOnly) {
            for (Position p : maps) {
                stopEffect.at(p);
            }
            effectOnly = false;
            timer = 0;
        } else if (!(unit instanceof ENGSWEISUnitEntity eu && eu.first) && !unit.hasEffect(FStatusEffects.StrongStop)) {
            timer = timer + Time.delta;
            if (timer >= reload) {
                maps.clear();
                number = 0;
                Units.nearbyEnemies(unit.team, unit.x, unit.y, range, u -> {
                    maps.add(u);
                    number++;
                });
                Units.nearbyBuildings(unit.x, unit.y, range, b -> {
                    if (!(b.team == unit.team)) {
                        maps.add(b);
                        number++;
                    }
                });
                if (number >= 5) {
                    start = true;
                    delayTimer = 0;
                }
            }
        }
    }

    public String localized() {
        return Core.bundle.get("ability." + "emp_ability" + ".name");
    }

    @Override
    public void addStats(Table t) {
        t.add("[lightgray]" + Stat.range.localized() + ": [white]" + range + " " + StatUnit.seconds.localized());
        t.row();
        t.add("[lightgray]" + Core.bundle.get("stats.continue") + ": [white]" + time / 60 + " " + StatUnit.seconds.localized());
        t.row();
        t.add("[lightgray]" + Stat.cooldownTime.localized() + ": [white]" + reload / 60 + " " + StatUnit.seconds.localized());
        t.row();
    }

    @Override
    public void displayBars(Unit unit, Table bars) {
        bars.add(new Bar(Stat.cooldownTime.localized(), Pal.accent, () -> Math.max(timer / reload, 1))).row();
    }
}
