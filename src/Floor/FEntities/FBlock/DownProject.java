package Floor.FEntities.FBlock;

import arc.Core;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.blocks.defense.OverdriveProjector;
import mindustry.world.consumers.ConsumeItems;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatValues;

import static mindustry.Vars.indexer;

public class DownProject extends OverdriveProjector {
    public float downSpeed = 0.1f;
    public float speedDownPhase = 0.1f;

    public DownProject(String name) {
        super(name);
    }

    @Override
    public void setStats() {
        stats.timePeriod = useTime;
        super.setStats();
        stats.remove(Stat.speedIncrease);
        stats.add(Stat.speedIncrease, "-" + (int) (downSpeed * 100f) + "%");

        if (hasBoost && findConsumer(f -> f instanceof ConsumeItems) instanceof ConsumeItems items) {
            stats.remove(Stat.booster);
            stats.add(Stat.booster, StatValues.itemBoosters("-{0}%", stats.timePeriod, speedDownPhase * 100f, phaseRangeBoost, items.items, this::consumesItem));
        }
    }

    @Override
    public void setBars() {
        super.setBars();
        removeBar("boost");
        addBar("slow", (DownProjectBuild entity) -> new Bar(() -> Core.bundle.format("bar.slow", Mathf.round(Math.max((entity.realDown() * 100), 0))), () -> Pal.accent, entity::realDown));
    }

    public class DownProjectBuild extends OverdriveBuild {
        @Override
        public void updateTile() {
            smoothEfficiency = Mathf.lerpDelta(smoothEfficiency, efficiency, 0.08f);
            heat = Mathf.lerpDelta(heat, efficiency > 0 ? 1f : 0f, 0.08f);
            charge += heat * Time.delta;

            if (hasBoost) {
                phaseHeat = Mathf.lerpDelta(phaseHeat, speedDownPhase, 0.1f);
            }

            if (charge >= reload) {
                float realRange = range + phaseHeat * phaseRangeBoost;

                charge = 0f;
                indexer.eachBlock(this, realRange, other -> other.block.canOverdrive, other -> other.applySlowdown(realDown(), reload + 1f));
            }

            if (timer(timerUse, useTime) && efficiency > 0) {
                consume();
            }
        }

        public float realDown() {
            return Math.max(0, (1 - (downSpeed + phaseHeat * speedDownPhase) * efficiency));
        }
    }
}
