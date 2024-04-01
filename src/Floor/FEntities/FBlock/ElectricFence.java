package Floor.FEntities.FBlock;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Angles;
import arc.math.Mathf;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.StatusEffects;
import mindustry.core.Renderer;
import mindustry.entities.Units;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;
import mindustry.ui.Bar;
import mindustry.world.Block;

import java.util.HashMap;
import java.util.Map;

import static arc.util.Time.*;
import static mindustry.Vars.*;

public class ElectricFence extends Block {
    //private final static FenceNet owner = new FenceNet();
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
                if (e.team == b.team && b instanceof ElectricFenceBuild efb) {
                    ElectricFence ef = (ElectricFence) efb.block;
                    if(ef != null){
                        if (e.builds.indexOf(efb) >= 0) {
                            if (e.linesMap.get(efb).broken) {
                                ElectricFenceBuild putter = efb.backTimes > e.backTimes ? efb : e;
                                putter.times.put(putter == e ? efb : e, putter.linesMap.get(efb).timer);
                            }
                            e.builds.remove(efb);
                            efb.builds.remove(e);
                            e.linesMap.remove(efb);
                            efb.linesMap.remove(e);
                        } else if (e.builds.size < maxConnect && efb.builds.size < ef.maxConnect) {
                            e.builds.add(efb);
                            efb.builds.add(e);
                            FenceLine fl = new FenceLine(e.team, Math.min(maxFenceSize, ef.maxFenceSize), e, efb);
                            fl.set(Math.max(ef.backTime, backTime), Math.min(ef.eleDamage, eleDamage), statusEffect,
                                    Math.min(ef.statusTime, statusTime), air && ef.air);
                            if (e.times.get(efb) != null) {
                                fl.broken = true;
                                fl.timer = e.times.get(efb);
                                e.times.remove(efb);
                            } else if (efb.times.get(e) != null) {
                                fl.broken = true;
                                fl.timer = efb.times.get(efb);
                                efb.times.remove(e);
                            }
                            e.linesMap.put(efb, fl);
                            efb.linesMap.put(e, fl);
                        }
                    }
                    //FenceLine fl = new FenceLine(build.team, Math.max(e.maxFenceSizes, efb.maxFenceSizes), e, efb);
//                    if (owner.find(fl) == null && e.builds.size < maxConnect && efb.builds.size < efb.maxConnects) {
//                        fl.set(backTime, eleDamage, statusEffect, statusTime, air);
//                        e.builds.add(efb);
//                        efb.builds.add(e);
//                        owner.addLine(fl);
//                        owner.addBuild(e);
//                        owner.addBuild(efb);
//                    } else if (e.builds.indexOf(efb) >= 0) {
//                        e.builds.remove(efb);
//                        efb.builds.remove(e);
//                        owner.removeLine(fl);
//                        owner.removeBuild(e);
//                        owner.removeBuild(efb);
//                    }
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

    //    public static class Line2 {
//        public float point;
//        public float halfLength;
//
//        public Line2(float p, float l) {
//            point = p;
//            halfLength = l;
//        }
//
//        public boolean equals(Line2 l2) {
//            return equals(l2.point, l2.halfLength);
//        }
//
//        public boolean equals(float point, float halfLength) {
//            return Math.round(this.point) == Math.round(point) && Math.round(this.halfLength) == Math.round(halfLength);
//        }
//
//        public void write(Writes write) {
//            write.f(point);
//            write.f(halfLength);
//        }
//
//        public void read(Reads read) {
//            point = read.f();
//            halfLength = read.f();
//        }
//    }
//
//    public static class FenceNet {
//        public static boolean run = false;
//        //public final Map<Line2, FenceLine> lines = new HashMap<>();
//        public final Seq<FenceLine> lines = new Seq<>();
//        public final Seq<ElectricFenceBuild> builds = new Seq<>();
//
//        public void addBuild(ElectricFenceBuild b) {
//            builds.add(b);
//        }
//
//        public void removeBuild(ElectricFenceBuild build) {
//            builds.remove(build);
//        }
//
//        public void addLine(FenceLine fl) {
//            lines.put(fl.point, fl);
//        }
//
//        public void addLine(FenceLine fl) {
//            lines.add(fl);
//        }
//
//        public void removeLine(Line2 l) {
//            Line2 rl = null;
//            for (Line2 l2 : lines.keySet()) {
//                if (l2.equals(l)) {
//                    rl = l2;
//                    break;
//                }
//            }
//            lines.remove(rl);
//        }
//
//      public void removeLine(FenceLine fl) {
//          FenceLine ff = null;
//          for (FenceLine l2 : lines) {
//              if (l2.equals(fl)) {
//                  ff = l2;
//                  break;
//              }
//          }
//          lines.remove(ff);
//      }
//
//        public FenceLine find(Line2 l) {
//            return find(l.point, l.halfLength);
//        }
//
//      public FenceLine find(FenceLine f) {
//          FenceLine fl = null;
//          for (FenceLine l2 : lines) {
//              if (l2.equals(f)) {
//                  fl = l2;
//              }
//          }
//          return fl;
//        }
//
//        public void update() {
//            run = false;
//
//            if (lines.isEmpty()) {
//                return;
//            }
//
//            //Seq<Line2> integers = new Seq<>();
//            Seq<FenceLine> integers = new Seq<>();
//            lines.forEach(l -> {
//                ElectricFenceBuild[] bs = l.twoPoint();
//                if (bs[0] == null || bs[1] == null) {
//                    integers.add(l);
//                } else {
//                    l.update();
//                }
//            });
//            for (Line2 l2 : integers) {
//                lines.remove(l2);
//            }
//          for (FenceLine l2 : integers) {
//              lines.remove(l2);
//          }
//
//      }
//
//        public void write(Writes write) {
//            write.i(lines.size);
//            lines.forEach(l -> {
//                l.write(write);
//                f.write(write);
//            });
//            lines.clear();
//            builds.clear();
//        }
//
//        public void read(Reads read) {
//            int len = read.i();
//            for (int i = 0; i < len; i++) {
//                //Line2 l2 = new Line2(0, 0);
//                //l2.read(read);
//                FenceLine fl = new FenceLine();
//                //fl.point = l2;
//                fl.read(read);
//                //lines.put(l2, fl);
//                lines.add(fl);
//            }
//        }
//  }
//        public static class FenceLine {
//        public float timer = 0;
//        public Team team;
//        public ElectricFenceBuild e1;
//        public int p1 = -1;
//        public ElectricFenceBuild e2;
//        public int p2 = -1;
//        public float x;
//        public float y;
//        public float half;
//        public float rotate;
//        public float backTime;
//        public float eleDamage;
//        public StatusEffect statusEffect;
//        public float statusTime;
//        public boolean air;
//        //public Line2 point;
//        public float maxFenceSize;
//        public Seq<Unit> stopUnits;
//        public Seq<Integer> ids = new Seq<>();
//        public boolean broken;
//        public float go = 0;
//
//        public ElectricFenceBuild[] twoPoint() {
//            ElectricFenceBuild[] builds = new ElectricFenceBuild[2];
//            if (p1 > 0 && p2 > 0) {
//                e1 = (ElectricFenceBuild) world.build(p1);
//                e2 = (ElectricFenceBuild) world.build(p2);
//                p1 = p2 = -1;
//            }
//            builds[0] = couldUse(e1) ? e1 : null;
//            builds[1] = couldUse(e2) ? e2 : null;
//            return builds;
//        }
//
//        public boolean couldUse(ElectricFenceBuild b) {
//            return b != null && !(b.dead || b.health <= 0 || !b.isAdded());
//        }
//
//        public FenceLine(Team team, float max, ElectricFenceBuild b1, ElectricFenceBuild b2) {
//            float x1 = Math.min(b1.x, b2.x), x2 = Math.max(b1.x, b2.x), y1 = Math.min(b1.y, b2.y), y2 = Math.max(b1.y, b2.y);
//            this.team = team;
//            e1 = b1;
//            e2 = b2;
//            x = (x1 + x2) / 2;
//            y = (y1 + y2) / 2;
//            half = (float) (Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)) / 2);
//            rotate = Angles.angle(x1, y1, x2, y2);
//            //float p = (1 + x) * Math.max(world.width(), world.height()) * 8 * 180 + (1 + y) * 180 + rotate % 180;
//            //point = new Line2(p, half);
//
//            maxFenceSize = max;
//            broken = false;
//            stopUnits = new Seq<>();
//        }
//
//        public FenceLine() {
//            maxFenceSize = 40;
//            broken = false;
//            stopUnits = new Seq<>();
//        }
//
//        public void set(float backTime, float eleDamage, StatusEffect statusEffect, float statusTime, boolean air) {
//            this.backTime = backTime;
//            this.eleDamage = eleDamage;
//            this.statusEffect = statusEffect;
//            this.statusTime = statusTime;
//            this.air = air;
//        }
//
//        public void broken() {
//            stopUnits.removeAll(u -> u != null && (u.dead || u.health <= 0));
//            go = 0;
//            for (Unit u : stopUnits) {
//                if (u != null) {
//                    go += u.hitSize;
//                }
//            }
//            if (go >= maxFenceSize) {
//                broken = true;
//                stopUnits.clear();
//            }
//        }
//
//        public void update() {
//            if (ids.size > 0) {
//                for (int i : ids) {
//                    stopUnits.add(Groups.unit.getByID(i));
//                }
//            }
//
//            if (!broken) {
//                broken();
//            }
//
//            if (!broken) {
//                updateUnit();
//            } else {
//                timer += delta;
//                if (timer >= backTime) {
//                    broken = false;
//                    timer = 0;
//                }
//            }
//        }
//
//        public void updateUnit() {
//            float dx = (float) Math.cos(Math.toRadians(rotate)) * half;
//            float dy = (float) Math.sin(Math.toRadians(rotate)) * half;
//            float len = (float) Math.sqrt(dx * dx + dy * dy);
//            Seq<Unit> toStop = new Seq<>();
//            Units.nearbyEnemies(team, x, y, len, u -> {
//                if (inRange(len, u)) {
//                    toStop.add(u);
//
//                    float ro = u.vel.angle() - rotate;
//                    float x1 = (float) (Math.cos(Math.toRadians(ro)) * u.vel.len());
//                    u.vel.set((float) -(Math.cos(Math.toRadians(rotate)) * x1),
//                            (float) -(Math.sin(Math.toRadians(rotate)) * x1));
//
//                    u.damage(eleDamage);
//                    u.apply(statusEffect, statusTime);
//                }
//            });
//            stopUnits = toStop;
//        }
//
//        public boolean inRange(float len, Unit u) {
//            float len2 = u.vel.len();
//            float ro = Angles.angleDist(u.vel.angle(), rotate);
//            double l = len2 * Math.sin(Math.toRadians(ro));
//
//            if (l > 0 || (stopUnits.indexOf(u) >= 0 && l >= 0)) {
//                float ux = u.x;
//                float uy = u.y;
//                float angle1 = Angles.angleDist(rotate, Angles.angle(x, y, ux, uy));
//                float len1 = (float) Math.sqrt((ux - x) * (ux - x) + (uy - y) * (uy - y));
//
//                angle1 = Math.min(angle1, 180 - angle1);
//                if (Math.sin(Math.toRadians(angle1)) * len1 <= 15f && len * Math.cos(Math.toRadians(angle1)) <= half) {
//                    if (air && !(u.physref.body.layer == 4)) {
//                        return true;
//                    } else return !air && u.isGrounded();
//                }
//                return false;
//            }
//            return false;
//        }
//
//        public boolean equals(FenceLine fl) {
//            if (fl == this) {
//                return true;
//            } else {
//                return Math.round(fl.x) == Math.round(x) && Math.round(fl.y) == Math.round(y) &&
//                        Math.round(fl.half) == Math.round(half) && Math.round(fl.rotate) == Math.round(rotate) &&
//                        fl.e1 == e1 && fl.e2 == e2;
//            }
//        }
//
//        public void write(Writes write) {
//            write.f(timer);
//            TypeIO.writeTeam(write, team);
//            write.f(x);
//            write.f(y);
//            write.f(half);
//            write.f(rotate);
//            write.f(maxFenceSize);
//            write.bool(broken);
//            write.f(backTime);
//            write.f(eleDamage);
//            write.str(statusEffect.name.split("-")[statusEffect.name.split("-").length - 1]);
//            write.f(statusTime);
//            write.bool(air);
//            write.i(stopUnits.size);
//            for (Unit u : stopUnits) {
//                write.i(u.id);
//            }
//            write.i(e1.pos());
//            write.i(e2.pos());
//        }
//
//        public void read(Reads read) {
//            timer = read.f();
//            team = TypeIO.readTeam(read);
//            x = read.f();
//            y = read.f();
//            half = read.f();
//            rotate = read.f();
//            maxFenceSize = read.f();
//            broken = read.bool();
//            backTime = read.f();
//            eleDamage = read.f();
//            statusEffect = content.statusEffect(read.str());
//            statusTime = read.f();
//            air = read.bool();
//            int num = read.i();
//            for (int i = 0; i < num; i++) {
//                ids.add(read.i());
//            }
//            p1 = read.i();
//            p2 = read.i();
//        }
//    }
    public static class FenceLine {
        public float timer = 0;
        public Team team;
        public boolean updated = false;
        public ElectricFenceBuild e1;
        public ElectricFenceBuild e2;
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

//        public ElectricFenceBuild[] twoPoint() {
//            ElectricFenceBuild[] builds = new ElectricFenceBuild[2];
//            builds[0] = couldUse(e1) ? e1 : null;
//            builds[1] = couldUse(e2) ? e2 : null;
//            return builds;
//        }
//
//        public boolean couldUse(ElectricFenceBuild b) {
//            return b != null && !(b.dead || b.health <= 0 || !b.isAdded());
//        }

        public FenceLine(Team team, float max, ElectricFenceBuild b1, ElectricFenceBuild b2) {
            float x1 = Math.min(b1.x, b2.x), x2 = Math.max(b1.x, b2.x), y1 = Math.min(b1.y, b2.y), y2 = Math.max(b1.y, b2.y);
            this.team = team;
            e1 = b1;
            e2 = b2;
            x = (x1 + x2) / 2;
            y = (y1 + y2) / 2;
            half = (float) (Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)) / 2);
            rotate = Angles.angle(x1, y1, x2, y2);
            //float p = (1 + x) * Math.max(world.width(), world.height()) * 8 * 180 + (1 + y) * 180 + rotate % 180;
            //point = new Line2(p, half);

            maxFenceSize = max;
            broken = false;
            stopUnits = new Seq<>();
        }

//        public FenceLine() {
//            maxFenceSize = 40;
//            broken = false;
//            stopUnits = new Seq<>();
//        }

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
                    go += u.hitSize;
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
            float dx = (float) Math.cos(Math.toRadians(rotate)) * half;
            float dy = (float) Math.sin(Math.toRadians(rotate)) * half;
            float len = (float) Math.sqrt(dx * dx + dy * dy);
            Seq<Unit> toStop = new Seq<>();
            Units.nearbyEnemies(team, x, y, len, u -> {
                if (inRange(len, u)) {
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
            float len2 = u.vel.len();
            float ro = Angles.angleDist(u.vel.angle(), rotate);
            double l = len2 * Math.sin(Math.toRadians(ro));

            if (l > 0 || (stopUnits.indexOf(u) >= 0 && l >= 0)) {
                float ux = u.x;
                float uy = u.y;
                float angle1 = Angles.angleDist(rotate, Angles.angle(x, y, ux, uy));
                float len1 = (float) Math.sqrt((ux - x) * (ux - x) + (uy - y) * (uy - y));

                angle1 = Math.min(angle1, 180 - angle1);
                if (Math.sin(Math.toRadians(angle1)) * len1 <= 15f && len * Math.cos(Math.toRadians(angle1)) <= half) {
                    if (air && !(u.physref.body.layer == 4)) {
                        return true;
                    } else return !air && u.isGrounded();
                }
                return false;
            }
            return false;
        }

//        public boolean equals(FenceLine fl) {
//            if (fl == this) {
//                return true;
//            } else {
//                return Math.round(fl.x) == Math.round(x) && Math.round(fl.y) == Math.round(y) &&
//                        Math.round(fl.half) == Math.round(half) && Math.round(fl.rotate) == Math.round(rotate) &&
//                        fl.e1 == e1 && fl.e2 == e2;
//            }
//        }
    }

    public class ElectricFenceBuild extends Building {
        private final Seq<Integer> tid = new Seq<>();
        private final Seq<Float> tts = new Seq<>();
        private final Seq<Integer> ids = new Seq<>();
        public final Map<ElectricFenceBuild, Float> times = new HashMap<>();
        public final Seq<ElectricFenceBuild> builds = new Seq<>();
        public final ObjectMap<ElectricFenceBuild, FenceLine> linesMap = new ObjectMap<>();
        public final Seq<Float> timers = new Seq<>();
        public final Seq<Boolean> booleans = new Seq<>();
        public float maxFenceSizes;
        public float maxConnects;
        public float backTimes;
        public float eleDamages;
        public float statusTimes;
        public boolean airs;

        @Override
        public void updateTile() {
            if (tid.size > 0) {
                for (int i = 0; i < tid.size; i++) {
                    times.put((ElectricFenceBuild) world.build(tid.get(i)), tts.get(i));
                }
                tid.clear();
                tts.clear();
            }
            if (ids.size > 0) {
                for (int i = 0; i < ids.size; i++) {
                    ElectricFenceBuild efb = (ElectricFenceBuild) world.build(ids.get(i));
                    if (builds.indexOf(efb) < 0) {
                        FenceLine fl = new FenceLine(team, Math.min(efb.maxFenceSizes, maxFenceSizes), this, efb);
                        fl.set(Math.max(efb.backTimes, backTimes), Math.min(efb.eleDamages, eleDamages), statusEffect,
                                Math.min(efb.statusTimes, statusTimes), airs && efb.airs);
                        fl.broken = booleans.get(i);
                        fl.timer = timers.get(i);
                        builds.add(efb);
                        efb.builds.add(this);
                        linesMap.put(efb, fl);
                        efb.linesMap.put(this, fl);
                    }
                }
                booleans.clear();
                timers.clear();
                ids.clear();
            }

            Seq<ElectricFenceBuild> removes = new Seq<>();
            times.replaceAll((b, f) -> f + delta);
            for (ElectricFenceBuild build : times.keySet()) {
                if (build.dead || build.health <= 0 || !build.added || times.get(build) >= backTimes) {
                    removes.add(build);
                }
            }
            for (ElectricFenceBuild build : removes) {
                times.remove(build);
            }
            removes.clear();
            builds.removeAll(b -> {
                if (b.dead || b.health <= 0 || !b.added) {
                    removes.add(b);
                    return true;
                }
                return false;
            });
            for (ElectricFenceBuild efb : removes) {
                linesMap.remove(efb);
            }

            if (added && !dead && health > 0 && efficiency > 0) {
//                if (!FenceNet.run) {
//                    Time.run(delta / 2, owner::update);
//                    FenceNet.run = true;
//                }
                for (FenceLine fl : linesMap.values()) {
                    if (!fl.updated) {
                        fl.updated = true;
                        fl.update();
                    } else {
                        fl.updated = false;
                    }
                }
            } else {
                for (ElectricFenceBuild b : builds) {
                    b.builds.remove(this);
                    b.linesMap.remove(this);
                }
                builds.clear();
                linesMap.clear();

                //owner.removeBuild(this);
            }
            super.updateTile();
        }

        @Override
        public boolean onConfigureBuildTapped(Building other) {
            if (other instanceof ElectricFenceBuild && other.within(this, maxLength) && other != this) {
                configure(other.pos());
                return false;
            }
            return true;
        }

        public void drawConfigure() {
            Drawf.circles(x, y, tile.block().size * tilesize / 2f + 1f + Mathf.absin(Time.time, 4f, 1f));
            Drawf.circles(x, y, maxLength);
            builds.each(i -> Drawf.square(i.x, i.y, i.block.size * tilesize / 2f + 1f, Pal.place));
        }

        @Override
        public void draw() {
            super.draw();

            if (isPayload()) return;

            for (int i = 0; i < builds.size; i++) {
                ElectricFenceBuild link = builds.get(i);
                FenceLine fl = linesMap.get(link);
                if (!fl.broken) {
                    //FenceLine f = owner.find(fl.point);
                    //FenceLine f = owner.find(fl);
                    setupColor(1 - (fl.go / fl.maxFenceSize));
                    Draw.z(Layer.power);
                    drawLaser(x, y, link.x, link.y, size, link.block.size, 1 - (fl.go / fl.maxFenceSize));
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
                maxFenceSizes = maxFenceSize;
                maxConnects = maxConnect;
                backTimes = backTime;
                eleDamages = eleDamage;
                statusTimes = statusTime;
                airs = air;

                this.added = true;
            }
        }

        @Override
        public void remove() {
            if (this.added) {
                Groups.all.removeIndex(this, this.index__all);
                this.index__all = -1;
                Groups.build.removeIndex(this, this.index__build);
                this.index__build = -1;
                if (this.sound != null) {
                    this.sound.stop();
                }

                //owner.removeBuild(this);
                for (ElectricFenceBuild efb : builds) {
                    efb.builds.remove(this);
                    efb.linesMap.remove(this);
                }
                times.clear();
                builds.clear();
                linesMap.clear();

                this.added = false;
            }
        }

        @Override
        public void write(Writes write) {
            write.i(times.size());
            for (ElectricFenceBuild e : times.keySet()) {
                write.i(e.pos());
                write.f(times.get(e));
            }
            //write.i(owner.lines.size());
            //write.i(owner.lines.size);
            write.i(builds.size);
            for (int i = 0; i < builds.size; i++) {
                write.i(builds.get(i).pos());
                //FenceLine fl = owner.lines.get(i);
                FenceLine fl = linesMap.get(builds.get(i));
                write.f(fl.timer);
                //TypeIO.writeTeam(write, fl.team);
                //write.f(fl.x);
                //write.f(fl.y);
                //write.f(fl.half);
                //write.f(fl.rotate);
                //write.f(fl.maxFenceSize);
                write.bool(fl.broken);
                //write.f(fl.backTime);
                //write.f(fl.eleDamage);
                //write.str(fl.statusEffect.name.split("-")[statusEffect.name.split("-").length - 1]);
                //write.f(fl.statusTime);
                //write.bool(fl.air);
                //write.i(fl.e1 == null ? fl.p1 : fl.e1.pos());
                //write.i(fl.e2 == null ? fl.p2 : fl.e2.pos());
            }
//            owner.lines.forEach(l -> {
//                //l.write(write);
//                //f.write(write);
//            });
//            write.i(builds.size);
//            for (ElectricFenceBuild b : builds) {
//                write.i(b.pos());
//            }
            write.f(maxFenceSizes);
            write.f(maxConnects);
            write.f(backTimes);
            write.f(eleDamages);
            write.f(statusTimes);
            write.bool(airs);
        }

        @Override
        public void read(Reads read, byte revision) {
            int num = read.i();
            for (int i = 0; i < num; i++) {
                tid.add(read.i());
                tts.add(read.f());
            }
            num = read.i();
            for (int i = 0; i < num; i++) {
                ids.add(read.i());
                //Line2 l2 = new Line2(0, 0);
                //l2.read(read);
                //fl.point = l2;
                //fl.read(read);
                timers.add(read.f());
                //fl.team = TypeIO.readTeam(read);
                //fl.x = read.f();
                //fl.y = read.f();
                //fl.half = read.f();
                //fl.rotate = read.f();
                //fl.maxFenceSize = read.f();
                booleans.add(read.bool());
                //fl.backTime = read.f();
                //fl.eleDamage = read.f();
                //fl.statusEffect = content.statusEffect(read.str());
                //fl.statusTime = read.f();
                //fl.air = read.bool();
//                fl.p1 = read.i();
//                fl.p2 = read.i();
//                if (owner.find(l2) == null) {
//                    owner.lines.put(l2, fl);
//                }
//                if (owner.find(fl) == null) {
//                    owner.lines.add(fl);
//                }
            }
//            num = read.i();
//            for (int i = 0; i < num; i++) {
//                ids.add(read.i());
//            }
            maxFenceSizes = read.f();
            maxConnects = read.f();
            backTimes = read.f();
            eleDamages = read.f();
            statusTimes = read.f();
            airs = read.bool();
            //owner.addBuild(this);
        }
    }
}