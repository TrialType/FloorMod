package Floor.FType.UponFloor;

import Floor.FTools.Corrosion;
import Floor.FTools.RangePure;
import arc.struct.IntMap;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.entities.Units;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;

import static arc.util.Time.delta;
import static mindustry.Vars.*;

public class CorrosionMist {
    private static float reload = 15 * delta;
    public final static Seq<RangePure> changer = new Seq<>();
    public final static IntMap<Integer> clear = new IntMap<>();
    public final static IntMap<Integer> withBoost = new IntMap<>();
    public final static IntMap<BoostWithTime> timeBoost = new IntMap<>();
    public static boolean update = false;

    public static void init() {
        timeBoost.clear();
        changer.clear();
        update = false;
        world.tiles.eachTile(t -> {
            if (!update) {
                if (t.floor() instanceof Corrosion) {
                    update = true;
                }
            }
        });

        if (update) {
            Time.run(reload, CorrosionMist::update);
        }
    }

    public static void update() {
        reload = 15 * delta;
        if (state.map == null || editor.isLoading() || state.isEditor()) return;

        clear.clear();
        withBoost.clear();
        changer.removeAll(r -> !r.couldUse());

        Seq<BoostWithTime> removes = new Seq<>();
        for (BoostWithTime bwt : timeBoost.values()) {
            bwt.go(reload, removes);
        }
        for (BoostWithTime bwt : removes) {
            int index = timeBoost.values().toArray().indexOf(bwt);
            timeBoost.remove(index);
        }

        for (RangePure rp : changer) {
            int plan = rp.plan();
            if (plan == 1) {
                IntMap<Integer> its = rp.protects();
                for (int i : its.keys().toArray().toArray()) {
                    Integer p = clear.get(i);
                    if (p == null) {
                        clear.put(i, its.get(i));
                    } else {
                        clear.put(i, Math.max(its.get(i), p));
                    }
                }
            } else if (plan == 2) {
                IntMap<Integer> bos = rp.withBoost();
                for (int i : bos.keys().toArray().toArray()) {
                    Integer p = withBoost.get(i);
                    if (p == null) {
                        withBoost.put(i, bos.get(i));
                    } else {
                        withBoost.put(i, Math.max(bos.get(i), p));
                    }
                }
            } else if (plan == 3) {
                IntMap<Integer> bos = rp.timeBoost();
                for (int i : bos.keys().toArray().toArray()) {
                    BoostWithTime p = timeBoost.get(i);
                    if (p == null) {
                        timeBoost.put(i, new BoostWithTime(i, bos.get(i)));
                    } else {
                        float last = p.time;
                        if (bos.get(i) > last) {
                            p.setTime(bos.get(i));
                        }
                    }
                }
            } else if (plan == 5) {
                IntMap<Integer> bos = rp.withBoost();
                for (int i : bos.keys().toArray().toArray()) {
                    Integer p = withBoost.get(i);
                    if (p == null) {
                        withBoost.put(i, bos.get(i));
                    } else {
                        withBoost.put(i, Math.max(bos.get(i), p));
                    }
                }

                bos = rp.timeBoost();
                for (int i : bos.keys().toArray().toArray()) {
                    BoostWithTime p = timeBoost.get(i);
                    if (p == null) {
                        timeBoost.put(i, new BoostWithTime(i, bos.get(i)));
                    } else {
                        float last = p.time;
                        if (bos.get(i) > last) {
                            p.setTime(bos.get(i));
                        }
                    }
                }
            }

            if (rp.protectPos() > 0) {
                clear.put(rp.protectPos(), rp.protectLevel());
            }
        }


        Units.nearby(0, 0, world.width() * 8, world.height() * 8, u -> {
            Tile t = u.tileOn();
            if (t != null && clear.keys().toArray().indexOf(t.pos()) < 0) {
                Floor f = t.floor();
                if (f instanceof Corrosion) {
                    BoostWithTime bo = timeBoost.get(t.pos());
                    Integer tt = withBoost.get(t.pos());
                    float boost;
                    if (bo == null) {
                        if (tt == null) {
                            boost = 0;
                        } else {
                            boost = tt;
                        }
                    } else {
                        if (tt == null) {
                            boost = bo.level;
                        } else {
                            boost = Math.max(tt, bo.level);
                        }
                    }

                    Integer po = clear.get(t.pos());
                    if (po == null || po < boost + 1) {
                        u.apply(f.status, 60);
                    }
                }
            }
        });

        indexer.allBuildings(world.width() * 4, world.height() * 4, Math.max(world.width(), world.height()) * 6, b -> {
            Tile t = world.tileWorld(b.x, b.y);
            if (t != null) {
                Floor f = t.floor();
                if (f instanceof Corrosion c) {
                    BoostWithTime bo = timeBoost.get(t.pos());
                    Integer tt = withBoost.get(t.pos());
                    float boost;
                    if (bo == null) {
                        if (tt == null) {
                            boost = 0;
                        } else {
                            boost = tt;
                        }
                    } else {
                        if (tt == null) {
                            boost = bo.level;
                        } else {
                            boost = Math.max(tt, bo.level);
                        }
                    }

                    Integer po = clear.get(t.pos());
                    if (po == null || po < boost + 1) {
                        b.damage(Math.max(0.5f / 15, b.maxHealth() / c.baseDamage()) * (boost + 1) * 15);
                    }
                }
            }
        });

        Time.run(reload, CorrosionMist::update);
    }

    public static class BoostWithTime {
        public int level;
        public float time;

        public BoostWithTime(int l, float t) {
            level = l;
            time = t;
        }

        public void go(float time, Seq<BoostWithTime> removes) {
            this.time -= time;
            if (this.time <= 0) {
                removes.add(this);
            }
        }

        public void setTime(float t) {
            this.time = t;
        }
    }
}
