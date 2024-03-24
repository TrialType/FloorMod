package Floor.FContent;

import Floor.FEntities.FBlock.GradeFactory;
import arc.Core;
import mindustry.content.Items;
import mindustry.type.Category;
import mindustry.world.Block;

import static mindustry.type.ItemStack.with;

public class FBlocks {
    public static Block outPowerFactory, inputPowerFactory;

    public static void load() {
        outPowerFactory = new GradeFactory("out_power_factory") {{
            requirements(Category.units, with(Items.copper, 2000, Items.lead, 2000, Items.silicon, 2000));

            itemCapacity = 100 * 10;
            size = 8;
            consumePower(500f);

            constructTime = 60f * 10f;
        }};
        inputPowerFactory = new GradeFactory("input_power_factory") {{
            requirements(Category.units, with(Items.copper, 2000, Items.lead, 2000, Items.silicon, 2000));

            itemCapacity = 100 * 10;
            size = 8;
            consumePower(500f);

            constructTime = 60f * 10f;
            out = false;
        }};
    }
}
