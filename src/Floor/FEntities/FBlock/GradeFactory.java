package Floor.FEntities.FBlock;

import Floor.FContent.FItems;
import Floor.FTools.interfaces.FUnitUpGrade;
import Floor.FTools.classes.UnitUpGrade;
import arc.Core;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Scaling;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.core.GameState;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.io.TypeIO;
import mindustry.type.Item;
import mindustry.type.UnitType;
import mindustry.ui.Bar;
import mindustry.ui.ItemDisplay;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.UnitPayload;
import mindustry.world.blocks.units.UnitBlock;
import mindustry.world.meta.Stat;

import static mindustry.Vars.net;
import static mindustry.Vars.state;

public class GradeFactory extends UnitBlock {
    private final static Item[] Items = {
            FItems.healthPower,
            FItems.damagePower,
            FItems.reloadPower,
            FItems.speedPower,
            FItems.againPower,
            FItems.shieldPower
    };
    private final static String[] numbers = {"0", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
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
        addBar("item", (GradeBuild e) -> new Bar(e.item == null ? "null" : Core.bundle.get("item." + e.item.name + ".name"), Pal.ammo, () -> 1f));
        addBar("levelTo", (GradeBuild e) -> new Bar(e.level + "/" + e.levelTo, Pal.ammo, () -> 1f));
    }

    @Override
    public void setStats() {
        super.setStats();

        stats.remove(Stat.itemCapacity);

        if (out) {
            stats.add(Stat.output, table -> {
                table.row();

                for (int i = 1; i < numbers.length; i++) {
                    int finalI = i;
                    table.table(Styles.grayPanel, t -> {
                        t.add(numbers[finalI]).size(40).pad(10).left().scaling(Scaling.fit);

                        t.table(gre -> {
                            gre.add(new ItemDisplay(FItems.healthPower, finalI, false)).pad(10).left();
                            gre.add("/").left();
                            gre.add(new ItemDisplay(FItems.damagePower, finalI, false)).pad(10).left();
                            gre.add("/").left();
                            gre.add(new ItemDisplay(FItems.reloadPower, finalI, false)).pad(10).left();
                            gre.add("/").left();
                            gre.add(new ItemDisplay(FItems.speedPower, finalI, false)).pad(10).left();
                            gre.add("/").left();
                            gre.add(new ItemDisplay(FItems.againPower, finalI, false)).pad(10).left();
                            gre.add("/").left();
                            gre.add(new ItemDisplay(FItems.shieldPower, finalI, false)).pad(10).left();
                        }).right().growX().pad(10f);
                    }).grow().row();
                    table.row();
                }
            });
        } else {
            stats.add(Stat.input, table -> {
                table.row();

                for (int i = 1; i < numbers.length; i++) {
                    int finalI = i;
                    table.table(Styles.grayPanel, t -> {
                        t.add(numbers[finalI]).size(40).pad(10).left().scaling(Scaling.fit);

                        t.table(gre -> {
                            gre.add(new ItemDisplay(FItems.healthPower, (int) Math.pow(finalI, 2), false)).pad(10).left();
                            gre.add("/").left();
                            gre.add(new ItemDisplay(FItems.damagePower, (int) Math.pow(finalI, 2), false)).pad(10).left();
                            gre.add("/").left();
                            gre.add(new ItemDisplay(FItems.reloadPower, (int) Math.pow(finalI, 2), false)).pad(10).left();
                            gre.add("/").left();
                            gre.add(new ItemDisplay(FItems.speedPower, (int) Math.pow(finalI, 2), false)).pad(10).left();
                            gre.add("/").left();
                            gre.add(new ItemDisplay(FItems.againPower, (int) Math.pow(finalI, 2), false)).pad(10).left();
                            gre.add("/").left();
                            gre.add(new ItemDisplay(FItems.shieldPower, (int) Math.pow(finalI, 2), false)).pad(10).left();
                            gre.add("*health/7000").left();
                        }).right().growX().pad(10f);
                    }).growX().row();
                    table.row();
                }
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

    public static class GradePlan {
        int levelTo;
        Item item;
        String name = "null";

        public GradePlan(int levelTo, Item item) {
            this.levelTo = levelTo;
            this.item = item;
            if (item != null) {
                if (item == FItems.healthPower) {
                    this.name = Core.bundle.get("@health");
                } else if (item == FItems.damagePower) {
                    this.name = Core.bundle.get("@damage");
                } else if (item == FItems.reloadPower) {
                    this.name = Core.bundle.get("@reload");
                } else if (item == FItems.speedPower) {
                    this.name = Core.bundle.get("@speed");
                } else if (item == FItems.againPower) {
                    this.name = Core.bundle.get("@again");
                } else if (item == FItems.shieldPower) {
                    this.name = Core.bundle.get("@shield");
                }
            } else {
                this.name = "null";
            }
        }

        public void item(int levelTo, Item item) {
            this.levelTo = levelTo;
            this.item = item;
            if (item != null) {
                if (item == FItems.healthPower) {
                    this.name = Core.bundle.get("@health");
                } else if (item == FItems.damagePower) {
                    this.name = Core.bundle.get("@damage");
                } else if (item == FItems.reloadPower) {
                    this.name = Core.bundle.get("@reload");
                } else if (item == FItems.speedPower) {
                    this.name = Core.bundle.get("@speed");
                } else if (item == FItems.againPower) {
                    this.name = Core.bundle.get("@again");
                } else if (item == FItems.shieldPower) {
                    this.name = Core.bundle.get("@shield");
                }
            } else {
                this.name = "null";
            }
        }
    }

    public class GradeBuild extends UnitBuild {
        public Table list;
        public BaseDialog bd;
        public final Seq<GradePlan> plan = new Seq<>();
        public final Seq<GradePlan> usePlan = new Seq<>();
        public boolean changed = true;
        public final Seq<Table> tables = new Seq<>();
        private int lastId = -1;
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
            table.row();
            table.table(Tex.paneSolid, t -> {
                t.row();
                t.button("", Icon.settings, () -> {
                    if (state.isGame() && !net.active()) {
                        state.set(GameState.State.paused);
                    }
                    addDialog();
                    bd.show();
                }).size(50, 40.0F).row();
            });
        }

        public void addDialog() {
            tables.clear();

            BaseDialog dialog = new BaseDialog("@settings");
            dialog.addCloseListener();
            dialog.cont.pane(set -> {
                set.row();

                set.table(sq -> {
                    list = sq;
                    sq.row();
                    for (int i = 0; i < plan.size; i++) {
                        sq.row();

                        sq.table(t -> {
                            tables.add(t);
                            GradePlan gp = plan.get(tables.indexOf(t));

                            t.background(Tex.scrollHorizontal);

                            t.button(gp.name, () -> {
                                GradePlan pl = plan.get(tables.indexOf(t));

                                BaseDialog baseDialog = new BaseDialog("@items");
                                baseDialog.addCloseListener();

                                baseDialog.cont.pane(tt -> tt.table(p -> {
                                    p.button(Core.bundle.get("@health"), () -> {
                                        pl.item(pl.levelTo, FItems.healthPower);
                                        changed = true;
                                        baseDialog.hide();
                                        bd.hide();
                                        bd.remove();
                                        addDialog();
                                        bd.show();
                                    }).pad(50).size(60, 150).left();
                                    p.button(Core.bundle.get("@damage"), () -> {
                                        pl.item(pl.levelTo, FItems.damagePower);
                                        changed = true;
                                        baseDialog.hide();
                                        bd.hide();
                                        bd.remove();
                                        addDialog();
                                        bd.show();
                                    }).pad(50).size(60, 150).left();
                                    p.button(Core.bundle.get("@reload"), () -> {
                                        pl.item(pl.levelTo, FItems.reloadPower);
                                        changed = true;
                                        baseDialog.hide();
                                        bd.hide();
                                        bd.remove();
                                        addDialog();
                                        bd.show();
                                    }).pad(50).size(60, 150).left();
                                    p.button(Core.bundle.get("@speed"), () -> {
                                        pl.item(pl.levelTo, FItems.speedPower);
                                        changed = true;
                                        baseDialog.hide();
                                        bd.hide();
                                        bd.remove();
                                        addDialog();
                                        bd.show();
                                    }).pad(50).size(60, 150).left();
                                    p.button(Core.bundle.get("@again"), () -> {
                                        pl.item(pl.levelTo, FItems.againPower);
                                        changed = true;
                                        baseDialog.hide();
                                        bd.hide();
                                        bd.remove();
                                        addDialog();
                                        bd.show();
                                    }).pad(50).size(60, 150).left();
                                    p.button(Core.bundle.get("@shield"), () -> {
                                        pl.item(pl.levelTo, FItems.shieldPower);
                                        changed = true;
                                        baseDialog.hide();
                                        bd.hide();
                                        bd.remove();
                                        addDialog();
                                        bd.show();
                                    }).pad(50).size(60, 150).left();
                                }).grow()).grow();

                                baseDialog.show();

                            }).size(100, 50).left().pad(30);

                            t.button(gp.levelTo + "", () -> {
                                GradePlan pl = plan.get(tables.indexOf(t));

                                BaseDialog baseDialog = new BaseDialog("@items");
                                baseDialog.addCloseListener();

                                baseDialog.cont.pane(tt -> tt.table(n -> {
                                    n.button("0", () -> {
                                        pl.item(0, pl.item);
                                        changed = true;
                                        baseDialog.hide();
                                        bd.hide();
                                        bd.remove();
                                        addDialog();
                                        bd.show();
                                    }).pad(50).size(80, 80).left();
                                    n.button("I", () -> {
                                        pl.item(1, pl.item);
                                        changed = true;
                                        baseDialog.hide();
                                        bd.hide();
                                        bd.remove();
                                        addDialog();
                                        bd.show();
                                    }).pad(50).size(80, 80);
                                    n.button("II", () -> {
                                        pl.item(2, pl.item);
                                        changed = true;
                                        baseDialog.hide();
                                        bd.hide();
                                        bd.remove();
                                        addDialog();
                                        bd.show();
                                    }).pad(50).size(80, 80);
                                    n.button("III", () -> {
                                        pl.item(3, pl.item);
                                        changed = true;
                                        baseDialog.hide();
                                        bd.hide();
                                        bd.remove();
                                        addDialog();
                                        bd.show();
                                    }).pad(50).size(80, 80);
                                    n.button("IV", () -> {
                                        pl.item(4, pl.item);
                                        changed = true;
                                        baseDialog.hide();
                                        bd.hide();
                                        bd.remove();
                                        addDialog();
                                        bd.show();
                                    }).pad(50).size(80, 80).row();
                                    n.button("V", () -> {
                                        pl.item(5, pl.item);
                                        changed = true;
                                        baseDialog.hide();
                                        bd.hide();
                                        bd.remove();
                                        addDialog();
                                        bd.show();
                                    }).pad(50).size(80, 80).left();
                                    n.button("VI", () -> {
                                        pl.item(6, pl.item);
                                        changed = true;
                                        baseDialog.hide();
                                        bd.hide();
                                        bd.remove();
                                        addDialog();
                                        bd.show();
                                    }).pad(50).size(80, 80);
                                    n.button("VII", () -> {
                                        pl.item(7, pl.item);
                                        changed = true;
                                        baseDialog.hide();
                                        bd.hide();
                                        bd.remove();
                                        addDialog();
                                        bd.show();
                                    }).pad(50).size(80, 80);
                                    n.button("VIII", () -> {
                                        pl.item(8, pl.item);
                                        changed = true;
                                        baseDialog.hide();
                                        bd.hide();
                                        bd.remove();
                                        addDialog();
                                        bd.show();
                                    }).pad(50).size(80, 80);
                                    n.button("IX", () -> {
                                        pl.item(9, pl.item);
                                        changed = true;
                                        baseDialog.hide();
                                        bd.hide();
                                        bd.remove();
                                        addDialog();
                                        bd.show();
                                    }).pad(50).size(80, 80);
                                    n.button("X", () -> {
                                        pl.item(10, pl.item);
                                        changed = true;
                                        baseDialog.hide();
                                        bd.hide();
                                        bd.remove();
                                        addDialog();
                                        bd.show();
                                    }).pad(50).size(80, 80);
                                }).grow()).grow();

                                baseDialog.show();

                            }).size(100, 50).left().pad(30);

                            t.button(Icon.trash, () -> {
                                plan.remove(tables.indexOf(t));
                                tables.remove(t);
                                changed = true;
                                t.remove();
                            }).size(100, 50).right().pad(30);

                        }).center().growX();
                    }
                    sq.row();
                }).center().grow().row();

                set.button(Icon.add, () -> {
                    GradePlan gp = new GradePlan(-1, null);
                    plan.add(gp);

                    list.row();

                    list.table(plans -> {
                        tables.add(plans);

                        plans.background(Tex.scrollHorizontal);

                        plans.button("null", () -> {
                            GradePlan pl = plan.get(tables.indexOf(plans));

                            BaseDialog baseDialog = new BaseDialog("@items");
                            baseDialog.addCloseListener();

                            baseDialog.cont.pane(t -> t.table(p -> {
                                p.button(Core.bundle.get("@health"), () -> {
                                    pl.item(pl.levelTo, FItems.healthPower);
                                    changed = true;
                                    baseDialog.hide();
                                    bd.hide();
                                    bd.remove();
                                    addDialog();
                                    bd.show();
                                }).pad(50).size(60, 150).left();
                                p.button(Core.bundle.get("@damage"), () -> {
                                    pl.item(pl.levelTo, FItems.damagePower);
                                    changed = true;
                                    baseDialog.hide();
                                    bd.hide();
                                    bd.remove();
                                    addDialog();
                                    bd.show();
                                }).pad(50).size(60, 150).left();
                                p.button(Core.bundle.get("@reload"), () -> {
                                    pl.item(pl.levelTo, FItems.reloadPower);
                                    changed = true;
                                    baseDialog.hide();
                                    bd.hide();
                                    bd.remove();
                                    addDialog();
                                    bd.show();
                                }).pad(50).size(60, 150).left();
                                p.button(Core.bundle.get("@speed"), () -> {
                                    pl.item(pl.levelTo, FItems.speedPower);
                                    changed = true;
                                    baseDialog.hide();
                                    bd.hide();
                                    bd.remove();
                                    addDialog();
                                    bd.show();
                                }).pad(50).size(60, 150).left();
                                p.button(Core.bundle.get("@again"), () -> {
                                    pl.item(pl.levelTo, FItems.againPower);
                                    changed = true;
                                    baseDialog.hide();
                                    bd.hide();
                                    bd.remove();
                                    addDialog();
                                    bd.show();
                                }).pad(50).size(60, 150).left();
                                p.button(Core.bundle.get("@shield"), () -> {
                                    pl.item(pl.levelTo, FItems.shieldPower);
                                    changed = true;
                                    baseDialog.hide();
                                    bd.hide();
                                    bd.remove();
                                    addDialog();
                                    bd.show();
                                }).pad(50).size(60, 150).left();
                            }).grow()).grow();

                            baseDialog.show();

                        }).size(100, 50).left().pad(30);

                        plans.button("-1", () -> {
                            GradePlan pl = plan.get(tables.indexOf(plans));

                            BaseDialog baseDialog = new BaseDialog("@items");
                            baseDialog.addCloseListener();

                            baseDialog.cont.pane(t -> t.table(n -> {
                                n.button("0", () -> {
                                    pl.item(0, pl.item);
                                    changed = true;
                                    baseDialog.hide();
                                    bd.hide();
                                    bd.remove();
                                    addDialog();
                                    bd.show();
                                }).pad(50).size(80, 80).left();
                                n.button("I", () -> {
                                    pl.item(1, pl.item);
                                    changed = true;
                                    baseDialog.hide();
                                    bd.hide();
                                    bd.remove();
                                    addDialog();
                                    bd.show();
                                }).pad(50).size(80, 80);
                                n.button("II", () -> {
                                    pl.item(2, pl.item);
                                    changed = true;
                                    baseDialog.hide();
                                    bd.hide();
                                    bd.remove();
                                    addDialog();
                                    bd.show();
                                }).pad(50).size(80, 80);
                                n.button("III", () -> {
                                    pl.item(3, pl.item);
                                    changed = true;
                                    baseDialog.hide();
                                    bd.hide();
                                    bd.remove();
                                    addDialog();
                                    bd.show();
                                }).pad(50).size(80, 80);
                                n.button("IV", () -> {
                                    pl.item(4, pl.item);
                                    changed = true;
                                    baseDialog.hide();
                                    bd.hide();
                                    bd.remove();
                                    addDialog();
                                    bd.show();
                                }).pad(50).size(80, 80).row();
                                n.button("V", () -> {
                                    pl.item(5, pl.item);
                                    changed = true;
                                    baseDialog.hide();
                                    bd.hide();
                                    bd.remove();
                                    addDialog();
                                    bd.show();
                                }).pad(50).size(80, 80).left();
                                n.button("VI", () -> {
                                    pl.item(6, pl.item);
                                    changed = true;
                                    baseDialog.hide();
                                    bd.hide();
                                    bd.remove();
                                    addDialog();
                                    bd.show();
                                }).pad(50).size(80, 80);
                                n.button("VII", () -> {
                                    pl.item(7, pl.item);
                                    changed = true;
                                    baseDialog.hide();
                                    bd.hide();
                                    bd.remove();
                                    addDialog();
                                    bd.show();
                                }).pad(50).size(80, 80);
                                n.button("VIII", () -> {
                                    pl.item(8, pl.item);
                                    changed = true;
                                    baseDialog.hide();
                                    bd.hide();
                                    bd.remove();
                                    addDialog();
                                    bd.show();
                                }).pad(50).size(80, 80);
                                n.button("IX", () -> {
                                    pl.item(9, pl.item);
                                    changed = true;
                                    baseDialog.hide();
                                    bd.hide();
                                    bd.remove();
                                    addDialog();
                                    bd.show();
                                }).pad(50).size(80, 80);
                                n.button("X", () -> {
                                    pl.item(10, pl.item);
                                    changed = true;
                                    baseDialog.hide();
                                    bd.hide();
                                    bd.remove();
                                    addDialog();
                                    bd.show();
                                }).pad(50).size(80, 80);
                            }).grow()).grow();

                            baseDialog.show();

                        }).size(100, 50).left().pad(30);

                        plans.button(Icon.trash, () -> {
                            plan.remove(tables.indexOf(plans));
                            tables.remove(plans);
                            plans.remove();
                            changed = true;
                        }).size(100, 50).right().pad(30);
                    }).center().growX();

                }).center().size(100, 50).pad(15);

                set.button("@back", () -> {
                    if (state.isGame() && !net.active()) {
                        state.set(GameState.State.playing);
                    }
                    dialog.hide();
                }).left().size(100, 50).pad(15);

            }).center().growY().growX();


            bd = dialog;
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
            if (changed || usePlan.isEmpty()) {
                usePlan.clear();
                for (GradePlan gp : plan) {
                    usePlan.add(gp);
                }
                changed = false;
            }

            if (usePlan.size >= 1) {
                GradePlan gp = usePlan.first();
                if (gp != null) {
                    levelTo = gp.levelTo;
                    item = gp.item;
                } else {
                    item = null;
                    levelTo = -1;
                }
            } else {
                item = null;
                levelTo = -1;
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
                    usePlan.clear();
                    for (GradePlan gg : plan) {
                        usePlan.add(gg);
                    }
                    changed = false;
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
                boolean li = lastItem != null && items.get(lastItem) >= itemUse && itemUse >= 0;
                boolean le = (out ? level > levelTo : level < levelTo);
                boolean si = usePlan.size > 0;

                if (si && li && le && in && efficiency >= 0) {
                    float adder = Time.delta * edelta() * Math.max(0, efficiency);
                    progress = out ? level > 0 ? progress + adder : constructTime :
                            level >= 10 ? constructTime : progress + adder;
                    if (progress >= constructTime) {
                        if (out) {
                            if (level - 1 <= levelTo) {
                                usePlan.remove(0);
                                if (usePlan.size == 0) {
                                    outing = true;
                                }
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
                                usePlan.remove(0);
                                if (usePlan.size == 0) {
                                    outing = true;
                                }
                            } else {
                                progress = progress % 1f;
                            }
                        }
                    }
                } else if (in && lastUnit != null && !lastUnit.type.isBanned()) {
                    if ((!le || !li) && si) {
                        usePlan.remove(0);
                        if (usePlan.size == 0) {
                            outing = true;
                        }
                    } else {
                        outing = true;
                    }
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
                usePlan.clear();
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
        public boolean acceptPayload(Building source, Payload payload) {
            return this.payload == null && payload instanceof UnitPayload up && grades.indexOf(up.unit.type) >= 0;
        }

        @Override
        public Object config() {
            return item;
        }

        @Override
        public void write(Writes write) {
            super.write(write);

            write.f(progress);
            write.i(lastUnit == null ? -1 : lastUnit.id);
            write.bool(outing);
            write.bool(changed);
            write.i(plan.size);
            for (int i = 0; i < plan.size; i++) {
                write.i(plan.get(i).levelTo);
                TypeIO.writeItem(write, plan.get(i).item);
                write.str(plan.get(i).name);
            }
            write.i(usePlan.size);
            for (int i = 0; i < usePlan.size; i++) {
                write.i(usePlan.get(i).levelTo);
                TypeIO.writeItem(write, usePlan.get(i).item);
                write.str(usePlan.get(i).name);
            }
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);

            progress = read.f();
            lastId = read.i();
            outing = read.bool();
            changed = read.bool();
            int num = read.i();
            GradePlan gp;
            for (int i = 0; i < num; i++) {
                gp = new GradePlan(read.i(), TypeIO.readItem(read));
                gp.name = read.str();
                plan.add(gp);
            }
            num = read.i();
            for (int i = 0; i < num; i++) {
                gp = new GradePlan(read.i(), TypeIO.readItem(read));
                gp.name = read.str();
                usePlan.add(gp);
            }
        }
    }
}
