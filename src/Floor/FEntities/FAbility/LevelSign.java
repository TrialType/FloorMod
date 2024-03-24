package Floor.FEntities.FAbility;

import Floor.FEntities.FUnitType.UpGradeUnitType;
import Floor.FTools.FUnitUpGrade;
import arc.Core;
import arc.graphics.Color;
import arc.scene.ui.layout.Table;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;

public class LevelSign extends Ability {
    public LevelSign() {
        display = false;
    }

    @Override
    public void displayBars(Unit unit, Table table) {
        if (unit instanceof FUnitUpGrade uug && !(unit.type instanceof UpGradeUnitType)) {
            table.row();

            table.add(new Bar(String.valueOf(uug.getLevel()), Pal.coalBlack, () -> 1f));
            table.row();

            table.add(new Bar(Core.bundle.get("bar.up_grade"), Color.black,
                    () -> uug.getExp() / ((4 + uug.getLevel()) * unit.maxHealth / 10)).blink(Color.white));
            table.row();

            table.add(new Bar(Core.bundle.get("bar.health"), Color.green, () -> uug.getHealthLevel() / 10.0f).blink(Color.white));
            table.row();

            table.add(new Bar(Core.bundle.get("bar.damage"), Color.red, () -> uug.getDamageLevel() / 10.0f).blink(Color.white));
            table.row();

            table.add(new Bar(Core.bundle.get("bar.reload"), Color.gray, () -> uug.getReloadLevel() / 10.0f).blink(Color.white));
            table.row();

            table.add(new Bar(Core.bundle.get("bar.speed"), Color.blue, () -> uug.getSpeedLevel() / 10.0f).blink(Color.white));
            table.row();

            table.add(new Bar(Core.bundle.get("bar.again"), Color.orange, () -> uug.getAgainLevel() / 10.0f).blink(Color.white));
            table.row();

            table.add(new Bar(Core.bundle.get("bar.shield"), Color.yellow, () -> uug.getShieldLevel() / 10.0f).blink(Color.white));
            table.row();
        }
    }
}
