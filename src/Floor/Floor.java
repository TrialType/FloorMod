package Floor;

import Floor.FContent.*;
import Floor.FType.FDialog.BulletDialog;
import arc.Events;
import arc.util.Time;
import mindustry.game.EventType;
import mindustry.mod.Mod;

public class Floor extends Mod {
    public Floor() {
        Time.run(60, () -> {
            BulletDialog bd = new BulletDialog(null, "");
            bd.show();
            Events.on(EventType.UnitCreateEvent.class, e -> {
                if (e.unit != null) {
                    bd.bullet.create(e.unit, e.unit.x, e.unit.y, 0);
                }
            });
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