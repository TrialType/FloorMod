package Floor.FContent;

import arc.graphics.Color;
import arc.struct.Seq;
import mindustry.content.Items;
import mindustry.content.Planets;
import mindustry.content.TechTree;
import mindustry.game.Objectives;
import mindustry.game.Team;
import mindustry.graphics.Pal;
import mindustry.graphics.g3d.HexMesh;
import mindustry.graphics.g3d.HexSkyMesh;
import mindustry.graphics.g3d.MultiMesh;
import mindustry.maps.planet.SerpuloPlanetGenerator;
import mindustry.type.ItemStack;
import mindustry.type.Planet;

import static mindustry.content.TechTree.node;

public class FPlanets {
    public static Planet ENGSWEIS;

    public static void load() {
        Planets.sun.children.clear();
        Planets.sun.updateTotalRadius();

        ENGSWEIS = new Planet("engsweis", Planets.sun, 1, 3) {{
            generator = new SerpuloPlanetGenerator();
            meshLoader = () -> new HexMesh(this, 6);
            cloudMeshLoader = () -> new MultiMesh(
                    new HexSkyMesh(this, 11, 0.15f, 0.13f, 5, new Color().set(Pal.spore).mul(0.9f).a(0.75f), 2, 0.45f, 0.9f, 0.38f),
                    new HexSkyMesh(this, 1, 0.6f, 0.16f, 5, Color.white.cpy().lerp(Pal.spore, 0.55f).a(0.75f), 2, 0.45f, 1f, 0.41f)
            );

            launchCapacityMultiplier = 0.2f;
            sectorSeed = 15;
            allowWaves = true;
            allowWaveSimulation = true;
            allowSectorInvasion = true;
            enemyCoreSpawnReplace = true;

            prebuildBase = false;
            ruleSetter = r -> {
                r.waveTeam = Team.crux;
                r.placeRangeCheck = false;
                r.showSpawns = false;
            };

            iconColor = Color.valueOf("ed6542");
            landCloudColor = Color.valueOf("ed6542");
            atmosphereColor = Color.valueOf("f07218");

            atmosphereRadIn = 0.02f;
            atmosphereRadOut = 0.3f;

            startSector = 13;
            alwaysUnlocked = true;

            techTree = Planets.serpulo.techTree;
        }};

        TechTree.TechNode tn = node(FBlocks.primarySolidification, ItemStack.with(Items.metaglass, 350, Items.copper, 400, Items.lead, 250), Seq.with(new Objectives.OnPlanet(ENGSWEIS)), () -> {
            node(FBlocks.intermediateSolidification, ItemStack.with(Items.metaglass, 1250, Items.copper, 1500, Items.lead, 1000, Items.graphite, 1400), () -> {
                node(FBlocks.advancedSolidification, ItemStack.with(Items.metaglass, 5000, Items.copper, 4500, Items.lead, 4000, Items.graphite, 3500, Items.titanium, 3000), () -> {
                    node(FBlocks.ultimateSolidification, ItemStack.with(Items.metaglass, 15000, Items.copper, 14500, Items.lead, 14000, Items.graphite, 13500, Items.titanium, 14000, Items.thorium, 14500, Items.surgeAlloy, 5000), () -> {
                    });
                });
            });
        });
        tn.parent = ENGSWEIS.techTree;
        ENGSWEIS.techTree.children.add(tn);

        Planets.sun.children.add(Planets.erekir);
        Planets.sun.children.add(Planets.serpulo);
        Planets.erekir.orbitRadius = (ENGSWEIS.orbitRadius + Planets.serpulo.orbitRadius) / 2;
    }
}
