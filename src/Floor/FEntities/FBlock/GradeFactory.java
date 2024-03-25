package Floor.FEntities.FBlock;

import Floor.FContent.FItems;
import Floor.FTools.FUnitUpGrade;
import Floor.FTools.UnitUpGrade;
import arc.Core;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Scaling;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.TechTree;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.UnitType;
import mindustry.ui.Bar;
import mindustry.ui.ItemDisplay;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.world.blocks.units.UnitBlock;
import mindustry.world.meta.Stat;

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
    public float constructTime = 60f * 5f;

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
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("progress", (GradeBuild e) -> new Bar("bar.progress", Pal.ammo, e::fraction));
        addBar("item", (GradeBuild e) -> new Bar(e.item == null ? "null" : Core.bundle.get(e.item.localizedName), Pal.ammo, () -> 1f));
        addBar("levelTo", (GradeBuild e) -> new Bar(e.level + "/" + e.levelTo, Pal.ammo, () -> 1f));
    }

    @Override
    public void setStats() {
        super.setStats();

        stats.remove(Stat.itemCapacity);

        if (out) {
            stats.add(Stat.output, table -> {
                table.row();

                table.table(Styles.grayPanel, t -> {
                    t.image(FItems.healthPower.uiIcon).size(40).pad(10).left().scaling(Scaling.fit);

                    t.table(gre -> {
                        gre.right();
                        gre.add(new ItemDisplay(FItems.healthPower, 1, false)).pad(10);
                    }).right().grow().pad(10f);
                }).growX().pad(5);
                table.row();
            });
        }
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
        private int itemId = -1;
        private int itemUse = 0;
        private boolean outing = false;
        public int levelTo = 0;
        public int level = 0;
        public Item item = null;
        public Unit lastUnit;
        public Item lastItem;

        public float fraction() {
            return lastUnit == null ? 0 : progress / constructTime;
        }

        @Override
        public void buildConfiguration(Table table) {
            BaseDialog dialog = new BaseDialog("@settings");
            dialog.addCloseListener();
            dialog.cont.pane(set -> {
                set.row();
                set.add(Core.bundle.get("@items")).size(200, 100);
                set.row();
                set.table(s -> {
                    s.button(Core.bundle.get("@health"), () -> item = FItems.healthPower).pad(50).size(60, 150).left();
                    s.button(Core.bundle.get("@damage"), () -> item = FItems.damagePower).pad(50).size(60, 150).left();
                    s.button(Core.bundle.get("@reload"), () -> item = FItems.reloadPower).pad(50).size(60, 150).left();
                    s.button(Core.bundle.get("@speed"), () -> item = FItems.speedPower).pad(50).size(60, 150).left();
                    s.button(Core.bundle.get("@again"), () -> item = FItems.againPower).pad(50).size(60, 150).left();
                    s.button(Core.bundle.get("@shield"), () -> item = FItems.shieldPower).pad(50).size(60, 150).left();
                }).growX().growY();
                set.row();
                set.add(Core.bundle.get("@number")).size(400, 100).growX();
                set.row();
                set.table(n -> {
                    n.button("O", () -> levelTo = 0).pad(50).size(80, 80).left();
                    n.button("I", () -> levelTo = 1).pad(50).size(80, 80);
                    n.button("II", () -> levelTo = 2).pad(50).size(80, 80);
                    n.button("III", () -> levelTo = 3).pad(50).size(80, 80);
                    n.button("IV", () -> levelTo = 4).pad(50).size(80, 80).row();
                    n.button("V", () -> levelTo = 5).pad(50).size(80, 80).left();
                    n.button("VI", () -> levelTo = 6).pad(50).size(80, 80);
                    n.button("VII", () -> levelTo = 7).pad(50).size(80, 80);
                    n.button("VIII", () -> levelTo = 8).pad(50).size(80, 80);
                    n.button("IX", () -> levelTo = 9).pad(50).size(80, 80);
                    n.button("X", () -> levelTo = 10).pad(50).size(80, 80);
                }).growX().growY();
                set.row();
                set.button("@back", dialog::hide).size(400, 100).growX();
            }).growY().growX();

            table.row();
            table.table(Tex.paneSolid, t -> {
                t.row();
                t.button("settings", Icon.settings, dialog::show).size(300.0F, 60.0F).row();
            });
        }

        @Override
        public void draw() {
            Draw.rect(region, x, y);
            Draw.rect(outRegion, x, y, rotdeg());

            if (lastUnit != null) {
                Draw.draw(Layer.blockOver,
                        () -> Drawf.construct(this, lastUnit.type, rotdeg() - 90f,
                                progress / constructTime, 0.8f, progress));
            }

            Draw.z(Layer.blockOver);

            payRotation = rotdeg();
            drawPayload();

            Draw.z(Layer.blockOver + 0.1f);

            Draw.rect(topRegion, x, y);
        }

        @Override
        public void updateTile() {
            if (itemId >= 0) {
                item = Vars.content.item(itemId);
                itemId = -1;
            }
            if (lastId >= 0) {
                lastUnit = Groups.unit.getByID(lastId);
                lastId = -1;
            }

            if (lastItem != item) {
                progress = progress % 1f;
            }
            lastItem = item;
            if (payload != null) {
                if (lastUnit != payload.unit) {
                    outing = false;
                    progress = Mathf.lerpDelta(progress, 0, 0.05f);
                }

                lastUnit = payload.unit;
            } else {
                progress = Mathf.lerpDelta(progress, 0, 0.05f);
                outing = false;
                lastUnit = null;
            }

            if (outing) {
                moveOutPayload();
            } else if (lastUnit instanceof FUnitUpGrade uug && !lastUnit.type.isBanned()) {

                updateLevel(uug);
                updateNumber();

                boolean in = moveInPayload();
                boolean le = (item != null && items.get(item) >= itemUse && itemUse >= 0) &&
                        (out ? level > levelTo : level < levelTo && level <= 10);

                if (le && in && efficiency >= 0) {
                    float adder = Time.delta * edelta() * Math.max(0, efficiency);
                    progress = out ? level > 0 ? progress + adder : constructTime :
                            level >= 10 ? constructTime : progress + adder;
                    if (progress >= constructTime) {
                        if (out) {
                            if (level - 1 <= levelTo) {
                                outing = true;
                            } else {
                                progress = progress % 1f;
                            }
                            items.add(item, level);
                            gradeChange(uug);
                        } else if (level < 10) {
                            items.remove(item, itemUse);
                            consume();
                            gradeChange(uug);
                            if (level + 1 >= levelTo) {
                                outing = true;
                            } else {
                                progress = progress % 1f;
                            }
                        }
                    }
                } else if (lastUnit != null && !lastUnit.type.isBanned() && in) {
                    outing = true;
                }
            } else if (lastUnit != null && !lastUnit.type.isBanned()) {
                outing = true;
            }
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


        private void updateLevel(FUnitUpGrade uug) {
            int choose = findIndex(item);
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
                switch (findIndex(item)) {
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
                switch (findIndex(item)) {
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

        @Override
        public Object config() {
            return item;
        }

        @Override
        public void write(Writes write) {
            super.write(write);

            write.i(item == null ? -1 : item.id);
            write.i(levelTo);
            write.f(progress);
            write.i(lastUnit == null ? -1 : lastUnit.id);
            write.bool(outing);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);

            itemId = read.i();
            levelTo = read.i();
            progress = read.f();
            lastId = read.i();
            outing = read.bool();
        }
    }
}
