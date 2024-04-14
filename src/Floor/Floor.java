package Floor;

import Floor.FContent.*;
import mindustry.mod.Mod;

public class Floor extends Mod {
    public Floor() {
    }

    @Override
    public void loadContent() {
        FPlanetGenerators.load();
        FItems.load();
        FCommands.load();
        FEntities.load();
        FStatusEffects.load();
        UnitOverride.load();
        FUnits.load();
        FEvents.load();
        FBlocks.load();
        FPlanets.load();
        TechChange.load();
        PFloors.load();
    }
}