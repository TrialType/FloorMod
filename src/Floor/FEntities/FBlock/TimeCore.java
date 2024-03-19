package Floor.FEntities.FBlock;

import arc.util.Time;
import mindustry.Vars;
import mindustry.world.blocks.storage.CoreBlock;

public class TimeCore extends CoreBlock {
    public TimeCore(String name) {
        super(name);

        solid = solidifes = false;
        teamPassable = false;
        underBullets = true;
        targetable = false;
        canOverdrive = false;
        destructible = false;
        itemCapacity = 0;
        unitCapModifier = 0;
        size = 1;
    }

    public class TimeCoreBuild extends CoreBuild {
        private float killTimer = 0;

        @Override
        public void updateTile() {
            super.updateTile();
            if (Vars.state.wave >= Vars.state.rules.winWave) {
                killTimer += Time.delta;
                if (killTimer >= 120) {
                    kill();
                    remove();
                }
            }
        }

        @Override
        public void damage(float damage) {
        }
    }
}
