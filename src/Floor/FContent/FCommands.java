package Floor.FContent;

import Floor.FAI.ChainAI;
import Floor.FAI.StrongBoostAI;
import mindustry.ai.UnitCommand;

public class FCommands {
    public static UnitCommand UCD, STB;
    public static void load(){
        UCD = new UnitCommand("UCD", "UCD", u -> new ChainAI()) {{
            switchToMove = false;
        }};
        STB = new UnitCommand("STB","STB",u -> new StrongBoostAI());
    }
}
