package Floor.FAI;

import Floor.FEntities.FUnit.F.TileMiner;
import Floor.FTools.classes.FLocated;
import Floor.FTools.interfaces.NeedPoseBridge;
import arc.math.geom.Vec2;
import arc.struct.IntSeq;
import mindustry.entities.units.AIController;
import mindustry.gen.Unit;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.AirBlock;
import mindustry.world.blocks.environment.Floor;

import static mindustry.Vars.world;

public class TilePutAI extends AIController implements NeedPoseBridge {
    protected Vec2 pose = null;
    protected TileMiner tm = null;

    public TilePutAI(Unit unit) {
        this.unit = unit;
    }
    @Override
    public void updateMovement() {
        if (tm != null && pose != null) {
            moveTo(pose, 0.1F);
            if (unit.within(pose, unit.hitSize / 2)) {
                Floor[] items = tm.tiles;
                int index = -1;
                if (items[0] != null) {
                    index = 0;
                } else if (items[1] != null) {
                    index = 1;
                }
                Tile tile = world.tileWorld(pose.x, pose.y);
                if (tile != null && tile.overlay().itemDrop == null && index >= 0 && tile.block() instanceof AirBlock) {
                    tile.setOverlay(items[index]);
                    tile.overlay().drawBase(tile);
                    IntSeq is = FLocated.ores[items[index].itemDrop.id][tile.x / FLocated.quadrantSize][tile.y / FLocated.quadrantSize];
                    if (is == null) {
                        is = FLocated.ores[items[index].itemDrop.id][tile.x / FLocated.quadrantSize][tile.y / FLocated.quadrantSize] = new IntSeq(false, 16);
                    }
                    is.add(tile.pos());
                    FLocated.allOres.increment(items[index].itemDrop, 1);
                    items[index] = null;
                }
            }
        } else {
            if (unit instanceof TileMiner) {
                tm = (TileMiner) unit;
            }
        }
    }

    @Override
    public void setPose(Vec2 vec2) {
        pose = vec2;
    }

    @Override
    public void init() {
        if (unit instanceof TileMiner) {
            this.tm = (TileMiner) unit;
        }
    }
}
