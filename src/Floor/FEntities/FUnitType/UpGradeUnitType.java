package Floor.FEntities.FUnitType;

import Floor.FTools.interfaces.FUnitUpGrade;
import arc.Core;
import arc.graphics.Color;
import arc.scene.ui.layout.Table;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.type.*;
import mindustry.ui.Bar;


public class UpGradeUnitType extends UnitType {
    public UpGradeUnitType(String name) {
        super(name);
    }

    @Override
    public void display(Unit unit, Table table) {
        super.display(unit, table);
        if (unit instanceof FUnitUpGrade uug) {
            table.row();
            table.table(dis -> {
                dis.defaults().growX().height(20).pad(5);

                dis.add(new Bar(String.valueOf(uug.getLevel()), Pal.coalBlack, () -> 1f));
                dis.row();

                dis.add(new Bar(Core.bundle.get("bar.up_grade"), Pal.thoriumPink,
                        () -> uug.getExp() / ((4 + uug.getLevel()) * unit.maxHealth / 10)).blink(Color.white));
                dis.row();

                dis.add(new Bar(Core.bundle.get("bar.health"), Color.green, () -> uug.getHealthLevel() / 10.0f).blink(Color.white));
                dis.row();

                dis.add(new Bar(Core.bundle.get("bar.damage"), Color.red, () -> uug.getDamageLevel() / 10.0f).blink(Color.white));
                dis.row();

                dis.add(new Bar(Core.bundle.get("bar.reload"), Color.gray, () -> uug.getReloadLevel() / 10.0f).blink(Color.white));
                dis.row();

                dis.add(new Bar(Core.bundle.get("bar.speed"), Color.blue, () -> uug.getSpeedLevel() / 10.0f).blink(Color.white));
                dis.row();

                dis.add(new Bar(Core.bundle.get("bar.again"), Color.orange, () -> uug.getAgainLevel() / 10.0f).blink(Color.white));
                dis.row();

                dis.add(new Bar(Core.bundle.get("bar.shield"), Color.yellow, () -> uug.getShieldLevel() / 10.0f).blink(Color.white));
                dis.row();
            }).growX();
            table.row();
        }
    }
}
