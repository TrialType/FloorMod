package Floor;

import Floor.FContent.*;
import Floor.FType.FDialog.EffectDialog;
import arc.util.Time;
import mindustry.entities.Effect;
import mindustry.mod.Mod;

public class Floor extends Mod {
    public Floor() {
        Time.run(60, () -> {
            Effect test = new Effect();
            new EffectDialog(test, "").show();
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