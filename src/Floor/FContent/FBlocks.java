package Floor.FContent;

import Floor.FEntities.FBlock.TimeCore;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;

public class FBlocks {
    public static Block timeCore;

    public static void load() {
        timeCore = new TimeCore("timeCore") {{
            health = 114514;
            armor = 114514;
            itemCapacity = 0;
            unitCapModifier = 0;

            requirements(Category.distribution, new ItemStack[0], true);
        }};
    }
}
