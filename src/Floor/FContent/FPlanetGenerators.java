package Floor.FContent;

import arc.Core;
import mindustry.type.SectorPreset;

import static Floor.FContent.FPlanets.ENGSWEIS;

public class FPlanetGenerators {
    public static SectorPreset fullWater;

    public static void load() {
        fullWater = new SectorPreset(Core.bundle.get("map.floor-szc"), ENGSWEIS, ENGSWEIS.sectors.size - 1) {{
            isLastSector = false;
            difficulty = 10;
        }};
    }

}
