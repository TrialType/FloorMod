package Floor.FEntities.FBlock;

import Floor.FTools.RangePure;
import Floor.FType.UponFloor.CorrosionMist;
import arc.math.geom.Point2;
import arc.struct.IntMap;
import arc.struct.IntSeq;
import mindustry.gen.Building;
import mindustry.world.Block;

public class pureText extends Block {
    public float protectRange = 400;
    public int protectLevel = 1;

    public pureText(String name) {
        super(name);
        update = true;
    }

    public class pureBuild extends Building implements RangePure {
        private final IntMap<IntSeq> protects = new IntMap<>();

        @Override
        public void updateTile() {
            if (protects.isEmpty()) {
                CorrosionMist.clearer.add(this);
                IntSeq ps = new IntSeq();
                for (float px = this.x - protectRange / 2; px < this.x + protectRange / 2; px += 1) {
                    for (float py = this.y - protectRange / 2; py < this.y + protectRange / 2; py += 1) {
                        ps.add(Point2.pack((int) px * 8, (int) py * 8));
                    }
                }
                for (int i = 1; i <= protectLevel; i++) {
                    IntSeq add = new IntSeq();
                    add.addAll(ps);
                    protects.put(i, add);
                }
            }
        }

        @Override
        public boolean couldUse() {
            return this.added && this.health > 0 && !this.dead;
        }

        @Override
        public IntMap<IntSeq> protects() {
            return this.protects;
        }
    }
}
