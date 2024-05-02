package Floor.FTools.classes;

import Floor.FEntities.FUnit.F.TileMiner;
import arc.math.Mathf;
import arc.struct.IntSeq;
import arc.struct.ObjectIntMap;
import arc.struct.Seq;
import mindustry.gen.Nulls;
import mindustry.type.Item;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.AirBlock;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static mindustry.Vars.*;

public class FLocated {
    public static final Map<Tile, TileMiner> tm = new HashMap<>();
    public static IntSeq[][][] ores;
    public static ObjectIntMap<Item> allOres;
    public static final int quadrantSize = 20;
    public static int quadWidth, quadHeight;
    private static int index = -1;

    private FLocated() {
    }

    public static boolean couldMine(TileMiner unit, Tile tile) {
        update();
        return tm.get(tile) == unit || tm.get(tile) == null;
    }

    public static void update() {
        Seq<Tile> ts = new Seq<>();
        for (Tile t : tm.keySet()) {
            if (tm.get(t).dead || tm.get(t).health() <= 0 || tm.get(t) == Nulls.unit) {
                ts.add(t);
            }
        }
        for (Tile t : ts) {
            tm.remove(t);
        }
    }

    public static Tile findOre(TileMiner sm, Item item) {
        get();
        if (ores[item.id] != null) {
            float minDst = 0f;
            Tile closest = null;
            for (int qx = 0; qx < quadWidth; qx++) {
                for (int qy = 0; qy < quadHeight; qy++) {
                    var arr = ores[item.id][qx][qy];
                    if (arr != null && arr.size > 0) {
                        for (int i = 0; i < arr.size; i++) {
                            Tile tile = world.tile(arr.get(i));
                            if (tile.block() instanceof AirBlock && couldMine(sm, tile)) {
                                float dst = Mathf.dst2(sm.x, sm.y, tile.worldx(), tile.worldy());
                                if (closest == null || dst < minDst) {
                                    closest = tile;
                                    minDst = dst;
                                }
                            }
                        }
                    }
                }
            }
            if (closest == null) {
                int si = sm.type.mineItems.indexOf(item);
                if (index < 0) {
                    index = si;
                }
                if (si == index - 1 || si == sm.type.mineItems.size - 1 && index == 0) {
                    index = -1;
                    return null;
                }
                if (si == sm.type.mineItems.size - 1) {
                    closest = findOre(sm, sm.type.mineItems.get(0));
                } else {
                    closest = findOre(sm, sm.type.mineItems.get(si + 1));
                }
            }
            return closest;
        }
        return null;
    }

    public static void removeOre(Tile tile) {
        get();
        Item item = tile.drop();
        if (ores[item.id] != null) {
            int qx = (tile.x / FLocated.quadrantSize);
            int qy = (tile.y / FLocated.quadrantSize);
            ores[item.id][qx][qy].removeValue(tile.pos());
            allOres.increment(item, -1);
        }
    }

    public static boolean hasOre(Item item) {
        get();
        return allOres.get(item) > 0;
    }

    @SuppressWarnings("unchecked")
    public static void get() {
        Field field1;
        Field field2;
        Field field3;
        Field field4;
        try {
            field1 = indexer.getClass().getDeclaredField("ores");
            field2 = indexer.getClass().getDeclaredField("allOres");
            field3 = indexer.getClass().getDeclaredField("quadWidth");
            field4 = indexer.getClass().getDeclaredField("quadHeight");
            field1.setAccessible(true);
            field2.setAccessible(true);
            field3.setAccessible(true);
            field4.setAccessible(true);
            ores = (IntSeq[][][]) field1.get(indexer);
            allOres = (ObjectIntMap<Item>) field2.get(indexer);
            quadWidth = (int) field3.get(indexer);
            quadHeight = (int) field4.get(indexer);
        } catch (NoSuchFieldException | IllegalAccessException | ClassCastException e) {
            throw new RuntimeException(e);
        }
    }
}
