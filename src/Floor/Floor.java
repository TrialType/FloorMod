package Floor;

import Floor.FContent.*;
import Floor.FType.FDialog.GradeBulletDialog;
import arc.Core;
import arc.struct.IntSeq;
import arc.util.Time;
import mindustry.mod.Mod;

public class Floor extends Mod {
    public Floor() {
        Time.run(60, () -> {
            new GradeBulletDialog("").show();
        });
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