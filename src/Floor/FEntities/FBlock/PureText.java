package Floor.FEntities.FBlock;

import Floor.FTools.RangePure;
import Floor.FType.UponFloor.CorrosionMist;
import arc.struct.IntMap;
import arc.struct.IntSeq;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.Tile;

import static mindustry.Vars.world;

public class PureText extends Block {
    public float protectRange = 200;
    public int protectLevel = 1;

    public PureText(String name) {
        super(name);
        update = true;
    }

    public class pureBuild extends Building implements RangePure {
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

        @Override
        public IntMap<Integer> timeBoost() {
            return null;
        }

        @Override
        public IntMap<Integer> withBoost() {
            return null;
        }
    }
}
