package Floor.FEntities.FBlock;

import Floor.FContent.FItems;
import Floor.FTools.FUnitUpGrade;
import Floor.FTools.UnitUpGrade;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.type.Item;
import mindustry.type.UnitType;
import mindustry.ui.Styles;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.blocks.units.UnitBlock;

import java.util.HashMap;
import java.util.Map;

public class GradeFactory extends UnitBlock {
    private final static Map<Item, Integer> itemIndex = new HashMap<>();
    public Seq<UnitType> grades = new Seq<>(UnitUpGrade.uppers);
    public boolean out = true;
    public float constructTime = 60f * 3f;

    public GradeFactory(String name) {
        super(name);

        update = true;
        hasPower = true;
        hasItems = true;
        solid = true;
        configurable = true;
        clearOnDoubleTap = true;
        outputsPayload = true;
        rotate = true;
        regionRotated1 = 1;
        commandable = true;
        ambientSound = Sounds.respawning;

        itemIndex.put(FItems.healthPower, 0);
        itemIndex.put(FItems.damagePower, 1);
        itemIndex.put(FItems.reloadPower, 2);
        itemIndex.put(FItems.speedPower, 3);
        itemIndex.put(FItems.againPower, 4);
        itemIndex.put(FItems.shieldPower, 5);

        configClear((GradeBuild b) -> b.choose = -1);

        config(Item.class, (GradeBuild b, Item i) -> {
            if (b.choose != itemIndex.get(i)) {
                b.choose = itemIndex.get(i);
            }
        });
    }

    public class GradeBuild extends UnitBuild {
        private int itemUse = 0;
        private boolean outing = false;
        public int level = 0;
        public int choose = -1;
        public Item item = null;
        public float timer;
        public Unit lastUnit;

        @Override
        public void buildConfiguration(Table table) {
            Seq<Item> units = new Seq<>();
            for (Item item : itemIndex.keySet()) {
                units.add(item);
            }

            if (units.any()) {
                ItemSelection.buildTable(GradeFactory.this, table, units,
                        () -> item,
                        this::configure,
                        selectionRows, selectionColumns);
            } else {
                table.table(Styles.black3, t -> t.add("@none").color(Color.lightGray));
            }
        }

        @Override
        public void draw() {
            Draw.rect(region, x, y);
            Draw.rect(outRegion, x, y, rotdeg());

            if (lastUnit != null) {
                Draw.draw(Layer.blockOver,
                        () -> Drawf.construct(this, lastUnit.type, rotdeg() - 90f, timer / constructTime, speedScl, time));
            }

            Draw.z(Layer.blockOver);

            payRotation = rotdeg();
            drawPayload();

            Draw.z(Layer.blockOver + 0.1f);

            Draw.rect(topRegion, x, y);
        }

        @Override
        public void updateTile() {
            if (items.total() != 0) {
                Fx.healWave.at(Vars.player.unit());
            }
            updateItem();

            if (payload != null) {
                if (lastUnit != payload.unit) {
                    timer = constructTime;
                }
                lastUnit = payload.unit;
            } else {
                outing = false;
                lastUnit = null;
            }

            if (outing) {
                moveOutPayload();
            } else if (lastUnit instanceof FUnitUpGrade uug && moveInPayload() && grades.indexOf(lastUnit.type) >= 0) {
                update(uug);
                if (item != null && itemUse >= 0 && items.get(item) >= itemUse && !lastUnit.type.isBanned()) {
                    float adder = Time.delta * edelta() * Math.max(0, efficiency);
                    timer = out ? level > 0 ? timer + adder : constructTime :
                            level >= 10 ? constructTime : timer + adder;
                    if (timer >= constructTime) {
                        if (out && level > 0) {
                            items.add(item, level);
                            gradeChange(uug);
                        } else if (!out && level < 10) {
                            outing = true;
                            items.remove(item, itemUse);
                            consume();
                            gradeChange(uug);
                        }

                        lastUnit = null;
                    }
                } else if (lastUnit != null && !lastUnit.type.isBanned()) {
                    outing = true;
                }
            }
        }

        @Override
        public Object config() {
            return item;
        }

        private void updateNumber() {
            if (payload != null && payload.unit instanceof FUnitUpGrade) {
                itemUse = out ? level == 0 ? -1 : 0 : level < 10 ? (level + 1) * (level + 1) * Math.max(1, (int) payload.unit.maxHealth / 7000) : -1;
            } else {
                itemUse = -1;
            }
        }

        private void updateItem() {
            for (Item item : itemIndex.keySet()) {
                if (itemIndex.get(item) == choose) {
                    this.item = item;
                }
            }
        }

        private void updateLevel(FUnitUpGrade uug) {
            if (choose == 0) {
                level = uug.getHealthLevel();
            } else if (choose == 1) {
                level = uug.getDamageLevel();
            } else if (choose == 2) {
                level = uug.getReloadLevel();
            } else if (choose == 3) {
                level = uug.getSpeedLevel();
            } else if (choose == 4) {
                level = uug.getAgainLevel();
            } else if (choose == 5) {
                level = uug.getShieldLevel();
            }
        }

        public void gradeChange(FUnitUpGrade uug) {
            if (out) {
                uug.setLevel(uug.getLevel() - 1);
                switch (choose) {
                    case 0 -> uug.setHealthLevel(uug.getHealthLevel() - 1);
                    case 1 -> uug.setDamageLevel(uug.getDamageLevel() - 1);
                    case 2 -> uug.setReloadLevel(uug.getReloadLevel() - 1);
                    case 3 -> uug.setSpeedLevel(uug.getSpeedLevel() - 1);
                    case 4 -> uug.setAgainLevel(uug.getAgainLevel() - 1);
                    case 5 -> {
                        uug.setShieldLevel(uug.getShieldLevel() - 1);
                        uug.sfa(uug.getShieldLevel());
                    }
                }
            } else {
                uug.setLevel(uug.getLevel() + 1);
                switch (choose) {
                    case 0 -> uug.setHealthLevel(uug.getHealthLevel() + 1);
                    case 1 -> uug.setDamageLevel(uug.getDamageLevel() + 1);
                    case 2 -> uug.setReloadLevel(uug.getReloadLevel() + 1);
                    case 3 -> uug.setSpeedLevel(uug.getSpeedLevel() + 1);
                    case 4 -> uug.setAgainLevel(uug.getAgainLevel() + 1);
                    case 5 -> {
                        uug.setShieldLevel(uug.getShieldLevel() + 1);
                        uug.sfa(uug.getShieldLevel());
                    }
                }
            }
        }

        private void update(FUnitUpGrade uug) {
            updateLevel(uug);
            updateNumber();
        }
    }
}
