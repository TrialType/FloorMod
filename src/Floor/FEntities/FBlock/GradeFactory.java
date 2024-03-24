package Floor.FEntities.FBlock;

import Floor.FContent.FItems;
import Floor.FTools.FUnitUpGrade;
import Floor.FTools.UnitUpGrade;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.type.Item;
import mindustry.type.UnitType;
import mindustry.ui.Styles;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.blocks.units.UnitBlock;

public class GradeFactory extends UnitBlock {
    private final static Item[] Items = {
            FItems.healthPower,
            FItems.damagePower,
            FItems.reloadPower,
            FItems.speedPower,
            FItems.againPower,
            FItems.shieldPower
    };
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

        configClear((GradeBuild b) -> b.choose = -1);

        config(Item.class, (GradeBuild b, Item i) -> {
            if (b.choose != findIndex(i)) {
                b.choose = findIndex(i);
            }
        });
    }

    private static int findIndex(Item i) {
        for (int j = 0; j < Items.length; j++) {
            if (Items[j] == i) {
                return j;
            }
        }
        return -1;
    }

    public class GradeBuild extends UnitBuild {
        private int lastId = -1;
        private int itemUse = 0;
        private boolean outing = false;
        public int level = 0;
        public int choose = -1;
        public Item item = null;
        public float timer;
        public Unit lastUnit;

        @Override
        public void buildConfiguration(Table table) {
            Seq<Item> units = new Seq<>(Items);

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
            if (lastId >= 0) {
                lastUnit = Groups.unit.getByID(lastId);
                lastId = -1;
            }

            updateItem();

            if (payload != null) {
                if (lastUnit != payload.unit) {
                    outing = false;
                    timer = 0;
                }

                lastUnit = payload.unit;
            } else {
                outing = false;
                lastUnit = null;
            }

            if (outing) {
                moveOutPayload();
            } else if (lastUnit instanceof FUnitUpGrade uug && !lastUnit.type.isBanned()) {

                update(uug);

                boolean in = moveInPayload();

                if (item != null && itemUse >= 0 && items.get(item) >= itemUse && in && efficiency >= 0) {
                    float adder = Time.delta * edelta() * Math.max(0, efficiency);
                    timer = out ? level > 0 ? timer + adder : constructTime :
                            level >= 10 ? constructTime : timer + adder;
                    if (timer >= constructTime) {
                        if (out) {
                            outing = true;
                            items.add(item, level);
                            gradeChange(uug);
                        } else if (level < 10) {
                            outing = true;
                            items.remove(item, itemUse);
                            consume();
                            gradeChange(uug);
                        }
                    }
                } else if (lastUnit != null && !lastUnit.type.isBanned() && in) {
                    outing = true;
                }
            } else if (lastUnit != null && !lastUnit.type.isBanned()) {
                outing = true;
            }
        }

        @Override
        public Object config() {
            return choose;
        }

        private void updateNumber() {
            if (lastUnit != null && grades.indexOf(lastUnit.type) >= 0) {
                if (out) {
                    if (level == 0) {
                        itemUse = -1;
                    } else {
                        itemUse = 0;
                    }
                } else {
                    if (level < 10 && level >= 0) {
                        itemUse = (level + 1) * (level + 1) * Math.max(1, (int) payload.unit.maxHealth / 7000);
                    } else {
                        itemUse = -1;
                    }
                }
            } else {
                itemUse = -1;
            }
        }

        private void updateItem() {
            item = choose >= 0 ? Items[choose] : null;
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
            } else {
                level = -1;
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

        public boolean acceptItem(Building source, Item item) {
            return findIndex(item) >= 0 && items.get(item) < getMaximumAccepted(item);
        }

        private void update(FUnitUpGrade uug) {
            updateLevel(uug);
            updateNumber();
        }

        @Override
        public void write(Writes write) {
            super.write(write);

            write.i(choose);
            write.f(timer);
            write.i(lastUnit == null ? -1 : lastUnit.id);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);

            choose = read.i();
            timer = read.f();
            lastId = read.i();
        }
    }
}
