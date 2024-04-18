package Floor.FContent;

import arc.Core;
import arc.struct.Seq;
import mindustry.content.Planets;
import mindustry.content.TechTree;
import mindustry.game.Objectives;
import mindustry.type.SectorPreset;

import static Floor.FContent.FPlanets.ENGSWEIS;
import static mindustry.content.SectorPresets.impact0078;
import static mindustry.content.TechTree.node;

public class FPlanetGenerators {
    public static SectorPreset fullWater, longestDown;

    public static void load() {
        fullWater = new SectorPreset(Core.bundle.get("map.floor-szc"), ENGSWEIS, 96) {{
            difficulty = 10;
        }};

        longestDown = new SectorPreset(Core.bundle.get("map.floor-long-down"), ENGSWEIS, 13) {{
            difficulty = 6;
        }};

        Planets.serpulo.techTree.each(tn -> {
            TechTree.TechNode t;
            if ((t = tn).content == impact0078) {
                TechTree.TechNode tt = node(longestDown, Seq.with(new Objectives.SectorComplete(impact0078)), () -> {
                    node(fullWater, Seq.with(new Objectives.SectorComplete(longestDown)), () -> {
                    });
                });
                tt.parent = t;
                t.children.add(tt);
            }
        });

    }
}
