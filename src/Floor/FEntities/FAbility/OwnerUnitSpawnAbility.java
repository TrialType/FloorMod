package Floor.FEntities.FAbility;

import Floor.FTools.interfaces.OwnerSpawner;
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

public class OwnerUnitSpawnAbility extends Ability {
    public UnitType unit;
    public float spawnTime, spawnX, spawnY, maxNum = 2;
    public Effect spawnEffect = Fx.spawn;
    public boolean parentizeEffects;

    protected float timer;

    public OwnerUnitSpawnAbility(UnitType unit, float spawnTime, float spawnX, float spawnY) {
        this.unit = unit;
        this.spawnTime = spawnTime;
        this.spawnX = spawnX;
        this.spawnY = spawnY;
    }

    @Override
    public void addStats(Table t) {
        t.add("[lightgray]" + Stat.buildTime.localized() + ": [white]" + Strings.autoFixed(spawnTime / 60f, 2) + " " + StatUnit.seconds.localized());
        t.row();
        t.add(unit.emoji() + " " + unit.localizedName);
    }

    @Override
    public void update(Unit unit) {
        if (unit instanceof OwnerSpawner ss && ss.unit().size < maxNum) {
            timer += Time.delta * state.rules.unitBuildSpeed(unit.team);

            if (timer >= spawnTime && Units.canCreate(unit.team, this.unit)) {
                float x = unit.x + Angles.trnsx(unit.rotation, spawnY, spawnX),
                        y = unit.y + Angles.trnsy(unit.rotation, spawnY, spawnX);
                spawnEffect.at(x, y, 0f, parentizeEffects ? unit : null);
                Unit u = this.unit.create(unit.team);
                u.set(x, y);
                u.rotation = unit.rotation;
                if (u instanceof OwnerSpawner s) {
                    s.spawner(unit);
                }
                Events.fire(new EventType.UnitCreateEvent(u, null, unit));
                if (!Vars.net.client()) {
                    u.add();
                }
                ss.unit().add(u);

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
