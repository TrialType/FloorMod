package Floor.FContent;

import Floor.FEntities.FBlock.GradeFactory;
import Floor.FTools.UnitUpGrade;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.type.Category;
import mindustry.type.UnitType;
import mindustry.world.Block;

import static mindustry.type.ItemStack.with;

public class FBlocks {
    public static Block outPowerFactory, inputPowerFactory;

    public static void load() {
        outPowerFactory = new GradeFactory("outPowerFactory") {{
            requirements(Category.units, with(Items.copper, 200, Items.lead, 120, Items.silicon, 90));

            itemCapacity = 100 * 20;
            size = 9;
            consumePower(500f);

            constructTime = 60f * 10f;

            for (UnitType ut : UnitUpGrade.uppers) {
                grades.add(ut);
            }
        }};
        inputPowerFactory = new GradeFactory("inputPowerFactory") {{
            requirements(Category.units, with(Items.copper, 200, Items.lead, 120, Items.silicon, 90));

            itemCapacity = 100 * 20;
            size = 9;
            consumePower(500f);

            constructTime = 60f * 10f;
            out = false;

            for (UnitType ut : UnitUpGrade.uppers) {
                grades.add(ut);
            }
        }};
    }
}
