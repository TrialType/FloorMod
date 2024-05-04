package Floor;

import Floor.FContent.*;
import Floor.FType.FDialog.BulletDialog;
import Floor.FType.FDialog.ProjectsLocated;
import arc.util.Time;
import mindustry.mod.Mod;

public class Floor extends Mod {
    public Floor() {
        Time.run(60, () -> {
            new ProjectsLocated("");
            new BulletDialog(null, "").show();
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