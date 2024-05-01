package Floor.FEntities.FBlock;

import Floor.FTools.interfaces.Corrosion;
import mindustry.type.StatusEffect;
import mindustry.world.blocks.environment.Floor;

public class CorrosionFloor extends Floor implements Corrosion {
    public float corrosionLevel = 1;
    public float damagePercent = 5400;

    public CorrosionFloor(String name) {
        super(name);
    }

    public CorrosionFloor(String name, StatusEffect effect) {
        super(name);
        status = effect;
    }

    public CorrosionFloor(String name, int variants, StatusEffect effect) {
        super(name, variants);
        status = effect;
    }

    @Override
    public float baseDamage() {
        return damagePercent;
    }
    @Override
    public float corrosionLevel() {
        return corrosionLevel;
    }

}
