package Floor.FEntities.FBlock;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Angles;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
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
                        e.builds.add(efb);
                        efb.builds.add(e);
                        owner.addLine(fl);
                    } else if (e.builds.indexOf(efb) >= 0) {
                        e.builds.remove(efb);
                        efb.builds.remove(e);
                        owner.removeLine(fl.point);
                    }
                }
            }
        });
    }

    protected void setupColor(float satisfaction) {
        Draw.color(Color.white, Pal.powerLight, (1f - satisfaction) * 0.86f + Mathf.absin(3f, 0.1f));
        Draw.alpha(Renderer.laserOpacity);
    }

    public void drawLaser(float x1, float y1, float x2, float y2, int size1, int size2) {
        float angle1 = Angles.angle(x1, y1, x2, y2),
                vx = Mathf.cosDeg(angle1), vy = Mathf.sinDeg(angle1),
                len1 = size1 * tilesize / 2f - 1.5f, len2 = size2 * tilesize / 2f - 1.5f;

        Drawf.laser(Core.atlas.find(""), Core.atlas.find(""),
                x1 + vx * len1, y1 + vy * len1, x2 - vx * len2, y2 - vy * len2, 0.25f);
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
            return this.point == point && this.halfLength == halfLength;
        }
    }

    public static class FenceNet {
        public float couldUp = 0;
        public final Map<Line2, FenceLine> lines = new HashMap<>();
        public final Seq<ElectricFenceBuild> builds = new Seq<>();

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
            if (builds.size == 0) return;

            if (couldUp < builds.size) return;
            couldUp = 0;

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
    }

    public class FenceLine {
        protected float timer = 0;
        protected Team team;

        private final float x;
        private final float y;
        private final float half;
        private final float rotate;
        public final Line2 point;
        public final float maxFenceSize;
        public final Seq<Unit> stopUnits;
        public boolean broken;
        public float go;

        public ElectricFenceBuild[] twoPoint() {
            ElectricFenceBuild[] builds = new ElectricFenceBuild[2];
            float dx = (float) Math.cos(Math.toRadians(rotate)) * half;
            float dy = (float) Math.sin(Math.toRadians(rotate)) * half;
            Building b = world.buildWorld(x + dx, y + dy);
            builds[0] = b instanceof ElectricFenceBuild efb ? couldUse(efb) ? efb : null : null;
            b = world.buildWorld(x - dx, y - dy);
            builds[1] = b instanceof ElectricFenceBuild efb ? couldUse(efb) ? efb : null : null;
            return builds;
        }

        public boolean couldUse(ElectricFenceBuild b) {
            return !(b.dead || b.health <= 0 || !b.isAdded());
        }

        public FenceLine(Team team, float max, ElectricFenceBuild b1, ElectricFenceBuild b2) {
            float x1 = b1.x, x2 = b2.x, y1 = b1.y, y2 = b2.y;
            this.team = team;
            x = (x1 + x2) / 2;
            y = (y1 + y2) / 2;
            half = (float) (Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)) / 2);
            rotate = Angles.angle(x1, y1, x2, y2) % 180;
            float p = x * Math.max(world.width(), world.height()) * 8 * 180 + y * 180 + rotate;
            point = new Line2(p, half);

            maxFenceSize = max;
            broken = false;
            stopUnits = new Seq<>();
        }

        public void broken() {
            go = 0;
            for (Unit u : stopUnits) {
                go += u.hitSize;
            }
            if (go >= maxFenceSize) {
                broken = true;
                stopUnits.clear();
            }
        }

        public void update() {
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
            stopUnits.clear();
            float dx = (float) Math.cos(Math.toRadians(rotate)) * half;
            float dy = (float) Math.sin(Math.toRadians(rotate)) * half;
            float len = (float) Math.sqrt(dx * dx + dy * dy);
            Units.nearbyEnemies(team, x, y, len, u -> {
                if (inRange(len, u)) {
                    stopUnits.add(u);

                    u.vel.setAngle(rotate);
                    float udx = u.vel.x;
                    u.vel.setZero();
                    u.vel.set(udx, 0);
                    u.vel.setAngle(rotate);

                    u.damage(eleDamage);
                    u.apply(statusEffect, statusTime);
                }
            });
        }

        public boolean inRange(float len, Unit u) {
            float ux = u.x;
            float uy = u.y;
            float angle = Angles.angleDist(rotate, Angles.angle(x, y, ux, uy));
            angle = Math.min(angle, 180 - angle);
            if (angle <= 10f && len * Math.cos(Math.toRadians(angle)) <= half) {
                if (air && !(u.physref.body.layer == 4)) {
                    return true;
                } else return !air && u.isGrounded();
            }
            return false;
        }
    }

    public class ElectricFenceBuild extends Building {
        public final Seq<ElectricFenceBuild> builds = new Seq<>();
        public float maxFenceSizes;
        public float maxConnects;

        @Override
        public void updateTile() {
            if (added && !dead && health > 0 && owner.builds.indexOf(this) >= 0) {
                owner.couldUp++;
                owner.update();
            } else {
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


            setupColor(0.5f);

            for (int i = 0; i < builds.size; i++) {
                ElectricFenceBuild link = builds.get(i);
                FenceLine fl = new FenceLine(team, maxConnect, this, link);
                FenceLine f = owner.find(fl.point);
                if(f != null){
                    Draw.z(Layer.power);

                    drawLaser(x, y, link.x, link.y, size, link.block.size);
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
    }
}
