package Floor.FContent;

import Floor.FEntities.FBlock.CorrosionFloor;
import mindustry.world.blocks.environment.Floor;
import mindustry.content.Liquids;
import mindustry.graphics.CacheLayer;

public class PFloors {
    public static Floor pDeepwater;
    public static void load(){
        pDeepwater = new CorrosionFloor("p-deep-water"){{
            baseDamage = 0.001f;
            speedMultiplier = 0.15f;
            variants = 0;
            liquidDrop = Liquids.water;
            liquidMultiplier = 1.5f;
            isLiquid = true;
            status = FStatusEffects.pWet;
            statusDuration = 120f;
            drownTime = 200f;
            cacheLayer = CacheLayer.water;
            albedo = 0.9f;
            supportsOverlay = true;
        }};
    }
}