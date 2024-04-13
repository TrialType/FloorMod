package Floor.FEntities.FBlock;

import Floor.FContent.FStatusEffects;
import Floor.FTools.Corrosion;
import mindustry.world.blocks.environment.Floor;

public class CorrosionFloor extends Floor implements Corrosion {
    public float baseDamage = 1f;

    public CorrosionFloor(String name) {
        super(name);
        status = FStatusEffects.corrosionI;
    }
    public CorrosionFloor(String name, int variants){
        super(name,variants);
    }

    @Override
    public float baseDamage() {
        return baseDamage;
    }
}
