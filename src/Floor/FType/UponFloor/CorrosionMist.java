package Floor.FType.UponFloor;

import Floor.FTools.Corrosion;
import Floor.FTools.RangePure;
import arc.struct.IntMap;
import arc.struct.IntSeq;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.entities.Units;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;

import static arc.util.Time.delta;
import static mindustry.Vars.*;

public class CorrosionMist {
    public final static Seq<RangePure> clearer = new Seq<>();
    public final static IntMap<IntSeq> clear = new IntMap<>();
    public final static IntMap<Float> boost = new IntMap<>();
    public static boolean update = false;

    public static void init() {
        boost.clear();
        clearer.clear();
        update = false;
        world.tiles.eachTile(t -> {
            if (!update) {
                if (t.floor() instanceof Corrosion) {
                    update = true;
                }
            }
        });

        if (update) {
            Time.run(delta * 15, CorrosionMist::update);
        }
    }

    public static void update() {
        if (world == null || state.isEditor()) return;

        IntSeq removes = new IntSeq();
        for (int i = 0; i < boost.size; i++) {
            int key = boost.keys().toArray().get(i);
            if (boost.get(key) - 15 * delta <= 0) {
                removes.add(key);
            } else {
                boost.put(key, boost.get(i) - 1);
            }
        }
        for (int i : removes.toArray()) {
            boost.remove(i);
        }

        clear.clear();
        clearer.removeAll(r -> !r.couldUse());
        for (RangePure rp : clearer) {
            IntMap<IntSeq> its = rp.protects();
            for (int i : its.keys().toArray().toArray()) {
                if (clear.containsKey(i)) {
                    clear.get(i).addAll(its.get(i));
                } else {
                    clear.put(i, its.get(i));
                }
            }
        }
        Units.nearby(0, 0, world.width() * 8, world.height() * 8, u -> {
            Tile t = u.tileOn();
            if (t != null && clear.keys().toArray().indexOf(t.pos()) < 0) {
                Floor f = t.floor();
                if (f instanceof Corrosion) {
                    Float bo = boost.get(t.pos());
                    if (bo == null) {
                        bo = 1f;
                    }
                    if (clear.get((int) (bo + 1)).indexOf(t.pos()) < 0) {
                        u.apply(f.status, 60);
                    }
                }
            }
        });

        indexer.allBuildings(world.width() * 4, world.height() * 4, Math.max(world.width(), world.height()) * 6, b -> {
            Tile t = world.tileWorld(b.x, b.y);
            if (t != null) {
                Floor f = t.floor();
                if (f instanceof Corrosion c) {
                    Float bo = boost.get(t.pos());
                    if (bo == null) {
                        bo = 1f;
                    }
                    if (clear.get((int) (bo + 1)).indexOf(t.pos()) < 0) {
                        b.damage(Math.max(0.5f / 15, b.maxHealth() / c.baseDamage()) * bo * 15);
                    }
                }
            }
        });

        Time.run(delta * 15, CorrosionMist::update);
    }
}
