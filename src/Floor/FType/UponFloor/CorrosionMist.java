package Floor.FType.UponFloor;

import Floor.FEntities.FBlock.CorrosionFloor;
import arc.struct.IntMap;
import arc.struct.IntSeq;
import arc.util.Time;
import mindustry.world.Tile;

import static mindustry.Vars.world;

public class CorrosionMist {
    public final static IntSeq clear = new IntSeq();
    public final static IntMap<Integer> tiles = new IntMap<>();

    public static void init() {
        for (Tile t : world.tiles) {
            if (t.floor() instanceof CorrosionFloor) {
                tiles.put(t.pos(), 1);
            }
        }

        Time.run(60, CorrosionMist::update);
    }

    public static void update() {
        if (world == null) {
            return;
        }

        for (int i = 0; i < tiles.size; i++) {
            int j = tiles.keys().toArray().get(i);
            if (clear.indexOf(j) < 0) {
                Tile t = world.tile(j);
                CorrosionFloor cf = (CorrosionFloor) t.floor();
                float boost = cf.baseDamage * tiles.get(j);
                if (t.build != null) {
                    t.build.damage(boost);
                }
            }
        }

        Time.run(60, CorrosionMist::update);
    }
}
