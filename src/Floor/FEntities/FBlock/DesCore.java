package Floor.FEntities.FBlock;

import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.world.Tile;
import mindustry.world.blocks.storage.CoreBlock;

import static mindustry.Vars.state;

public class DesCore extends CoreBlock {
    public DesCore(String name) {
        super(name);
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        if (tile == null) return false;

        CoreBuild core = team.core();

        if (core == null || (!state.rules.infiniteResources && !core.items.has(requirements, state.rules.buildCostMultiplier)))
            return false;

        return ((tile.block() instanceof CoreBlock && size > tile.block().size) || (tile.build == null && team.cores().size < 8)) &&
                (!requiresCoreZone || tempTiles.allMatch(o -> o.floor().allowCorePlacement));
    }

    @Override
    public boolean canBreak(Tile tile) {
        Building b = tile.build;
        if (b != null) {
            return b.team.cores().size > 1 || state.isEditor();
        }
        return false;
    }

    public class DesCoreBuild extends CoreBuild {
    }
}
