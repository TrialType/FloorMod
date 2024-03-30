package Floor.FEntities.FBlock;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Angles;
import arc.math.Mathf;
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
import mindustry.io.TypeIO;
import mindustry.type.StatusEffect;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.power.PowerGraph;

import java.util.HashMap;
import java.util.Map;

import static arc.util.Time.*;
import static mindustry.Vars.*;

public class ElectricFence extends Block {
    private final static FenceNet owner = new FenceNet();
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
                    FenceLine fl = new FenceLine(build.team, Math.max(e.maxFenceSizes, efb.maxFenceSizes), e, efb);
                    if (owner.find(fl.point) == null && e.builds.size < maxConnect && efb.builds.size < efb.maxConnects) {
                        fl.set(backTime, eleDamage, statusEffect, statusTime, air);
                        e.builds.add(efb);
                        efb.builds.add(e);
                        owner.addLine(fl);
                        owner.addBuild(e);
                        owner.addBuild(efb);
                    } else if (e.builds.indexOf(efb) >= 0) {
                        e.builds.remove(efb);
                        efb.builds.remove(e);
                        owner.removeLine(fl.point);
                        owner.removeBuild(e);
                        owner.removeBuild(efb);
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

    public static class Line2 {
        public float point;
        public float halfLength;

        public Line2(float p, float l) {
            point = p;
            halfLength = l;
        }

        public boolean equals(Line2 l2) {
            return equals(l2.point, l2.halfLength);
        }

        public boolean equals(float point, float halfLength) {
            return Math.round(this.point) == Math.round(point) && Math.round(this.halfLength) == Math.round(halfLength);
        }

        public void write(Writes write) {
            write.f(point);
            write.f(halfLength);
        }

        public void read(Reads read) {
            point = read.f();
            halfLength = read.f();
        }
    }

    public static class FenceNet {
        public static boolean run = false;
        public final Map<Line2, FenceLine> lines = new HashMap<>();
        public final Seq<ElectricFenceBuild> builds = new Seq<>();

        public void addBuild(ElectricFenceBuild b) {
            builds.add(b);
        }

        public void removeBuild(ElectricFenceBuild build) {
            builds.remove(build);
        }

        public void addLine(FenceLine fl) {
            lines.put(fl.point, fl);
        }

        public void removeLine(Line2 l) {
            Line2 rl = null;
            for (Line2 l2 : lines.keySet()) {
                if (l2.equals(l)) {
                    rl = l2;
                    break;
                }
            }
            lines.remove(rl);
        }

        public FenceLine find(Line2 l) {
            return find(l.point, l.halfLength);
        }

        public FenceLine find(float point, float l) {
            FenceLine fl = null;
            for (Line2 l2 : lines.keySet()) {
                if (l2.equals(point, l)) {
                    fl = lines.get(l2);
                }
            }
            return fl;
        }

        public void update() {
            run = false;

            if (lines.isEmpty()) {
                return;
            }

            Seq<Line2> integers = new Seq<>();
            lines.forEach((i, l) -> {
                ElectricFenceBuild[] bs = l.twoPoint();
                if (bs[0] == null || bs[1] == null) {
                    integers.add(i);
                } else {
                    l.update();
                }
            });
            for (Line2 l2 : integers) {
                lines.remove(l2);
            }

        }

        public void write(Writes write) {
            write.i(lines.size());
            lines.forEach((l, f) -> {
                l.write(write);
                f.write(write);
            });
            lines.clear();
            builds.clear();
        }

        public void read(Reads read) {
            int len = read.i();
            for (int i = 0; i < len; i++) {
                Line2 l2 = new Line2(0, 0);
                l2.read(read);
                FenceLine fl = new FenceLine();
                fl.point = l2;
                fl.read(read);
                lines.put(l2, fl);
            }
        }
    }

    public static class FenceLine {
        protected float timer = 0;
        protected Team team;
        protected ElectricFenceBuild e1;
        private int p1 = -1;
        protected ElectricFenceBuild e2;
        private int p2 = -1;
        private float x;
        private float y;
        private float half;
        private float rotate;
        public float backTime;
        public float eleDamage;
        public StatusEffect statusEffect;
        public float statusTime;
        public boolean air;
        public Line2 point;
        public float maxFenceSize;
        public Seq<Unit> stopUnits;
        public Seq<Integer> ids = new Seq<>();
        public boolean broken;
        public float go = 0;

        public ElectricFenceBuild[] twoPoint() {
            ElectricFenceBuild[] builds = new ElectricFenceBuild[2];
            if (p1 > 0 && p2 > 0) {
                e1 = (ElectricFenceBuild) world.build(p1);
                e2 = (ElectricFenceBuild) world.build(p2);
                p1 = p2 = -1;
            }
            builds[0] = couldUse(e1) ? e1 : null;
            builds[1] = couldUse(e2) ? e2 : null;
            return builds;
        }

        public boolean couldUse(ElectricFenceBuild b) {
            return b != null && !(b.dead || b.health <= 0 || !b.isAdded());
        }

        public FenceLine(Team team, float max, ElectricFenceBuild b1, ElectricFenceBuild b2) {
            float x1 = Math.min(b1.x, b2.x), x2 = Math.max(b1.x, b2.x), y1 = Math.min(b1.y, b2.y), y2 = Math.max(b1.y, b2.y);
            this.team = team;
            e1 = b1;
            e2 = b2;
            x = (x1 + x2) / 2;
            y = (y1 + y2) / 2;
            half = (float) (Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)) / 2);
            rotate = Angles.angle(x1, y1, x2, y2);
            float p = (1 + x) * Math.max(world.width(), world.height()) * 8 * 180 + (1 + y) * 180 + rotate % 180;
            point = new Line2(p, half);

            maxFenceSize = max;
            broken = false;
            stopUnits = new Seq<>();
        }

        public FenceLine() {
            maxFenceSize = 40;
            broken = false;
            stopUnits = new Seq<>();
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

        public void write(Writes write) {
            write.f(timer);
            TypeIO.writeTeam(write, team);
            write.f(x);
            write.f(y);
            write.f(half);
            write.f(rotate);
            write.f(maxFenceSize);
            write.bool(broken);
            write.f(backTime);
            write.f(eleDamage);
            write.str(statusEffect.name.split("-")[statusEffect.name.split("-").length - 1]);
            write.f(statusTime);
            write.bool(air);
            write.i(stopUnits.size);
            for (Unit u : stopUnits) {
                write.i(u.id);
            }
            write.i(e1.pos());
            write.i(e2.pos());
        }

        public void read(Reads read) {
            timer = read.f();
            team = TypeIO.readTeam(read);
            x = read.f();
            y = read.f();
            half = read.f();
            rotate = read.f();
            maxFenceSize = read.f();
            broken = read.bool();
            backTime = read.f();
            eleDamage = read.f();
            statusEffect = content.statusEffect(read.str());
            statusTime = read.f();
            air = read.bool();
            int num = read.i();
            for (int i = 0; i < num; i++) {
                ids.add(read.i());
            }
            p1 = read.i();
            p2 = read.i();
        }
    }

    public class ElectricFenceBuild extends Building {
        private final Seq<Integer> ids = new Seq<>();
        public final Seq<ElectricFenceBuild> builds = new Seq<>();
        public float maxFenceSizes;
        public float maxConnects;

        @Override
        public void updateTile() {
            if (ids.size > 0) {
                for (int i = 0; i < ids.size; i++) {
                    builds.add((ElectricFenceBuild) world.build(ids.get(i)));
                }
                ids.clear();
            }

            builds.removeAll(b -> b.dead || b.health <= 0 || !b.added);

            if (added && !dead && health > 0 && efficiency > 0) {
                if (!FenceNet.run) {
                    Time.run(delta / 2, owner::update);
                    FenceNet.run = true;
                }
            } else {
                for (ElectricFenceBuild b : builds) {
                    b.builds.remove(this);
                }

                owner.removeBuild(this);
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
                FenceLine fl = new FenceLine(team, maxConnect, this, link);
                FenceLine f = owner.find(fl.point);
                if (f != null) {
                    setupColor(1 - (f.go / f.maxFenceSize));

                    Draw.z(Layer.power);

                    drawLaser(x, y, link.x, link.y, size, link.block.size, 1 - 1 * (f.go / f.maxFenceSize));
                }
            }

            Draw.reset();
        }

        @Override
        public Building init(Tile tile, Team team, boolean shouldAdd, int rotation) {
            if (!this.initialized) {
                this.create(tile.block(), team);
            } else if (this.block.hasPower) {
                this.power.init = false;
                (new PowerGraph()).add(this);
            }

            this.proximity.clear();
            this.rotation = rotation;
            this.tile = tile;

            owner.builds.add(this);
            builds.clear();
            maxFenceSizes = maxFenceSize;
            maxConnects = maxConnect;

            this.set(tile.drawx(), tile.drawy());
            if (shouldAdd) {
                this.add();
            }

            this.created();
            return this;
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

                owner.removeBuild(this);

                this.added = false;
            }
        }

        @Override
        public void write(Writes write) {
            write.i(owner.lines.size());
            owner.lines.forEach((l, f) -> {
                l.write(write);
                f.write(write);
            });
            write.i(builds.size);
            for (ElectricFenceBuild b : builds) {
                write.i(b.pos());
            }
            write.f(maxFenceSizes);
            write.f(maxConnects);
        }

        @Override
        public void read(Reads read, byte revision) {
            int num = read.i();
            for (int i = 0; i < num; i++) {
                Line2 l2 = new Line2(0, 0);
                l2.read(read);
                FenceLine fl = new FenceLine();
                fl.point = l2;
                fl.read(read);
                if (owner.find(l2) == null) {
                    owner.lines.put(l2, fl);
                }
            }
            num = read.i();
            for (int i = 0; i < num; i++) {
                ids.add(read.i());
            }
            maxFenceSizes = read.f();
            maxConnects = read.f();
            owner.addBuild(this);
        }
    }
}