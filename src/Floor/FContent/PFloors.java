package Floor.FContent;

import Floor.FEntities.FBlock.CorrosionEmptyFloor;
import Floor.FEntities.FBlock.CorrosionFloor;
import Floor.FEntities.FBlock.CorrosionSteamVent;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.gl.Shader;
import arc.struct.Seq;
import mindustry.content.Items;
import mindustry.content.StatusEffects;
import mindustry.gen.Sounds;
import mindustry.graphics.Shaders;
import mindustry.world.Block;
import mindustry.world.blocks.environment.Floor;
import mindustry.content.Liquids;
import mindustry.graphics.CacheLayer;
import mindustry.world.blocks.environment.OreBlock;
import mindustry.world.blocks.environment.StaticWall;
import mindustry.world.meta.Attribute;

public class PFloors {
    public final static Seq<Block> WEFloors = new Seq<>();
    public static Floor pDeepwater, pWater, pTaintedWater, pDeepTaintedWater, pTar, pCryofluid, pSlag, pStone, pCraters, pCharr, pBasalt, pHotrock, pMagmarock,
            pSand, pDarksand, pDirt, pMud, pEmpty, pDacite, pRhyolite, pRhyoliteCrater, pRoughRhyolite, pRegolith, pYellowStone, pCarbonStone,
            pFerricStone, pFerricCraters, pBeryllicStone, pCrystallineStone, pCrystalFloor, pYellowStonePlates, pRedStone, pDenseRedStone,
            pRedIce, pArkyciteFloor, pArkyicStone, pRhyoliteVent, pCarbonVent, pArkyicVent, pYellowStoneVent, pRedStoneVent, pCrystallineVent,
            pRedmat, pBluemat, pGrass, pSalt, pSnow, pIce, pIceSnow, pShale, pMoss, pCoreZone, pSporeMoss, pMetalFloor, pMetalFloorDamaged,
            pMetalFloor2, pMetalFloor3, pMetalFloor4, pMetalFloor5, pDarkPanel1, pDarkPanel2, pDarkPanel3, pDarkPanel4, pDarkPanel5, pDarkPanel6;
    public static StaticWall igneousRock;
    public static Floor igneousFloor, metaGlassOre,
            poolCopper, poolLead, poolTitanium, poolThorium;

    public static void load() {
        metaGlassOre = new OreBlock("meta-glass-ore", Items.metaglass) {{
            oreDefault = true;
            oreThreshold = 0.75f;
            oreScale = 23.9f;
        }};

        poolCopper = new Floor("pool-copper") {{
            drownTime = 230f;
            status = StatusEffects.melting;
            statusDuration = 240f;
            speedMultiplier = 0.19f;
            variants = 0;
            liquidDrop = FLiquids.fusionCopper;
            isLiquid = true;
            cacheLayer = new CacheLayer.ShaderLayer(new Shaders.SurfaceShader("test"));
            attributes.set(Attribute.heat, 0.85f);

            emitLight = true;
            lightRadius = 40f;
            lightColor = Color.valueOf("FF583A");
        }};
        poolLead = new Floor("pool-lead") {{
            drownTime = 230f;
            status = StatusEffects.melting;
            statusDuration = 240f;
            speedMultiplier = 0.19f;
            variants = 0;
            liquidDrop = FLiquids.fusionLead;
            isLiquid = true;
            cacheLayer = CacheLayer.tar;
            attributes.set(Attribute.heat, 0.85f);

            emitLight = true;
            lightRadius = 40f;
            lightColor = Color.valueOf("9D42FF");
        }};
        poolTitanium = new Floor("pool-titanium") {{
            drownTime = 230f;
            status = StatusEffects.melting;
            statusDuration = 240f;
            speedMultiplier = 0.19f;
            variants = 0;
            liquidDrop = FLiquids.fusionTitanium;
            isLiquid = true;
            cacheLayer = CacheLayer.tar;
            attributes.set(Attribute.heat, 0.85f);

            emitLight = true;
            lightRadius = 40f;
            lightColor = Color.valueOf("4A45FF");
        }};
        poolThorium = new Floor("pool-thorium") {{
            drownTime = 230f;
            status = StatusEffects.melting;
            statusDuration = 240f;
            speedMultiplier = 0.19f;
            variants = 0;
            liquidDrop = FLiquids.fusionThorium;
            isLiquid = true;
            cacheLayer = CacheLayer.tar;
            attributes.set(Attribute.heat, 0.85f);

            emitLight = true;
            lightRadius = 40f;
            lightColor = Color.valueOf("FA6188");
        }};

        igneousRock = new StaticWall("igneous-rock") {{
            variants = 3;
        }};
        igneousFloor = new Floor("igneous-floor") {{
            variants = 1;
        }};


        pMetalFloor = new CorrosionFloor("p-metal-floor", 0, FStatusEffects.corrosionI);
        pMetalFloorDamaged = new CorrosionFloor("p-metal-floor-damaged", 3, FStatusEffects.corrosionI);

        pMetalFloor2 = new CorrosionFloor("p-metal-floor-2", 0, FStatusEffects.corrosionI);
        pMetalFloor3 = new CorrosionFloor("p-metal-floor-3", 0, FStatusEffects.corrosionI);
        pMetalFloor4 = new CorrosionFloor("p-metal-floor-4", 0, FStatusEffects.corrosionI);
        pMetalFloor5 = new CorrosionFloor("p-metal-floor-5", 0, FStatusEffects.corrosionI);

        pDarkPanel1 = new CorrosionFloor("p-dark-panel-1", 0, FStatusEffects.corrosionI);
        pDarkPanel2 = new CorrosionFloor("p-dark-panel-2", 0, FStatusEffects.corrosionI);
        pDarkPanel3 = new CorrosionFloor("p-dark-panel-3", 0, FStatusEffects.corrosionI);
        pDarkPanel4 = new CorrosionFloor("p-dark-panel-4", 0, FStatusEffects.corrosionI);
        pDarkPanel5 = new CorrosionFloor("p-dark-panel-5", 0, FStatusEffects.corrosionI);
        pDarkPanel6 = new CorrosionFloor("p-dark-panel-6", 0, FStatusEffects.corrosionI);

        pDeepwater = new CorrosionFloor("p-deep-water") {{
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

        pEmpty = new CorrosionEmptyFloor("p-empty") {{
            status = FStatusEffects.corrosionI;
        }};

        pStone = new CorrosionFloor("p-stone", FStatusEffects.corrosionI);

        pCraters = new CorrosionFloor("p-crater-stone", FStatusEffects.corrosionI) {{
            variants = 3;
            blendGroup = pStone;
        }};

        pCharr = new CorrosionFloor("p-char", FStatusEffects.corrosionI) {{
            blendGroup = pStone;
        }};

        pBasalt = new CorrosionFloor("p-basalt", FStatusEffects.corrosionI) {{
            attributes.set(Attribute.water, -0.25f);
        }};

        pHotrock = new CorrosionFloor("p-hotrock", FStatusEffects.corrosionI) {{
            attributes.set(Attribute.heat, 0.5f);
            attributes.set(Attribute.water, -0.5f);
            blendGroup = pBasalt;

            emitLight = true;
            lightRadius = 30f;
            lightColor = Color.orange.cpy().a(0.15f);
        }};

        pMagmarock = new CorrosionFloor("p-magmarock", FStatusEffects.corrosionI) {{
            attributes.set(Attribute.heat, 0.75f);
            attributes.set(Attribute.water, -0.75f);
            blendGroup = pBasalt;

            emitLight = true;
            lightRadius = 50f;
            lightColor = Color.orange.cpy().a(0.3f);
        }};

        pSand = new CorrosionFloor("p-sand-floor", FStatusEffects.corrosionI) {{
            itemDrop = Items.sand;
            playerUnmineable = true;
            attributes.set(Attribute.oil, 0.7f);
        }};

        pDarksand = new CorrosionFloor("p-darksand", FStatusEffects.corrosionI) {{
            itemDrop = Items.sand;
            playerUnmineable = true;
            attributes.set(Attribute.oil, 1.5f);
        }};

        pDirt = new CorrosionFloor("p-dirt", FStatusEffects.corrosionI);

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
        pDacite = new CorrosionFloor("p-dacite", FStatusEffects.corrosionI);
        pRhyolite = new CorrosionFloor("p-rhyolite", FStatusEffects.corrosionI) {{
            attributes.set(Attribute.water, -1f);
        }};
        pRhyoliteCrater = new CorrosionFloor("p-rhyolite-crater", FStatusEffects.corrosionI) {{
            attributes.set(Attribute.water, -1f);
            blendGroup = pRhyolite;
        }};
        pRoughRhyolite = new CorrosionFloor("p-rough-rhyolite", FStatusEffects.corrosionI) {{
            attributes.set(Attribute.water, -1f);
            variants = 3;
        }};
        pRegolith = new CorrosionFloor("p-regolith", FStatusEffects.corrosionI) {{
            attributes.set(Attribute.water, -1f);
        }};
        pYellowStone = new CorrosionFloor("p-yellow-stone", FStatusEffects.corrosionI) {{
            attributes.set(Attribute.water, -1f);
        }};
        pCarbonStone = new CorrosionFloor("p-carbon-stone", FStatusEffects.corrosionI) {{
            attributes.set(Attribute.water, -1f);
            variants = 4;
        }};
        pFerricStone = new CorrosionFloor("p-ferric-stone", FStatusEffects.corrosionI) {{
            attributes.set(Attribute.water, -1f);
        }};
        pFerricCraters = new CorrosionFloor("p-ferric-craters", FStatusEffects.corrosionI) {{
            variants = 3;
            attributes.set(Attribute.water, -1f);
            blendGroup = pFerricStone;
        }};
        pBeryllicStone = new CorrosionFloor("p-beryllic-stone", FStatusEffects.corrosionI) {{
            variants = 4;
        }};
        pCrystallineStone = new CorrosionFloor("p-crystalline-stone", FStatusEffects.corrosionI) {{
            variants = 5;
        }};
        pCrystalFloor = new CorrosionFloor("p-crystal-floor", FStatusEffects.corrosionI) {{
            variants = 4;
        }};
        pYellowStonePlates = new CorrosionFloor("p-yellow-stone-plates", FStatusEffects.corrosionI) {{
            variants = 3;
        }};
        pRedStone = new CorrosionFloor("p-red-stone", FStatusEffects.corrosionI) {{
            attributes.set(Attribute.water, -1f);
            variants = 4;
        }};
        pDenseRedStone = new CorrosionFloor("p-dense-red-stone", FStatusEffects.corrosionI) {{
            attributes.set(Attribute.water, -1f);
            variants = 4;
        }};
        pRedIce = new CorrosionFloor("p-red-ice", FStatusEffects.corrosionI) {{
            dragMultiplier = 0.4f;
            speedMultiplier = 0.9f;
            attributes.set(Attribute.water, 0.4f);
        }};
        pArkyciteFloor = new CorrosionFloor("p-arkycite-floor", FStatusEffects.corrosionI) {{
            speedMultiplier = 0.3f;
            variants = 0;
            liquidDrop = Liquids.arkycite;
            isLiquid = true;
            drownTime = 200f;
            cacheLayer = CacheLayer.arkycite;
            albedo = 0.9f;
        }};
        pArkyicStone = new CorrosionFloor("p-arkyic-stone", FStatusEffects.corrosionI) {{
            variants = 3;
        }};
        pRhyoliteVent = new CorrosionSteamVent("p-rhyolite-vent") {{
            parent = blendGroup = pRhyolite;
            attributes.set(Attribute.steam, 1f);
            status = FStatusEffects.corrosionI;
        }};
        pCarbonVent = new CorrosionSteamVent("p-carbon-vent") {{
            parent = blendGroup = pCarbonStone;
            attributes.set(Attribute.steam, 1f);
            status = FStatusEffects.corrosionI;
        }};
        pArkyicVent = new CorrosionSteamVent("p-arkyic-vent") {{
            parent = blendGroup = pArkyicStone;
            attributes.set(Attribute.steam, 1f);
            status = FStatusEffects.corrosionI;
        }};
        pYellowStoneVent = new CorrosionSteamVent("p-yellow-stone-vent") {{
            parent = blendGroup = pYellowStone;
            attributes.set(Attribute.steam, 1f);
            status = FStatusEffects.corrosionI;
        }};
        pRedStoneVent = new CorrosionSteamVent("p-red-stone-vent") {{
            parent = blendGroup = pDenseRedStone;
            attributes.set(Attribute.steam, 1f);
            status = FStatusEffects.corrosionI;
        }};
        pCrystallineVent = new CorrosionSteamVent("p-crystalline-vent") {{
            parent = blendGroup = pCrystallineStone;
            attributes.set(Attribute.steam, 1f);
            status = FStatusEffects.corrosionI;
        }};
        pRedmat = new CorrosionFloor("p-redmat", FStatusEffects.corrosionI);
        pBluemat = new CorrosionFloor("p-bluemat", FStatusEffects.corrosionI);
        pGrass = new CorrosionFloor("p-grass", FStatusEffects.corrosionI) {{
            attributes.set(Attribute.water, 0.1f);
        }};
        pSalt = new CorrosionFloor("p-salt", FStatusEffects.corrosionI) {{
            variants = 0;
            attributes.set(Attribute.water, -0.3f);
            attributes.set(Attribute.oil, 0.3f);
        }};
        pSnow = new CorrosionFloor("p-snow", FStatusEffects.corrosionI) {{
            attributes.set(Attribute.water, 0.2f);
            albedo = 0.7f;
        }};
        pIce = new CorrosionFloor("p-ice", FStatusEffects.corrosionI) {{
            dragMultiplier = 0.35f;
            speedMultiplier = 0.9f;
            attributes.set(Attribute.water, 0.4f);
            albedo = 0.65f;
        }};
        pIceSnow = new CorrosionFloor("p-ice-snow", FStatusEffects.corrosionI) {{
            dragMultiplier = 0.6f;
            variants = 3;
            attributes.set(Attribute.water, 0.3f);
            albedo = 0.6f;
        }};
        pShale = new CorrosionFloor("p-shale", FStatusEffects.corrosionI) {{
            variants = 3;
            attributes.set(Attribute.oil, 1.6f);
        }};
        pMoss = new CorrosionFloor("p-moss", FStatusEffects.corrosionI) {{
            variants = 3;
            attributes.set(Attribute.spores, 0.15f);
        }};
        pCoreZone = new CorrosionFloor("p-core-zone", FStatusEffects.corrosionI) {{
            variants = 0;
            allowCorePlacement = true;
        }};
        pSporeMoss = new CorrosionFloor("p-spore-moss", FStatusEffects.corrosionI) {{
            variants = 3;
            attributes.set(Attribute.spores, 0.3f);
        }};

        WEFloors.addAll(igneousFloor, metaGlassOre, poolCopper, poolLead, poolTitanium, poolThorium, igneousRock);
    }
}