package Floor.FEntities.FBlock;

import Floor.FContent.FStatusEffects;
import Floor.FEntities.FUnitType.ENGSWEISUnitType;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Env;

import static java.lang.Math.sqrt;
import static mindustry.Vars.*;

public class KnockingTurret extends Block {
    public int sides = 6;
    public float reload = 600;
    public int maxPower = 15;
    public float radius = 150;
    public float minSpeed = 3F;

    public KnockingTurret(String name) {
        super(name);
        update = true;
        solid = true;
        group = BlockGroup.projectors;
        envEnabled |= Env.space;
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("power", (KnockingTurretBuild entity) -> new Bar(
                "power", Pal.accent,
                () -> (float) entity.power / maxPower
        ));
        addBar("reload", (KnockingTurretBuild entity) -> new Bar(
                "reload", Pal.techBlue,
                () -> Math.min(entity.reloadCounter / reload, 1.0F)
        ));
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);

        Draw.color(Pal.gray);
        Lines.stroke(3f);
        Lines.poly(x * tilesize + offset, y * tilesize + offset, sides, radius / 2, 0);
        Draw.color(player.team().color);
        Lines.stroke(1f);
        Lines.poly(x * tilesize + offset, y * tilesize + offset, sides, radius / 2, 0);
        Draw.color();
    }

    public class KnockingTurretBuild extends Building {
        private int reloadCounter = 0;
        private int power = maxPower;
        public boolean broken = false;
        private float Boost = 0;

        @Override
        public void updateTile() {
            Boost = Mathf.lerpDelta(Boost, 1, 0.1F);
            reloadCounter++;

            if (power > 0) {
                Groups.unit.intersect(x - findRadius()/2, y - findRadius()/2, findRadius(), findRadius(), unit -> {
                    if (unit.team != team && unit.speed() >= minSpeed) {
                        if (unit.type instanceof ENGSWEISUnitType wut && power >= wut.power) {
                            unit.apply(FStatusEffects.StrongStop, 600.0F / wut.defend);
                            power = power - wut.power;
                        } else if (power >= sqrt(unit.hitSize)) {
                            unit.apply(FStatusEffects.StrongStop, (float) (600.0F / sqrt(unit.hitSize)));
                            power = (int) (power - sqrt(unit.speed()));
                        }
                        if (power == 0) {
                            Fx.shieldBreak.at(x, y, radius, team.color);
                            Boost = 0;
                            broken = true;
                        }
                    }
                });
            }

            if (reloadCounter >= reload && power < maxPower) {
                if (power == 0) {
                    Fx.spawnShockwave.at(x, y);
                }
                reloadCounter = 0;
                power++;
                broken = false;
            }
        }

        @Override
        public void draw() {
            super.draw();
            Draw.alpha((float) power / maxPower * 0.75f);
            Draw.z(Layer.blockAdditive);
            Draw.blend(Blending.additive);
            Draw.blend();
            Draw.z(Layer.block);
            Draw.reset();
            drawShield();
        }

        public void drawShield() {
            if (!broken) {
                float r = findRadius();
                if (r > 0.001f) {
                    Draw.color(Color.green, Color.green, Mathf.clamp(0));
                    if (renderer.animateShields) {
                        Draw.z(Layer.shields + 0.001f);
                        Fill.poly(x, y, sides, r / 2, rotation);
                    } else {
                        Draw.z(Layer.shields);
                        Lines.stroke(1.5f);
                        Draw.alpha(0.09f + 0);
                        Fill.poly(x, y, sides, r / 2, rotation);
                        Draw.alpha(1f);
                        Lines.poly(x, y, sides, r / 2, rotation);
                        Draw.reset();
                    }
                }
            }

            Draw.reset();
        }

        public float findRadius() {
            return radius * Boost;
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.bool(broken);
            write.i(power);
            write.i(reloadCounter);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            broken = read.bool();
            power = read.i();
            reloadCounter = read.i();
        }
    }
}
