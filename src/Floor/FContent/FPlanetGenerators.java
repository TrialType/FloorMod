package Floor.FContent;

import arc.Core;
import mindustry.type.SectorPreset;

import static Floor.FContent.FPlanets.ENGSWEIS;

public class FPlanetGenerators {
    public static SectorPreset fullWater, longestDown;

    public static void load() {
        fullWater = new SectorPreset(Core.bundle.get("map.floor-szc"), ENGSWEIS, 96) {{
            difficulty = 10;
        }};

        longestDown = new SectorPreset(Core.bundle.get("map.floor-long-down"), ENGSWEIS, 64) {{
            difficulty = 6;
        }};
    }
}
