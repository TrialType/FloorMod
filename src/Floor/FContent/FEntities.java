package Floor.FContent;

import Floor.FEntities.FUnit.F.*;
import Floor.FEntities.FUnit.Override.*;
import Floor.FEntities.FBullet.removeSpwanBullet;
import mindustry.gen.EntityMapping;

public class FEntities {
    public static void load(){
        EntityMapping.idMap[99] = removeSpwanBullet::new;
        EntityMapping.idMap[100] = ChainLegUnit::create;
        EntityMapping.idMap[103] = FLegsUnit::create;
        EntityMapping.idMap[104] = FMechUnit::create;
        EntityMapping.idMap[105] = FTankUnit::create;
        EntityMapping.idMap[106] = FUnitEntity::create;
        EntityMapping.idMap[107] = FUnitWaterMove::create;
        EntityMapping.idMap[108] = FPayloadUnit::create;
        EntityMapping.idMap[109] = FElevationMoveUnit::create;
        EntityMapping.idMap[110] = FCrawlUnit::create;
        EntityMapping.idMap[111] = FMechUnitLegacyNova::create;
        EntityMapping.idMap[113] = ENGSWEISUnitEntity::create;
        EntityMapping.idMap[114] = TileMiner::create;
        EntityMapping.idMap[115] = TileSpawnerUnit::create;
    }
}
