package Floor.FContent;

import arc.struct.Seq;
import mindustry.content.*;
import mindustry.game.Objectives;
import mindustry.type.ItemStack;

import static Floor.FContent.FBlocks.*;
import static Floor.FContent.FItems.*;
import static Floor.FContent.FPlanetGenerators.fullWater;
import static Floor.FContent.FPlanetGenerators.longestDown;
import static Floor.FContent.FPlanets.ENGSWEIS;
import static Floor.FContent.FUnits.*;
import static mindustry.content.Blocks.*;
import static mindustry.content.Items.*;
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
                tf = node(longestDown, Seq.with(new Objectives.SectorComplete(impact0078)), () -> {
                    node(fullWater, Seq.with(new Objectives.SectorComplete(longestDown)), () -> {
                    });
                });
                tf.parent = tn;
                tn.children.add(tf);
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
        //items
        tf = node(blueprint1, () -> {
            node(blueprint2, ItemStack.with(), () -> {
                node(blueprint3, ItemStack.with(), () -> {
                    node(blueprint4, ItemStack.with(), () -> {
                        node(blueprint5, ItemStack.with(), () -> {
                        });
                    });
                });
            });

            node(sizeProject1, ItemStack.with(blueprint1, 1000, silicon, 1000, copper, 1000), () -> {
                node(sizeProject2, ItemStack.with(blueprint1, 1000, silicon, 1000, copper, 1000), () -> {
                    node(sizeProject3, ItemStack.with(blueprint1, 1000, silicon, 1000, copper, 1000), () -> {
                        node(sizeProject4, ItemStack.with(blueprint1, 1000, silicon, 1000, copper, 1000), () -> {
                            node(sizeProject5, ItemStack.with(blueprint1, 1000, silicon, 1000, copper, 1000), () -> {
                                node(sizeProject6, ItemStack.with(blueprint1, 1000, silicon, 1000, copper, 1000), () -> {
                                    node(sizeProject7, ItemStack.with(blueprint1, 1000, silicon, 1000, copper, 1000), () -> {
                                        node(sizeProject8, ItemStack.with(blueprint1, 1000, silicon, 1000, copper, 1000), () -> {
                                            node(sizeProject9, ItemStack.with(blueprint1, 1000, silicon, 1000, copper, 1000), () -> {
                                                node(sizeProject10, ItemStack.with(blueprint1, 1000, silicon, 1000, copper, 1000), () -> {
                                                });
                                            });
                                        });
                                    });
                                });
                            });
                        });
                    });
                });
            });

            node(healthProject1, ItemStack.with(blueprint1, 1000, silicon, 1000, copper, 1000), () -> {
                node(healthProject2, ItemStack.with(blueprint2, 1000, silicon, 1000, copper, 1000), () -> {
                    node(healthProject3, ItemStack.with(blueprint3, 1000, silicon, 1000, copper, 1000), () -> {
                        node(healthProject4, ItemStack.with(blueprint4, 1000, silicon, 1000, copper, 1000), () -> {
                            node(healthProject5, ItemStack.with(blueprint5, 1000, silicon, 1000, copper, 1000), () -> {
                            });
                        });
                    });
                });
            });

            node(speedProject1, ItemStack.with(blueprint1, 1000, silicon, 1000, copper, 1000), () -> {
                node(speedProject2, ItemStack.with(blueprint2, 1000, silicon, 1000, copper, 1000), () -> {
                    node(speedProject3, ItemStack.with(blueprint3, 1000, silicon, 1000, copper, 1000), () -> {
                        node(speedProject4, ItemStack.with(blueprint4, 1000, silicon, 1000, copper, 1000), () -> {
                            node(speedProject5, ItemStack.with(blueprint5, 1000, silicon, 1000, copper, 1000), () -> {
                            });
                        });
                    });
                });
            });

            node(bulletProject1, ItemStack.with(blueprint1, 1000, silicon, 1000, copper, 1000), () -> {
                node(bulletProject2, ItemStack.with(blueprint2, 1000, silicon, 1000, copper, 1000), () -> {
                    node(bulletProject3, ItemStack.with(blueprint3, 1000, silicon, 1000, copper, 1000), () -> {
                        node(bulletProject4, ItemStack.with(blueprint4, 1000, silicon, 1000, copper, 1000), () -> {
                            node(bulletProject5, ItemStack.with(blueprint5, 1000, silicon, 1000, copper, 1000), () -> {
                            });
                        });
                    });
                });
            });

            node(reloadProject1, ItemStack.with(blueprint1, 1000, silicon, 1000, copper, 1000), () -> {
                node(reloadProject2, ItemStack.with(blueprint2, 1000, silicon, 1000, copper, 1000), () -> {
                    node(reloadProject3, ItemStack.with(blueprint3, 1000, silicon, 1000, copper, 1000), () -> {
                        node(reloadProject4, ItemStack.with(blueprint4, 1000, silicon, 1000, copper, 1000), () -> {
                            node(reloadProject5, ItemStack.with(blueprint5, 1000, silicon, 1000, copper, 1000), () -> {
                            });
                        });
                    });
                });
            });

            node(shieldProject1, ItemStack.with(blueprint1, 1000, silicon, 1000, copper, 1000), () -> {
                node(shieldProject2, ItemStack.with(blueprint2, 1000, silicon, 1000, copper, 1000), () -> {
                    node(shieldProject3, ItemStack.with(blueprint3, 1000, silicon, 1000, copper, 1000), () -> {
                        node(shieldProject4, ItemStack.with(blueprint4, 1000, silicon, 1000, copper, 1000), () -> {
                            node(shieldProject5, ItemStack.with(blueprint5, 1000, silicon, 1000, copper, 1000), () -> {
                            });
                        });
                    });
                });
            });

            node(splashProject1, ItemStack.with(blueprint1, 1000, silicon, 1000, copper, 1000), () -> {
                node(splashProject2, ItemStack.with(blueprint2, 1000, silicon, 1000, copper, 1000), () -> {
                    node(splashProject3, ItemStack.with(blueprint3, 1000, silicon, 1000, copper, 1000), () -> {
                        node(splashProject4, ItemStack.with(blueprint4, 1000, silicon, 1000, copper, 1000), () -> {
                            node(splashProject5, ItemStack.with(blueprint5, 1000, silicon, 1000, copper, 1000), () -> {
                            });
                        });
                    });
                });
            });

            node(pricesProject1, ItemStack.with(blueprint1, 1000, silicon, 1000, copper, 1000), () -> {
                node(pricesProject2, ItemStack.with(blueprint2, 1000, silicon, 1000, copper, 1000), () -> {
                    node(pricesProject3, ItemStack.with(blueprint3, 1000, silicon, 1000, copper, 1000), () -> {
                        node(pricesProject4, ItemStack.with(blueprint4, 1000, silicon, 1000, copper, 1000), () -> {
                            node(pricesProject5, ItemStack.with(blueprint5, 1000, silicon, 1000, copper, 1000), () -> {
                            });
                        });
                    });
                });
            });

            node(knockProject1, ItemStack.with(blueprint1, 1000, silicon, 1000, copper, 1000), () -> {
                node(knockProject2, ItemStack.with(blueprint2, 1000, silicon, 1000, copper, 1000), () -> {
                    node(knockProject3, ItemStack.with(blueprint3, 1000, silicon, 1000, copper, 1000), () -> {
                        node(knockProject4, ItemStack.with(blueprint4, 1000, silicon, 1000, copper, 1000), () -> {
                            node(knockProject5, ItemStack.with(blueprint5, 1000, silicon, 1000, copper, 1000), () -> {
                            });
                        });
                    });
                });
            });

            node(percentProject1, ItemStack.with(blueprint1, 1000, silicon, 1000, copper, 1000), () -> {
                node(percentProject2, ItemStack.with(blueprint2, 1000, silicon, 1000, copper, 1000), () -> {
                    node(percentProject3, ItemStack.with(blueprint3, 1000, silicon, 1000, copper, 1000), () -> {
                        node(percentProject4, ItemStack.with(blueprint4, 1000, silicon, 1000, copper, 1000), () -> {
                            node(percentProject5, ItemStack.with(blueprint5, 1000, silicon, 1000, copper, 1000), () -> {
                            });
                        });
                    });
                });
            });
        });
    }
}
