package Floor.FEntities.FBlock;

import Floor.FTools.BossList;
import Floor.FTools.FUnitUpGrade;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Angles;
import arc.math.Mathf;
import arc.struct.IntSeq;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.core.GameState;
import mindustry.core.Renderer;
import mindustry.entities.Units;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.logic.LogicDialog;
import mindustry.type.StatusEffect;
import mindustry.ui.Bar;
import mindustry.world.Block;

import java.util.HashMap;
import java.util.Map;

import static arc.util.Time.*;
import static mindustry.Vars.*;

public class ElectricFence extends Block {
    public boolean air = false;
    public float maxLength = 400;
    public int maxConnect = 2;
    public float eleDamage = 0.1f;
    public StatusEffect statusEffect = StatusEffects.burning;
    public float statusTime = 240;
    public float maxFenceSize = 90;
    public float backTime = 600;

    public ElectricFence(String name) {
        super(name);

        update = solid = true;
        configurable = true;
        swapDiagonalPlacement = true;
        config(Integer.class, (build, inter) -> {

            ElectricFenceBuild e = (ElectricFenceBuild) build;
            if (e != null) {
                Building b = world.build(inter);
                if (b instanceof ElectricFenceBuild efb) {
                    ElectricFence ef = (ElectricFence) efb.block;
                    if (ef != null) {
                        if (e.builds.indexOf(efb.pos()) >= 0) {
                            if (e.linesMap.get(efb.pos()).broken) {
                                ElectricFenceBuild putter = ef.backTime > backTime ? efb : e;
                                putter.times.put(putter == e ? efb.pos() : e.pos(), putter.linesMap.get(efb.pos()).timer);
                            }
                            e.builds.removeValue(efb.pos());
                            efb.builds.removeValue(e.pos());
                            e.linesMap.remove(efb.pos());
                            efb.linesMap.remove(e.pos());
                        } else if (e.builds.size < maxConnect && efb.builds.size < ef.maxConnect) {
                            e.builds.add(efb.pos());
                            efb.builds.add(e.pos());
                            FenceLine fl = new FenceLine(e.team, Math.min(maxFenceSize, ef.maxFenceSize), e, efb);
                            fl.set(Math.max(ef.backTime, backTime), Math.min(ef.eleDamage, eleDamage), statusEffect,
                                    Math.min(ef.statusTime, statusTime), air && ef.air);
                            if (e.times.get(efb.pos()) != null) {
                                fl.broken = true;
                                fl.timer = e.times.get(efb.pos());
                                e.times.remove(efb.pos());
                            } else if (efb.times.get(e.pos()) != null) {
                                fl.broken = true;
                                fl.timer = efb.times.get(efb.pos());
                                efb.times.remove(e.pos());
                            }
                            e.linesMap.put(efb.pos(), fl);
                            efb.linesMap.put(e.pos(), fl);
                        }
                    }
                }
            }
        });
    }

    protected void setupColor(float satisfaction) {
        Draw.color(Color.white, Pal.powerLight, (1f - satisfaction) * 0.86f + Mathf.absin(3f, 0.1f));
        Draw.alpha(Renderer.laserOpacity);
    }

    public void drawLaser(float x1, float y1, float x2, float y2, int size1, int size2, float thick) {
        float angle1 = Angles.angle(x1, y1, x2, y2),
                vx = Mathf.cosDeg(angle1), vy = Mathf.sinDeg(angle1),
                len1 = size1 * tilesize / 2f - 1.5f, len2 = size2 * tilesize / 2f - 1.5f;

        Drawf.laser(Core.atlas.find(""), Core.atlas.find(""),
                x1 + vx * len1, y1 + vy * len1, x2 - vx * len2, y2 - vy * len2, thick);
    }

    @Override
    public void setBars() {
        super.setBars();

        addBar("connections", (ElectricFenceBuild b) -> new Bar(() ->
                Core.bundle.format("bar.powerlines", b.builds.size, maxConnect),
                () -> Pal.items,
                () -> (float) b.builds.size / (float) maxConnect));
    }

    public class FenceLine {
        public float timer = 0;
        public Team team;
        public float x;
        public float y;
        public float half;
        public float rotate;
        public float backTime;
        public float eleDamage;
        public StatusEffect statusEffect;
        public float statusTime;
        public boolean air;
        public float maxFenceSize;
        public Seq<Unit> stopUnits;
        public Seq<Integer> ids = new Seq<>();
        public boolean broken;
        public float go = 0;

        public FenceLine(Team team, float max, ElectricFenceBuild b1, ElectricFenceBuild b2) {
            float x1 = b1.x, x2 = b2.x, y1 = b1.y, y2 = b2.y;
            this.team = team;
            this.x = (x1 + x2) / 2;
            this.y = (y1 + y2) / 2;
            this.half = (float) (Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)) / 2);
            this.rotate = Angles.angle(x1, y1, x2, y2);

            this.maxFenceSize = max;
            this.broken = false;
            this.stopUnits = new Seq<>();
        }

        public void set(float backTime, float eleDamage, StatusEffect statusEffect, float statusTime, boolean air) {
            this.backTime = backTime;
            this.eleDamage = eleDamage;
            this.statusEffect = statusEffect;
            this.statusTime = statusTime;
            this.air = air;
        }

        public void broken() {
            stopUnits.removeAll(u -> u != null && (u.dead || u.health <= 0));
            go = 0;
            for (Unit u : stopUnits) {
                if (u != null) {
                    if (u instanceof FUnitUpGrade uug) {
                        go = go + u.hitSize * Math.max(1, uug.getSpeedLevel() * 0.75f);
                    } else {
                        go += u.hitSize;
                    }
                }
            }
            if (go >= maxFenceSize) {
                broken = true;
                stopUnits.clear();
            }
        }

        public void update() {
            if (ids.size > 0) {
                for (int i : ids) {
                    stopUnits.add(Groups.unit.getByID(i));
                }
            }

            if (!broken) {
                broken();
            }

            if (!broken) {
                updateUnit();
            } else {
                go = maxFenceSize - 0.01f;
                timer += delta;
                if (timer >= backTime) {
                    broken = false;
                    timer = 0;
                }
            }
        }

        public void updateUnit() {
            Seq<Unit> toStop = new Seq<>();
            Units.nearbyEnemies(team, x, y, half, u -> {
                if (inRange(half, u)) {
                    toStop.add(u);

                    float ro = u.vel.angle() - rotate;
                    float x1 = (float) (Math.cos(Math.toRadians(ro)) * u.vel.len());
                    u.vel.set((float) -(Math.cos(Math.toRadians(rotate)) * x1),
                            (float) -(Math.sin(Math.toRadians(rotate)) * x1));

                    u.damage(eleDamage);
                    u.apply(statusEffect, statusTime);
                }
            });
            stopUnits = toStop;
        }

        public boolean inRange(float len, Unit u) {
            if (BossList.list.indexOf(u.type) < 0) {
                float ux = u.x;
                float uy = u.y;
                float angle1 = Angles.angleDist(rotate, Angles.angle(x, y, ux, uy));
                float len1 = (float) Math.sqrt((ux - x) * (ux - x) + (uy - y) * (uy - y));
                angle1 = Math.min(angle1, 180 - angle1);
                if (Math.sin(Math.toRadians(angle1)) * len1 <= size * 4 && len1 * Math.cos(Math.toRadians(angle1)) <= len) {
                    float ro2 = Angles.angleDist(Angles.angle(ux, uy, x, y), u.vel.angle());
                    float ro4 = Angles.angleDist(rotate, u.vel.angle());
                    float ro3 = Angles.angleDist(Angles.angle(x, y, ux, uy), rotate);
                    boolean close;
                    if (ro4 + ro3 >= 180) {
                        close = ro2 < ro3 + 1;
                    } else {
                        close = ro2 + ro3 < 181;
                    }
                    if (close && air && (u.physref == null || !(u.physref.body.layer == 4))) {
                        return true;
                    } else return close && !air && u.isGrounded();
                }
            }
            return false;
        }
    }

    public class ElectricFenceBuild extends Building {
        protected boolean loaded = true;
        protected final Seq<Float> timers = new Seq<>();
        protected final Seq<Boolean> booleans = new Seq<>();
        public final Map<Integer, Float> times = new HashMap<>();
        public final IntSeq builds = new IntSeq();
        public final ObjectMap<Integer, FenceLine> linesMap = new ObjectMap<>();

        @Override
        public void updateTile() {
            if (dead || health <= 0) {
                builds.each(i -> {
                    ElectricFenceBuild e = (ElectricFenceBuild) world.build(i);
                    e.builds.removeValue(pos());
                    e.linesMap.remove(pos());
                    e.times.remove(pos());
                });
                builds.clear();
                linesMap.clear();
                times.clear();
            } else if (loaded) {
                Seq<Integer> removes = new Seq<>();
                for (int i = 0; i < builds.size; i++) {
                    Building b = world.build(builds.get(i));
                    if (b == null || b.dead || b.health <= 0) {
                        removes.add(builds.get(i));
                    }
                }
                for (Integer i : removes) {
                    builds.removeValue(i);
                    linesMap.remove(i);
                }
                removes.clear();
                times.replaceAll((b, f) -> f + delta);
                for (Integer i : times.keySet()) {
                    Building b = world.build(i);
                    if (times.get(i) >= backTime || b.dead || b.health <= 0 || !b.isAdded()) {
                        removes.add(i);
                    }
                }
                for (Integer i : removes) {
                    times.remove(i);
                }

                for (FenceLine fl : linesMap.values()) {
                    fl.update();
                }
            } else {
                draw();
            }
            super.updateTile();
        }

        @Override
        public boolean onConfigureBuildTapped(Building other) {
            if (other instanceof ElectricFenceBuild && other.within(this, maxLength) && other != this && other.team == team) {
                configure(other.pos());
                return false;
            }
            return true;
        }

        public void drawConfigure() {
            Drawf.circles(x, y, tile.block().size * tilesize / 2f + 1f + Mathf.absin(Time.time, 4f, 1f));
            Drawf.circles(x, y, maxLength);
            Seq<Integer> is = new Seq<>();
            builds.each(i -> {
                Building b = world.build(i);
                if (b != null) {
                    Drawf.square(b.x, b.y, b.block.size * tilesize / 2f + 1f, Pal.place);
                } else {
                    is.add(i);
                }
            });
            for (int i = 0; i < is.size; i++) {
                builds.removeValue(is.get(i));
                linesMap.remove(is.get(i));
                times.remove(is.get(i));
            }
        }

        public boolean hasPos(int pos) {
            for (int p : linesMap.keys()) {
                if (p == pos) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public void draw() {
            super.draw();

            if (isPayload()) return;

            if (linesMap.size != builds.size && !loaded) {
                for (int p = 0; p < builds.size; p++) {
                    ElectricFenceBuild efb = (ElectricFenceBuild) world.build(builds.get(p));
                    if (!hasPos(builds.get(p))) {
                        ElectricFence ef = (ElectricFence) efb.block;
                        FenceLine fl = new FenceLine(team, Math.min(ef.maxFenceSize, maxFenceSize), this, efb);
                        fl.set(Math.max(ef.backTime, backTime), Math.min(ef.eleDamage, eleDamage), statusEffect,
                                Math.min(ef.statusTime, statusTime), air && ef.air);
                        fl.broken = booleans.get(p);
                        fl.timer = timers.get(p);
                        linesMap.put(builds.get(p), fl);
                        efb.linesMap.put(pos(), fl);
                    }
                }
                timers.clear();
                booleans.clear();
            }
            loaded = true;

            for (int i = 0; i < builds.size; i++) {
                Building e = world.build(builds.get(i));
                FenceLine fl = linesMap.get(builds.get(i));
                if (fl != null && e != null) {
                    float thick;
                    if (fl.broken) {
                        thick = 0.01f;
                    } else {
                        thick = 1 - (fl.go / fl.maxFenceSize);
                    }
                    setupColor(thick);
                    Draw.z(Layer.power);
                    drawLaser(x, y, e.x, e.y, size, e.block.size, thick);
                }
            }

            Draw.reset();
        }

        @Override
        public void add() {
            if (!this.added) {
                this.index__all = Groups.all.addIndex(this);
                this.index__build = Groups.build.addIndex(this);
                if (this.power != null) {
                    this.power.graph.checkAdd();
                }

                builds.clear();
                linesMap.clear();
                times.clear();

                this.added = true;
            }
        }

        @Override
        public void write(Writes write) {
            write.i(builds.size);
            for (int i = 0; i < builds.size; i++) {
                write.i(builds.get(i));
            }
            if (linesMap.size == 0) {
                write.i(booleans.size);
                for (int i = 0; i < booleans.size; i++) {
                    write.f(timers.get(i));
                    write.bool(booleans.get(i));
                }
            } else {
                write.i(linesMap.size);
                for (Integer i : linesMap.keys()) {
                    write.f(linesMap.get(i).timer);
                    write.bool(linesMap.get(i).broken);
                }
            }
            write.i(times.size());
            for (Integer i : times.keySet()) {
                write.i(i);
                write.f(times.get(i));
            }
        }

        @Override
        public void read(Reads read, byte revision) {
            int num = read.i();
            for (int i = 0; i < num; i++) {
                builds.add(read.i());
            }
            num = read.i();
            for (int i = 0; i < num; i++) {
                timers.add(read.f());
                booleans.add(read.bool());
            }
            num = read.i();
            for (int i = 0; i < num; i++) {
                times.put(read.i(), read.f());
            }
            loaded = false;
        }
    }
}