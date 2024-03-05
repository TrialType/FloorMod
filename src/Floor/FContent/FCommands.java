package Floor.FContent;

import Floor.FAI.ChainAI;
import Floor.FAI.StrongBoostAI;
import mindustry.ai.UnitCommand;

public class FCommands {
    public static UnitCommand ucd, STB;
    public static void load(){
        ucd = new UnitCommand("chain", "chain", u -> new ChainAI()) {{
            switchToMove = false;
        }};
        STB = new UnitCommand("STB","STB",u -> new StrongBoostAI());
    }
}
