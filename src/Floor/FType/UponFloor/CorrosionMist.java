package Floor.FType.UponFloor;

import Floor.FTools.Corrosion;
import arc.struct.IntMap;
import arc.struct.IntSeq;
import arc.util.Time;
import mindustry.world.Tile;

import static arc.util.Time.delta;
import static mindustry.Vars.world;

public class CorrosionMist {
    public final static IntSeq clear = new IntSeq();
    public final static IntMap<Integer> tiles = new IntMap<>();

    public static void init() {
        tiles.clear();
        clear.clear();
        for (Tile t : world.tiles) {
            if (t.floor() instanceof Corrosion) {
                tiles.put(t.pos(), 1);
            }
        }

        Time.run(delta, CorrosionMist::update);
    }

    public static void update() {
        if (world == null) {
            return;
        }

        for (int i = 0; i < tiles.size; i++) {
            int j = tiles.keys().toArray().get(i);
            if (clear.indexOf(j) < 0) {
                Tile t = world.tile(j);
                Corrosion cf = (Corrosion) t.floor();
                float damage = cf.baseDamage() * tiles.get(j);
                if (t.build != null) {
                    t.build.damage(damage);
                }
            }
        }

        Time.run(delta, CorrosionMist::update);
    }
}
