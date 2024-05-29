package Floor;

import Floor.FContent.*;
import Floor.FEntities.FBulletType.EMPLarge;
import mindustry.mod.ClassMap;
import mindustry.mod.Mod;

public class Floor extends Mod {
    public Floor() {
        ClassMap.classes.put("EMPLarge", EMPLarge.class);
    }

    @Override
    public void loadContent() {
        FSettings.load();
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