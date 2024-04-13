package Floor.FContent;

import Floor.FEntities.FBlock.CorrosionEmptyFloor;
import Floor.FEntities.FBlock.CorrosionFloor;
import Floor.FEntities.FBlock.CorrosionShallowLiquid;
import Floor.FEntities.FBlock.CorrosionSteamVent;
import arc.graphics.Color;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.gen.Sounds;
import mindustry.world.blocks.environment.Floor;
import mindustry.content.Liquids;
import mindustry.graphics.CacheLayer;
import mindustry.world.blocks.environment.ShallowLiquid;
import mindustry.world.meta.Attribute;

public class PFloors {
    public static Floor pDeepwater, pWater, pTaintedWater, pDeepTaintedWater, pTar, pCryofluid, pSlag, pStone, pCraters, pCharr, pBasalt, pHotrock, pMagmarock,
            pSand, pDarksand, pDirt, pMud, pEmpty, pDacite, pRhyolite, pRhyoliteCrater, pRoughRhyolite, pRegolith, pYellowStone, pCarbonStone,
            pFerricStone, pFerricCraters, pBeryllicStone, pCrystallineStone, pCrystalFloor, pYellowStonePlates, pRedStone, pDenseRedStone,
            pRedIce, pArkyciteFloor, pArkyicStone, pRhyoliteVent, pCarbonVent, pArkyicVent, pYellowStoneVent, pRedStoneVent, pCrystallineVent,
            pRedmat, pBluemat, pGrass, pSalt, pSnow, pIce, pIceSnow, pShale, pMoss, pCoreZone, pSporeMoss;
    public static Floor pDarksandTaintedWater, pSandWater, pDarksandWater;

    public static void load() {
        pDeepwater = new CorrosionFloor("p-deep-water") {{
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

        pWater = new CorrosionFloor("p-shallow-water") {{
            speedMultiplier = 0.5f;
            variants = 0;
            status = FStatusEffects.pWet;
            statusDuration = 90f;
            liquidDrop = Liquids.water;
            isLiquid = true;
            cacheLayer = CacheLayer.water;
            albedo = 0.9f;
            supportsOverlay = true;
        }};

        pTaintedWater = new CorrosionFloor("p-tainted-water") {{
            speedMultiplier = 0.5f;
            variants = 0;
            status = FStatusEffects.pWet;
            statusDuration = 90f;
            liquidDrop = Liquids.water;
            isLiquid = true;
            cacheLayer = CacheLayer.water;
            albedo = 0.9f;
            attributes.set(Attribute.spores, 0.15f);
            supportsOverlay = true;
        }};

        pDeepTaintedWater = new CorrosionFloor("p-deep-tainted-water") {{
            speedMultiplier = 0.18f;
            variants = 0;
            status = FStatusEffects.pWet;
            statusDuration = 140f;
            drownTime = 200f;
            liquidDrop = Liquids.water;
            isLiquid = true;
            cacheLayer = CacheLayer.water;
            albedo = 0.9f;
            attributes.set(Attribute.spores, 0.15f);
            supportsOverlay = true;
        }};

        pDarksandTaintedWater = new CorrosionShallowLiquid("p-darksand-tainted-water") {{
            speedMultiplier = 0.75f;
            statusDuration = 60f;
            albedo = 0.9f;
            attributes.set(Attribute.spores, 0.1f);
            supportsOverlay = true;
        }};

        pSandWater = new CorrosionShallowLiquid("p-sand-water") {{
            speedMultiplier = 0.8f;
            statusDuration = 50f;
            albedo = 0.9f;
            supportsOverlay = true;
        }};

        pDarksandWater = new CorrosionShallowLiquid("p-darksand-water") {{
            speedMultiplier = 0.8f;
            statusDuration = 50f;
            albedo = 0.9f;
            supportsOverlay = true;
        }};

        pTar = new CorrosionFloor("p-tar") {{
            drownTime = 230f;
            status = FStatusEffects.pTarred;
            statusDuration = 240f;
            speedMultiplier = 0.19f;
            variants = 0;
            liquidDrop = Liquids.oil;
            isLiquid = true;
            cacheLayer = CacheLayer.tar;
        }};

        pCryofluid = new CorrosionFloor("p-pooled-cryofluid") {{
            drownTime = 150f;
            status = FStatusEffects.pFreezing;
            statusDuration = 240f;
            speedMultiplier = 0.5f;
            variants = 0;
            liquidDrop = Liquids.cryofluid;
            liquidMultiplier = 0.5f;
            isLiquid = true;
            cacheLayer = CacheLayer.cryofluid;

            emitLight = true;
            lightRadius = 25f;
            lightColor = Color.cyan.cpy().a(0.19f);
        }};

        pSlag = new CorrosionFloor("p-molten-slag") {{
            drownTime = 230f;
            status = FStatusEffects.pMelting;
            statusDuration = 240f;
            speedMultiplier = 0.19f;
            variants = 0;
            liquidDrop = Liquids.slag;
            isLiquid = true;
            cacheLayer = CacheLayer.slag;
            attributes.set(Attribute.heat, 0.85f);

            emitLight = true;
            lightRadius = 40f;
            lightColor = Color.orange.cpy().a(0.38f);
        }};

        pEmpty = new CorrosionEmptyFloor("p-empty");

        pStone = new CorrosionFloor("p-stone");

        pCraters = new CorrosionFloor("p-crater-stone") {{
            variants = 3;
            blendGroup = pStone;
        }};

        pCharr = new CorrosionFloor("p-char") {{
            blendGroup = pStone;
        }};

        pBasalt = new CorrosionFloor("p-basalt") {{
            attributes.set(Attribute.water, -0.25f);
        }};

        pHotrock = new CorrosionFloor("p-hotrock") {{
            attributes.set(Attribute.heat, 0.5f);
            attributes.set(Attribute.water, -0.5f);
            blendGroup = pBasalt;

            emitLight = true;
            lightRadius = 30f;
            lightColor = Color.orange.cpy().a(0.15f);
        }};

        pMagmarock = new CorrosionFloor("p-magmarock") {{
            attributes.set(Attribute.heat, 0.75f);
            attributes.set(Attribute.water, -0.75f);
            blendGroup = pBasalt;

            emitLight = true;
            lightRadius = 50f;
            lightColor = Color.orange.cpy().a(0.3f);
        }};

        pSand = new CorrosionFloor("p-sand-floor") {{
            itemDrop = Items.sand;
            playerUnmineable = true;
            attributes.set(Attribute.oil, 0.7f);
        }};

        pDarksand = new CorrosionFloor("p-darksand") {{
            itemDrop = Items.sand;
            playerUnmineable = true;
            attributes.set(Attribute.oil, 1.5f);
        }};

        pDirt = new CorrosionFloor("p-dirt");

        pMud = new CorrosionFloor("p-mud") {{
            speedMultiplier = 0.6f;
            variants = 3;
            status = FStatusEffects.pMuddy;
            statusDuration = 30f;
            attributes.set(Attribute.water, 1f);
            cacheLayer = CacheLayer.mud;
            walkSound = Sounds.mud;
            walkSoundVolume = 0.08f;
            walkSoundPitchMin = 0.4f;
            walkSoundPitchMax = 0.5f;
        }};

        ((ShallowLiquid) pDarksandTaintedWater).set(Blocks.taintedWater, Blocks.darksand);
        ((ShallowLiquid) pSandWater).set(Blocks.water, Blocks.sand);
        ((ShallowLiquid) pDarksandWater).set(Blocks.water, Blocks.darksand);

        pDacite = new CorrosionFloor("p-dacite");

        pRhyolite = new CorrosionFloor("p-rhyolite") {{
            attributes.set(Attribute.water, -1f);
        }};

        pRhyoliteCrater = new CorrosionFloor("p-rhyolite-crater") {{
            attributes.set(Attribute.water, -1f);
            blendGroup = pRhyolite;
        }};

        pRoughRhyolite = new CorrosionFloor("p-rough-rhyolite") {{
            attributes.set(Attribute.water, -1f);
            variants = 3;
        }};

        pRegolith = new CorrosionFloor("p-regolith") {{
            attributes.set(Attribute.water, -1f);
        }};

        pYellowStone = new CorrosionFloor("p-yellow-stone") {{
            attributes.set(Attribute.water, -1f);
        }};

        pCarbonStone = new CorrosionFloor("p-carbon-stone") {{
            attributes.set(Attribute.water, -1f);
            variants = 4;
        }};

        pFerricStone = new CorrosionFloor("p-ferric-stone") {{
            attributes.set(Attribute.water, -1f);
        }};

        pFerricCraters = new CorrosionFloor("p-ferric-craters") {{
            variants = 3;
            attributes.set(Attribute.water, -1f);
            blendGroup = pFerricStone;
        }};

        pBeryllicStone = new CorrosionFloor("p-beryllic-stone") {{
            variants = 4;
        }};

        pCrystallineStone = new CorrosionFloor("p-crystalline-stone") {{
            variants = 5;
        }};

        pCrystalFloor = new CorrosionFloor("p-crystal-floor") {{
            variants = 4;
        }};

        pYellowStonePlates = new CorrosionFloor("p-yellow-stone-plates") {{
            variants = 3;
        }};

        pRedStone = new CorrosionFloor("p-red-stone") {{
            attributes.set(Attribute.water, -1f);
            variants = 4;
        }};

        pDenseRedStone = new CorrosionFloor("p-dense-red-stone") {{
            attributes.set(Attribute.water, -1f);
            variants = 4;
        }};

        pRedIce = new CorrosionFloor("p-red-ice") {{
            dragMultiplier = 0.4f;
            speedMultiplier = 0.9f;
            attributes.set(Attribute.water, 0.4f);
        }};

        pArkyciteFloor = new CorrosionFloor("p-arkycite-floor") {{
            speedMultiplier = 0.3f;
            variants = 0;
            liquidDrop = Liquids.arkycite;
            isLiquid = true;
            drownTime = 200f;
            cacheLayer = CacheLayer.arkycite;
            albedo = 0.9f;
        }};

        pArkyicStone = new CorrosionFloor("p-arkyic-stone") {{
            variants = 3;
        }};

        pRhyoliteVent = new CorrosionSteamVent("p-rhyolite-vent") {{
            parent = blendGroup = pRhyolite;
            attributes.set(Attribute.steam, 1f);
        }};

        pCarbonVent = new CorrosionSteamVent("p-carbon-vent") {{
            parent = blendGroup = pCarbonStone;
            attributes.set(Attribute.steam, 1f);
        }};

        pArkyicVent = new CorrosionSteamVent("p-arkyic-vent") {{
            parent = blendGroup = pArkyicStone;
            attributes.set(Attribute.steam, 1f);
        }};

        pYellowStoneVent = new CorrosionSteamVent("p-yellow-stone-vent") {{
            parent = blendGroup = pYellowStone;
            attributes.set(Attribute.steam, 1f);
        }};

        pRedStoneVent = new CorrosionSteamVent("p-red-stone-vent") {{
            parent = blendGroup = pDenseRedStone;
            attributes.set(Attribute.steam, 1f);
        }};

        pCrystallineVent = new CorrosionSteamVent("p-crystalline-vent") {{
            parent = blendGroup = pCrystallineStone;
            attributes.set(Attribute.steam, 1f);
        }};

        pRedmat = new CorrosionFloor("p-redmat");
        pBluemat = new CorrosionFloor("p-bluemat");

        pGrass = new CorrosionFloor("p-grass") {{
            attributes.set(Attribute.water, 0.1f);
        }};

        pSalt = new CorrosionFloor("p-salt") {{
            variants = 0;
            attributes.set(Attribute.water, -0.3f);
            attributes.set(Attribute.oil, 0.3f);
        }};

        pSnow = new CorrosionFloor("p-snow") {{
            attributes.set(Attribute.water, 0.2f);
            albedo = 0.7f;
        }};

        pIce = new CorrosionFloor("p-ice") {{
            dragMultiplier = 0.35f;
            speedMultiplier = 0.9f;
            attributes.set(Attribute.water, 0.4f);
            albedo = 0.65f;
        }};

        pIceSnow = new CorrosionFloor("p-ice-snow") {{
            dragMultiplier = 0.6f;
            variants = 3;
            attributes.set(Attribute.water, 0.3f);
            albedo = 0.6f;
        }};

        pShale = new CorrosionFloor("p-shale") {{
            variants = 3;
            attributes.set(Attribute.oil, 1.6f);
        }};

        pMoss = new CorrosionFloor("p-moss") {{
            variants = 3;
            attributes.set(Attribute.spores, 0.15f);
        }};

        pCoreZone = new CorrosionFloor("p-core-zone") {{
            variants = 0;
            allowCorePlacement = true;
        }};

        pSporeMoss = new CorrosionFloor("p-spore-moss") {{
            variants = 3;
            attributes.set(Attribute.spores, 0.3f);
        }};
    }
}