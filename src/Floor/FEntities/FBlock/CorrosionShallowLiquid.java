package Floor.FEntities.FBlock;

import Floor.FContent.FStatusEffects;
import Floor.FTools.Corrosion;
import mindustry.world.blocks.environment.ShallowLiquid;

public class CorrosionShallowLiquid extends ShallowLiquid implements Corrosion {
    public float baseDamage = 0.01f;
    public CorrosionShallowLiquid(String name) {
        super(name);
        status = FStatusEffects.corrosionI;
    }

    @Override
    public float baseDamage() {
        return baseDamage;
    }
}
