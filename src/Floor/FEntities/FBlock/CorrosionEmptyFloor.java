package Floor.FEntities.FBlock;

import mindustry.content.Blocks;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;

public class CorrosionEmptyFloor extends CorrosionFloor{
    public CorrosionEmptyFloor(String name){
        super(name);
        variants = 0;
        canShadow = false;
        placeableOn = false;
        solid = true;
    }

    @Override
    public void drawBase(Tile tile){
        drawEdges(tile);

        Floor floor = tile.overlay();
        if(floor != Blocks.air && floor != this){
            floor.drawBase(tile);
        }
    }
}
