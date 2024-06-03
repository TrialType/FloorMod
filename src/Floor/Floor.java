package Floor;

import Floor.FContent.*;
import Floor.FEntities.FBulletType.EMPLarge;
import Floor.FType.FDialog.WeaponDialog;
import arc.util.Time;
import mindustry.mod.ClassMap;
import mindustry.mod.Mod;
import mindustry.type.Weapon;

public class Floor extends Mod {
    public Floor() {
        ClassMap.classes.put("EMPLarge", EMPLarge.class);
        Time.run(600, () -> new WeaponDialog("", new Weapon(), w -> {
        }, f -> {
        }).show());
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