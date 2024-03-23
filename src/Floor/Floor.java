package Floor;

import Floor.FContent.*;
import Floor.FTools.FLine;
import mindustry.mod.Mod;

public class Floor extends Mod {
    public Floor() {
    }

    @Override
    public void loadContent() {
        FItems.load();
        FCommands.load();
        FEntities.load();
        FStatusEffects.load();
        UnitOverride.load();
        FUnits.load();
        FEvents.load();
        FDefends.load();
        FBlocks.load();
    }
}