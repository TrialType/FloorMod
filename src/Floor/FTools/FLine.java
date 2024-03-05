package Floor.FTools;

import Floor.FEntities.FUnit.F.TileMiner;
import arc.math.Mathf;
import arc.struct.IntSeq;
import arc.struct.ObjectIntMap;
import arc.struct.Seq;
import mindustry.content.Blocks;
import mindustry.gen.Nulls;
import mindustry.type.Item;
import mindustry.world.Tile;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static mindustry.Vars.indexer;
import static mindustry.Vars.world;

public class FLine {
    public static final Map<Tile, TileMiner> tm = new HashMap<>();
    public static IntSeq[][][] ores;
    public static ObjectIntMap<Item> allOres = new ObjectIntMap<>();
    public static final int quadrantSize = 20;
    public static int quadWidth, quadHeight;
    private static int index = -1;

    public FLine() {
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
        if(ores == null || allOres == null){
            get();
        }
        if (ores[item.id] != null && ores[item.id].length != 0) {
            float minDst = 0f;
            Tile closest = null;
            for (int qx = 0; qx < quadWidth; qx++) {
                for (int qy = 0; qy < quadHeight; qy++) {
                    var arr = ores[item.id][qx][qy];
                    if (arr != null && arr.size > 0) {
                        for (int i = 0; i < arr.size; i++) {
                            Tile tile = world.tile(arr.get(i));
                            if (tile.block() == Blocks.air && couldMine(sm, tile)) {
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
        if(ores == null || allOres == null){
            get();
        }
        Item item = tile.drop();
        if (ores[item.id] != null) {
            int qx = (tile.x / FLine.quadrantSize);
            int qy = (tile.y / FLine.quadrantSize);
            ores[item.id][qx][qy].removeValue(tile.pos());
            allOres.increment(item, -1);
        }
    }

    public static boolean hasOre(Item item) {
        if(ores == null || allOres == null){
            get();
        }
        return allOres.get(item) > 0;
    }

    public static void get() {
        try {
            Field field1 = indexer.getClass().getDeclaredField("ores");
            Field field2 = indexer.getClass().getDeclaredField("allOres");
            field1.setAccessible(true);
            field2.setAccessible(true);
            Object obj = field2.get(indexer);
            ores = (IntSeq[][][]) field1.get(indexer);
            allOres = (ObjectIntMap<Item>) obj;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
