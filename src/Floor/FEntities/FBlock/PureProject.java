package Floor.FEntities.FBlock;

import Floor.FTools.interfaces.RangePure;
import Floor.FType.UponFloor.CorrosionMist;
import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.struct.IntMap;
import arc.struct.IntSeq;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.meta.Stat;

import static mindustry.Vars.world;

public class PureProject extends Block {
    public float protectRange = 400;
    public int protectLevel = 6;

    public PureProject(String name) {
        super(name);
        update = true;
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(new Stat("pure-range"), protectRange);
        stats.add(new Stat("pure-max-power"), protectLevel);
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("power", build -> new Bar(
                () -> Core.bundle.format("stat.pure-power", (int) (build.efficiency * protectLevel)) +
                        ":" + (int) (build.efficiency * protectLevel),
                () -> Pal.breakInvalid,
                () -> build.efficiency));
    }

    public class PureBuild extends Building implements RangePure {
        private final IntMap<Integer> protects = new IntMap<>();

        @Override
        public void updateTile() {
            if (protects.isEmpty()) {
                CorrosionMist.changer.add(this);
                IntSeq ps = new IntSeq();
                for (float px = this.x - protectRange / 2; px <= this.x + protectRange / 2; px += 1) {
                    for (float py = this.y - protectRange / 2; py <= this.y + protectRange / 2; py += 1) {
                        Tile t = world.tileWorld(px, py);
                        if (t != null) {
                            ps.add(t.pos());
                        }
                    }
                }
                for (int i : ps.toArray()) {
                    protects.put(i, protectLevel);
                }
            }
            if (!protects.isEmpty()) {
                IntSeq ins = protects.keys().toArray();
                for (int i : ins.toArray()) {
                    protects.put(i, (int) Math.max(0, efficiency * protectLevel));
                }
            }
        }

        @Override
        public void draw() {
            super.draw();
            Draw.z(Layer.shields);
            Draw.alpha(0.07f);
            Lines.stroke(2);
            Fill.circle(x, y, protectRange / 2);
        }

        @Override
        public int plan() {
            return 1;
        }

        @Override
        public boolean couldUse() {
            return this.added && this.health > 0 && !this.dead;
        }

        @Override
        public IntMap<Integer> protects() {
            return this.protects;
        }
    }
}
