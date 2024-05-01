package Floor.FEntities.FUnitType;

import Floor.FAI.ChainAI;
import Floor.FTools.interfaces.ChainAble;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Tmp;
import mindustry.ai.UnitCommand;
import mindustry.entities.Leg;
import mindustry.gen.Legsc;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.world.meta.Stat;

import static Floor.FContent.FCommands.*;


public class ChainUnitType extends UpGradeUnitType {
    private static final Vec2 legOffset = new Vec2();
    public float percent = 15;

    public ChainUnitType(String name) {
        super(name);
        aiController = ChainAI::new;
        commands = new UnitCommand[]{
                UnitCommand.boostCommand,
                UnitCommand.moveCommand,
                UCD
        };
        defaultCommand = UCD;
        riseSpeed = 0.04F;
        canBoost = true;
        allowLegStep = true;
    }

    @Override
    public void drawEngines(Unit unit) {
        if (unit instanceof ChainAble uca) {
            Unit u = uca.UnderUnit();
            if ((uca.upon() && unit.within(u.x, u.y, 4.5F)) || unit.elevation < 0.0001F) {
                return;
            }
        }

        for (var engine : engines) {
            engine.draw(unit);
        }

        Draw.color();
    }

    @Override
    public <T extends Unit & Legsc> void drawLegs(T unit) {
        applyColor(unit);
        Tmp.c3.set(Draw.getMixColor());

        Leg[] legs = unit.legs();

        float level = unit.elevation;

        float ssize = footRegion.width * footRegion.scl() * 1.5f;
        float rotation = unit.baseRotation();
        float invDrown = 1f - unit.drownTime;

        if (footRegion.found()) {
            for (Leg leg : legs) {
                if (level > 0.0001F) {

                    Drawf.shadow(
                            (leg.base.x - unit.x) * (1 - level) + unit.x,
                            (leg.base.y - unit.y) * (1 - level) + unit.y,
                            ssize, invDrown
                    );

                } else Drawf.shadow(leg.base.x, leg.base.y, ssize, invDrown);
            }
        }
        //legs are drawn front first
        for (int j = legs.length - 1; j >= 0; j--) {
            int i = (j % 2 == 0 ? j / 2 : legs.length - 1 - j / 2);
            Leg leg = legs[i];
            boolean flip = i >= legs.length / 2f;
            int flips = Mathf.sign(flip);
            Vec2 position;
            position = unit.legOffset(legOffset, i).add(unit);

            Tmp.v1.set(leg.base).sub(leg.joint).inv().setLength(legExtension);

            if (footRegion.found() && leg.moving && shadowElevation > 0) {
                float scl = shadowElevation * invDrown;
                float elev = Mathf.slope(1f - leg.stage) * scl;
                Draw.color(Pal.shadow);
                if (level > 0.0001F) {
                    Draw.rect(
                            footRegion,
                            (leg.base.x + shadowTX * elev - unit.x) * (1 - level) + unit.x,
                            (leg.base.y + shadowTY * elev - unit.y) * (1 - level) + unit.y,
                            position.angleTo(leg.base)
                    );
                } else {
                    Draw.rect(footRegion, leg.base.x + shadowTX * elev, leg.base.y + shadowTY * elev, position.angleTo(leg.base));
                }
                Draw.color();
            }
            Draw.mixcol(Tmp.c3, Tmp.c3.a);
            if (footRegion.found()) {
                if (level > 0.0001F) {

                    Draw.rect(
                            footRegion,
                            (leg.base.x - unit.x) * (1 - level) + unit.x,
                            (leg.base.y - unit.y) * (1 - level) + unit.y,
                            position.angleTo(leg.base)
                    );

                } else Draw.rect(footRegion, leg.base.x, leg.base.y, position.angleTo(leg.base));
            }

            Lines.stroke(legRegion.height * legRegion.scl() * flips);
            if (level > 0.0001F) {
                Lines.line(
                        legRegion,
                        (position.x - unit.x) * (1 - level) + unit.x,
                        (position.y - unit.y) * (1 - level) + unit.y,
                        (leg.joint.x - unit.x) * (1 - level) + unit.x,
                        (leg.joint.y - unit.y) * (1 - level) + unit.y,
                        false
                );

            } else Lines.line(legRegion, position.x, position.y, leg.joint.x, leg.joint.y, false);

            Lines.stroke(legBaseRegion.height * legRegion.scl() * flips);
            if (level > 0.0001F) {
                Lines.line(
                        legBaseRegion,
                        (leg.joint.x + Tmp.v1.x - unit.x) * (1 - level) + unit.x,
                        (leg.joint.y + Tmp.v1.y - unit.y) * (1 - level) + unit.y,
                        (leg.base.x - unit.x) * (1 - level) + unit.x,
                        (leg.base.y - unit.y) * (1 - level) + unit.y,
                        false
                );

            } else {
                Lines.line(legBaseRegion, leg.joint.x + Tmp.v1.x, leg.joint.y + Tmp.v1.y, leg.base.x, leg.base.y, false);
            }

            if (jointRegion.found()) {
                if (level > 0.0001F) {

                    Draw.rect(
                            jointRegion,
                            (leg.joint.x - unit.x) * (1 - level) + unit.x,
                            (leg.joint.y - unit.y) * (1 - level) + unit.y
                    );

                } else Draw.rect(jointRegion, leg.joint.x, leg.joint.y);
            }
        }
        //base joints are drawn after everything else
        if (baseJointRegion.found()) {
            for (int j = legs.length - 1; j >= 0; j--) {
                //TODO does the index / draw order really matter?
                Vec2 position = unit.legOffset(legOffset, (j % 2 == 0 ? j / 2 : legs.length - 1 - j / 2)).add(unit);
                Draw.rect(baseJointRegion, position.x, position.y, rotation);
            }
        }
        if (baseRegion.found()) {
            Draw.rect(baseRegion, unit.x, unit.y, rotation - 90);
        }
        Draw.reset();
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(new Stat("down_damage"),percent);
    }
}
