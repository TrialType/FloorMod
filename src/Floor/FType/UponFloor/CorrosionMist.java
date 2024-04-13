package Floor.FType.UponFloor;

import Floor.FTools.Corrosion;
import arc.struct.IntMap;
import arc.struct.IntSeq;
import arc.struct.ObjectSet;
import arc.util.Time;
import mindustry.Vars;
import mindustry.entities.Units;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;

import static arc.util.Time.delta;
import static mindustry.Vars.*;

public class CorrosionMist {
    public final static IntSeq clear = new IntSeq();
    public final static IntMap<Integer> boost = new IntMap<>();
    public static boolean update = false;

    public static void init() {
        boost.clear();
        clear.clear();
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
        if (world == null || Vars.editor.isLoading()) return;

        Units.nearby(0, 0, world.width() * 8, world.height() * 8, u -> {
            Tile t = u.tileOn();
            if (t != null && clear.indexOf(t.pos()) < 0) {
                Floor f = t.floor();
                if (f instanceof Corrosion) {
                    u.apply(f.status, 60);
                }
            }
        });

        indexer.allBuildings(world.width() * 4, world.height() * 4, Math.max(world.width(), world.height()) * 6, b -> {
            Tile t = world.tileWorld(b.x, b.y);
            if (t != null && clear.indexOf(t.pos()) < 0) {
                Floor f = t.floor();
                if (f instanceof Corrosion c) {
                    Integer bo = boost.get(t.pos());
                    if (bo == null) {
                        bo = 1;
                    }

                    b.damage(Math.max(0.5f / 15, b.maxHealth() / c.baseDamage()) * bo * 15);
                }
            }
        });

        Time.run(delta * 15, CorrosionMist::update);
    }
}
