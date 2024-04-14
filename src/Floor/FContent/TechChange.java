package Floor.FContent;

import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.content.Planets;
import mindustry.content.TechTree;
import mindustry.game.Objectives;
import mindustry.type.ItemStack;

public class TechChange {
    public static TechTree.TechNode tn;
    public static TechTree.TechNode head;

    public static void load() {
        head = Planets.serpulo.techTree;
        head.each(t -> tn = t.content == Blocks.scorch ? t : tn);
        TechTree.TechNode tf = new TechTree.TechNode(tn, FBlocks.fireStream, ItemStack.empty);
        new TechTree.TechNode(tf, FBlocks.fireBoost, ItemStack.empty);
    }
}
