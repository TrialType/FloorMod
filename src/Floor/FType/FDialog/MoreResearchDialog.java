package Floor.FType.FDialog;

import arc.struct.ObjectMap;
import arc.struct.Seq;
import mindustry.content.Planets;
import mindustry.type.Item;
import mindustry.type.ItemSeq;
import mindustry.type.Planet;
import mindustry.type.Sector;
import mindustry.ui.dialogs.ResearchDialog;

import static mindustry.Vars.content;

public class MoreResearchDialog extends ResearchDialog {
    public MoreResearchDialog() {
        super();
    }

    @Override
    public void rebuildItems() {
        items = new ItemSeq() {
            //store sector item amounts for modifications
            final ObjectMap<Sector, ItemSeq> cache = new ObjectMap<>();

            {
                Seq<Planet> planets = new Seq<>();
                if (lastNode.planet != null) {
                    planets.add(lastNode.planet);
                }
                content.planets().each(p -> {
                    if (p.techTree == lastNode) {
                        planets.add(p);
                    }
                });
                Planet rootPlanet;
                if (planets.isEmpty()) {
                    rootPlanet = Planets.serpulo;

                    for (Sector sector : rootPlanet.sectors) {
                        if (sector.hasBase()) {
                            ItemSeq cached = sector.items();
                            cache.put(sector, cached);
                            cached.each((item, amount) -> {
                                values[item.id] += Math.max(amount, 0);
                                total += Math.max(amount, 0);
                            });
                        }
                    }
                } else {
                    for (int i = 0; i < planets.size; i++) {
                        rootPlanet = planets.get(i);

                        for (Sector sector : rootPlanet.sectors) {
                            if (sector.hasBase()) {
                                ItemSeq cached = sector.items();
                                cache.put(sector, cached);
                                cached.each((item, amount) -> {
                                    values[item.id] += Math.max(amount, 0);
                                    total += Math.max(amount, 0);
                                });
                            }
                        }
                    }
                }
            }

            //this is the only method that actually modifies the sequence itself.
            @Override
            public void add(Item item, int amount) {
                //only have custom removal logic for when the sequence gets items taken out of it (e.g. research)
                if (amount < 0) {
                    //remove items from each sector's storage, one by one

                    //negate amount since it's being *removed* - this makes it positive
                    amount = -amount;

                    //% that gets removed from each sector
                    double percentage = (double) amount / get(item);
                    int[] counter = {amount};
                    cache.each((sector, seq) -> {
                        if (counter[0] == 0) return;

                        //amount that will be removed
                        int toRemove = Math.min((int) Math.ceil(percentage * seq.get(item)), counter[0]);

                        //actually remove it from the sector
                        sector.removeItem(item, toRemove);
                        seq.remove(item, toRemove);

                        counter[0] -= toRemove;
                    });

                    //negate again to display correct number
                    amount = -amount;
                }

                super.add(item, amount);
            }
        };

        itemDisplay.rebuild(items);
    }
}
