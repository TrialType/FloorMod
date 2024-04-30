package Floor;

import Floor.FContent.*;
import arc.Core;
import arc.struct.IntSeq;
import mindustry.mod.Mod;

public class Floor extends Mod {
    public Floor() {
    }

    @Override
    public void loadContent() {

        Core.settings.putJson("floor-stronger-core", new IntSeq(new int[]{32}));
        FLiquids.load();
        FItems.load();
        FCommands.load();
        FEntities.load();
        FStatusEffects.load();
        UnitOverride.load();
        FUnits.load();
        FEvents.load();
        FBlocks.load();
        FPlanets.load();
        PFloors.load();
        FPlanetGenerators.load();
        Techs.load();
    }
}