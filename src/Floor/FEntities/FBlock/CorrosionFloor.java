package Floor.FEntities.FBlock;

import Floor.FContent.FStatusEffects;
import Floor.FTools.Corrosion;
import mindustry.type.StatusEffect;
import mindustry.world.blocks.environment.Floor;

public class CorrosionFloor extends Floor implements Corrosion {
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
}
