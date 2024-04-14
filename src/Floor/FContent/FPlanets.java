package Floor.FContent;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g3d.VertexBatch3D;
import mindustry.content.Blocks;
import mindustry.content.Planets;
import mindustry.game.Team;
import mindustry.graphics.g3d.HexMesh;
import mindustry.graphics.g3d.HexSkyMesh;
import mindustry.graphics.g3d.MultiMesh;
import mindustry.maps.planet.SerpuloPlanetGenerator;
import mindustry.type.Planet;
import mindustry.type.Sector;
import mindustry.type.SectorPreset;
import mindustry.world.meta.Attribute;

public class FPlanets {
    public static Planet ENGSWEIS;

    public static void load() {
        Planets.serpulo.techTree.planet = null;
        Planets.erekir.techTree.planet = null;

        ENGSWEIS = new Planet("engsweis", Planets.sun, 1, 5) {{
            generator = new SerpuloPlanetGenerator();
            meshLoader = () -> new HexMesh(this, 3);
            cloudMeshLoader = () -> new MultiMesh(
                    new HexSkyMesh(this, 2, 0.15f, 0.14f, 5,
                            Color.valueOf("eba768").a(0.75f), 2, 0.42f, 1f, 0.43f),
                    new HexSkyMesh(this, 3, 0.6f, 0.15f, 5,
                            Color.valueOf("eea293").a(0.75f), 2, 0.42f, 1.2f, 0.45f)
            );

            alwaysUnlocked = true;
            landCloudColor = Color.valueOf("ed6542");
            atmosphereColor = Color.valueOf("f07218");
            startSector = 10;
            atmosphereRadIn = 0.02f;
            atmosphereRadOut = 0.3f;
            tidalLock = true;
            orbitSpacing = 2f;
            totalRadius += 2.6f;
            lightSrcTo = 0.5f;
            lightDstFrom = 0.2f;
            clearSectorOnLose = true;
            defaultCore = Blocks.coreAcropolis;
            iconColor = Color.valueOf("ff9266");
            hiddenItems.clear();
            enemyBuildSpeedMultiplier = 1.5f;

            allowLaunchToNumbered = false;

            updateLighting = false;

            defaultAttributes.set(Attribute.heat, 0.8f);

            ruleSetter = r -> {
                r.waveTeam = Team.crux;
                r.placeRangeCheck = false;
                r.showSpawns = false;
            };

            orbitTime = 12;
            startSector = 1;
            sectorSeed = 114;

            techTree = Planets.serpulo.techTree;
        }};

        Sector s = ENGSWEIS.sectors.get(ENGSWEIS.sectors.size - 1);
        s.preset = new SectorPreset(Core.bundle.get("map.floor-szc"), ENGSWEIS, ENGSWEIS.sectors.size - 1) {{
            isLastSector = true;
            difficulty = 10;
        }};
        ENGSWEIS.setLastSector(s);
    }
}
