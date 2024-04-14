package Floor.FType.UponFloor;

import Floor.FContent.FStatusEffects;
import Floor.FTools.Corrosion;
import Floor.FTools.RangePure;
import arc.struct.IntMap;
import arc.struct.IntSeq;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.entities.Units;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;

import static arc.util.Time.delta;
import static mindustry.Vars.*;

public class CorrosionMist {
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
            Time.run(delta, CorrosionMist::update);
        }
    }

    @SuppressWarnings({"ReplaceNullCheck"})
    public static void update() {
        float reload = 15 * delta;
        if (!state.isGame() || state.isEditor()) return;

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
                IntSeq bos = (IntSeq) rp.timeBoost()[0];
                int level = (int) rp.timeBoost()[1];
                float time = (float) rp.timeBoost()[2];
                for (int i : bos.toArray()) {
                    BoostWithTime p = timeBoost.get(i);
                    if (p == null) {
                        timeBoost.put(i, new BoostWithTime(level, time));
                    } else {
                        if (time > p.time) {
                            p.setTime(time);
                        }
                    }
                }
            } else if (plan == 4) {
                IntMap<Integer> bos = rp.withBoost();
                for (int i : bos.keys().toArray().toArray()) {
                    Integer p = withBoost.get(i);
                    if (p == null) {
                        withBoost.put(i, bos.get(i));
                    } else {
                        withBoost.put(i, Math.max(bos.get(i), p));
                    }
                }

                IntSeq boos = (IntSeq) rp.timeBoost()[0];
                int level = (int) rp.timeBoost()[1];
                float time = (float) rp.timeBoost()[2];
                for (int i : boos.toArray()) {
                    BoostWithTime p = timeBoost.get(i);
                    if (p == null) {
                        timeBoost.put(i, new BoostWithTime(level, time));
                    } else {
                        if (time > p.time) {
                            p.setTime(time);
                        }
                    }
                }
            }

            if (rp.protectPos() > 0) {
                clear.put(rp.protectPos(), rp.protectLevel());
            }
        }

        Units.nearby(0, 0, world.width() * 8, world.height() * 8, u -> {
            Tile t = world.tileWorld(u.x, u.y);
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
                    if (po == null || po < c.corrosionLevel() * (boost + 1)) {
                        u.apply(f.status, 60);
                        if (po != null && po > 0) {
                            for (int i = 0; i < c.corrosionLevel() * (boost + 1) - po; i++) {
                                u.apply(FStatusEffects.catalyzeI, 1);
                            }
                        }
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
                    if (po == null || po < c.corrosionLevel() * (boost + 1)) {
                        float boo = c.corrosionLevel() * (boost + 1);
                        if (po != null) {
                            boo = boo - po;
                        }
                        b.damage(Math.max(0.5f / 15, b.maxHealth() / c.baseDamage()) * boo * 15);
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
