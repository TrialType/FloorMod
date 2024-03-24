package Floor.FEntities.FAbility;

import Floor.FEntities.FUnit.F.TileMiner;
import Floor.FTools.StrongSpawner;
import arc.Core;
import arc.Events;
import arc.graphics.g2d.Draw;
import arc.math.Angles;
import arc.scene.ui.layout.Table;
import arc.util.Strings;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.abilities.Ability;
import mindustry.game.EventType;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.type.UnitType;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.state;

public class StrongMinerAbility extends Ability {
    private UnitType unit;
    public float spawnTime = 100, spawnX, spawnY;
    public Effect spawnEffect = Fx.spawn;
    public boolean parentizeEffects;

    protected float timer;

    public StrongMinerAbility(UnitType unit, float spawnTime, float spawnX, float spawnY) {
        this.unit = unit;
        this.spawnTime = spawnTime;
        this.spawnX = spawnX;
        this.spawnY = spawnY;
    }

    public StrongMinerAbility() {
    }

    @Override
    public void addStats(Table t) {
        t.add("[lightgray]" + Stat.buildTime.localized() + ": [white]" + Strings.autoFixed(spawnTime / 60f, 2) + " " + StatUnit.seconds.localized());
        t.row();
        t.add(unit.emoji() + " " + unit.localizedName);
    }

    @Override
    public void update(Unit unit) {
        if (unit instanceof StrongSpawner ss && ss.miner().size < 2) {
            timer += Time.delta * state.rules.unitBuildSpeed(unit.team);

            if (timer >= spawnTime && Units.canCreate(unit.team, this.unit)) {
                float x = unit.x + Angles.trnsx(unit.rotation, spawnY, spawnX), y = unit.y + Angles.trnsy(unit.rotation, spawnY, spawnX);
                spawnEffect.at(x, y, 0f, parentizeEffects ? unit : null);
                TileMiner u = (TileMiner) this.unit.create(unit.team);
                u.set(x, y);
                u.rotation = unit.rotation;
                u.spawner = unit;
                Events.fire(new EventType.UnitCreateEvent(u, null, unit));
                if (!Vars.net.client()) {
                    u.add();
                }
                ss.miner().add(u);

                timer = 0f;
            }
        }
    }

    @Override
    public void draw(Unit unit) {
        if (Units.canCreate(unit.team, this.unit)) {
            Draw.draw(Draw.z(), () -> {
                float x = unit.x + Angles.trnsx(unit.rotation, spawnY, spawnX), y = unit.y + Angles.trnsy(unit.rotation, spawnY, spawnX);
                Drawf.construct(x, y, this.unit.fullIcon, unit.rotation - 90, timer / spawnTime, 1f, timer);
            });
        }
    }

    @Override
    public String localized() {
        return Core.bundle.format("ability.unit_spawn.name", unit.localizedName);
    }
}
