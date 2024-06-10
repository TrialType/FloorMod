package Floor;

import Floor.FContent.*;
import Floor.FEntities.FBulletType.EMPLarge;
import Floor.FEntities.FBulletType.LimitBulletType;
import Floor.FType.FDialog.ProjectUtils;
import mindustry.mod.ClassMap;
import mindustry.mod.Mod;

public class Floor extends Mod {
    public Floor() {
        ProjectUtils.init();
        ClassMap.classes.put("EMPLarge", EMPLarge.class);
        ClassMap.classes.put("LimitBulletType", LimitBulletType.class);
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