package Floor.FContent;


import Floor.FType.FDialog.MoreResearchDialog;
import arc.Events;
import arc.util.Time;
import mindustry.Vars;
import mindustry.game.EventType;

public class FVarsChange {
    public static void load() {
        Events.on(EventType.ClientLoadEvent.class, e -> Time.runTask(10f, () -> Vars.ui.research = new MoreResearchDialog()));
    }
}
