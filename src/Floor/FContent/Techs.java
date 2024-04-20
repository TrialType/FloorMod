package Floor.FContent;

import arc.struct.Seq;
import mindustry.content.*;
import mindustry.game.Objectives;
import mindustry.type.ItemStack;

import static Floor.FContent.FBlocks.*;
import static Floor.FContent.FPlanetGenerators.fullWater;
import static Floor.FContent.FPlanetGenerators.longestDown;
import static Floor.FContent.FPlanets.ENGSWEIS;
import static Floor.FContent.FUnits.*;
import static mindustry.content.Blocks.*;
import static mindustry.content.SectorPresets.*;
import static mindustry.content.TechTree.node;

public class Techs {
    public static TechTree.TechNode tf;
    public static TechTree.TechNode head;

    public static void load() {
        //ews
        head = Planets.serpulo.techTree;
        //blocks
        tf = node(buildCore, ItemStack.with(Items.copper, 10000, Items.lead, 10000, Items.graphite, 10000, Items.silicon, 10000, Items.titanium, 10000), Seq.with(new Objectives.SectorComplete(overgrowth)), () -> {
        });
        tf.parent = head;
        head.children.add(tf);
        tf = node(FBlocks.primarySolidification, ItemStack.with(Items.metaglass, 350, Items.copper, 400, Items.lead, 250), Seq.with(new Objectives.OnPlanet(ENGSWEIS)), () -> {
            node(FBlocks.intermediateSolidification, ItemStack.with(Items.metaglass, 1250, Items.copper, 1500, Items.lead, 1000, Items.graphite, 1400), () -> {
                node(FBlocks.advancedSolidification, ItemStack.with(Items.metaglass, 5000, Items.copper, 4500, Items.lead, 4000, Items.graphite, 3500, Items.titanium, 3000), () -> {
                    node(FBlocks.ultimateSolidification, ItemStack.with(Items.metaglass, 15000, Items.copper, 14500, Items.lead, 14000, Items.graphite, 13500, Items.titanium, 14000, Items.thorium, 14500, Items.surgeAlloy, 5000), () -> {
                    });
                });
            });
        });
        tf.parent = head;
        head.children.add(tf);
        head.each(t -> {
            if (t.content == tsunami) {
                tf = node(fourNet, ItemStack.with(Items.titanium, 49990, Items.copper, 49990, Items.thorium, 49990, Items.silicon, 49990, Items.phaseFabric, 49990), Seq.with(new Objectives.SectorComplete(fullWater)), () -> {
                });
                tf.parent = t;
                t.children.add(tf);
            } else if (t.content == scorch) {
                tf = node(fireStream, ItemStack.with(Items.titanium, 3400, Items.copper, 3000, Items.graphite, 3500), Seq.with(new Objectives.SectorComplete(stainedMountains)), () -> {
                    node(fireBoost, ItemStack.with(Items.titanium, 15000, Items.graphite, 15000, Items.graphite, 20000, Items.silicon, 15000, Items.phaseFabric, 15000, Items.plastanium, 9000), () -> {
                    });
                });
                tf.parent = t;
                t.children.add(tf);
            } else if (t.content == copperWall) {
                tf = node(eleFence, ItemStack.with(Items.titanium, 1500, Items.copper, 3000, Items.silicon, 1500), Seq.with(new Objectives.SectorComplete(fungalPass)), () -> {
                    node(eleFenceII, ItemStack.with(Items.titanium, 3500, Items.copper, 6000, Items.silicon, 3000), Seq.with(new Objectives.SectorComplete(saltFlats)), () -> {
                        node(eleFenceIII, ItemStack.with(Items.titanium, 4500, Items.copper, 10000, Items.silicon, 5000), Seq.with(new Objectives.SectorComplete(nuclearComplex)), () -> {
                        });
                    });
                });
                tf.parent = t;
                t.children.add(tf);
            } else if (t.content == overdriveProjector) {
                tf = node(slowProject, ItemStack.with(Items.lead, 100, Items.titanium, 75, Items.silicon, 75, Items.plastanium, 30), () -> {
                });
                tf.parent = t;
                t.children.add(tf);
            } else if (t.content == groundFactory) {
                tf = node(specialUnitFactory, ItemStack.with(Items.lead, 15000, Items.silicon, 7000, Items.titanium, 14000, Items.thorium, 4000, Items.plastanium, 2000), () -> {
                    node(bulletInterception);
                    node(rejuvenate);
                });
                tf.parent = t;
                t.children.add(tf);
                tf = node(outPowerFactory, ItemStack.with(Items.copper, 5000, Items.lead, 6000, Items.silicon, 8000), () -> {
                    node(outPowerFactoryII, ItemStack.with(Items.copper, 10000, Items.lead, 12000, Items.silicon, 20000), () -> {
                        node(outPowerFactoryIII, ItemStack.with(Items.copper, 20000, Items.lead, 24000, Items.silicon, 28000), () -> {
                        });
                    });
                });
                tf.parent = t;
                t.children.add(tf);
                tf = node(inputPowerFactory, ItemStack.with(Items.copper, 5000, Items.lead, 6000, Items.silicon, 8000), () -> {
                    node(inputPowerFactoryII, ItemStack.with(Items.copper, 10000, Items.lead, 12000, Items.silicon, 20000), () -> {
                        node(inputPowerFactoryIII, ItemStack.with(Items.copper, 20000, Items.lead, 24000, Items.silicon, 28000), () -> {
                        });
                    });
                });
                tf.parent = t;
                t.children.add(tf);
            }
        });
        //Sectors
        head.each(tn -> {
            if (tn.content == impact0078) {
                TechTree.TechNode tt = node(longestDown, Seq.with(new Objectives.SectorComplete(impact0078)), () -> {
                    node(fullWater, Seq.with(new Objectives.SectorComplete(longestDown)), () -> {
                    });
                });
                tt.parent = tn;
                tn.children.add(tt);
            }
        });
        //units
        head.each(t -> {
            if (t.content == UnitTypes.dagger) {
                tf = node(barb, () -> node(hammer, () -> node(buying, () -> node(crazy, () -> node(transition, () -> node(shuttle, () -> {
                }))))));
                tf.parent = t;
                t.children.add(tf);
            }
        });
    }
}
