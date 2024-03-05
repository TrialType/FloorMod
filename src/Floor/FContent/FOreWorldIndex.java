package Floor.FContent;

import arc.Events;
import arc.math.Mathf;
import arc.math.geom.QuadTree;
import arc.math.geom.Rect;
import arc.struct.IntSeq;
import arc.struct.Seq;
import mindustry.ai.BlockIndexer;
import mindustry.content.Blocks;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.logic.Ranged;
import mindustry.world.Tile;
import mindustry.world.meta.BlockFlag;

import static mindustry.Vars.*;
import static mindustry.Vars.world;

public class FOreWorldIndex {
    private static final int quadrantSize = 20;
    private static int quadWidth, quadHeight;

    /** Stores all ore quadrants on the map. Maps ID to qX to qY to a list of tiles with that ore. */
    private static IntSeq[][][] ores;
    public static void load(){
        Events.on(EventType.WorldLoadEvent .class, event -> {
            ores = new IntSeq[content.items().size][][];
            quadWidth = Mathf.ceil(world.width() / (float)quadrantSize);
            quadHeight = Mathf.ceil(world.height() / (float)quadrantSize);

            //so WorldLoadEvent gets called twice sometimes... ugh
            for(Team team : Team.all){
                var data = state.teams.get(team);
                if(data != null){
                    if(data.buildingTree != null) data.buildingTree.clear();
                    if(data.turretTree != null) data.turretTree.clear();
                }
            }

            for(Tile tile : world.tiles){

                var drop = tile.drop();

                if(drop != null){
                    int qx = (tile.x / quadrantSize);
                    int qy = (tile.y / quadrantSize);

                    //add position of quadrant to list
                    if(tile.block() == Blocks.air){
                        if(ores[drop.id] == null){
                            ores[drop.id] = new IntSeq[quadWidth][quadHeight];
                        }
                        if(ores[drop.id][qx][qy] == null){
                            ores[drop.id][qx][qy] = new IntSeq(false, 16);
                        }
                        ores[drop.id][qx][qy].add(tile.pos());
                    }
                }
            }
        });
    }
}
