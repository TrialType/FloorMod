package Floor.FEntities.FBlock;

import arc.math.Angles;
import arc.math.geom.Point2;
import arc.struct.Seq;
import mindustry.content.StatusEffects;
import mindustry.entities.Units;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.power.PowerGraph;

import java.util.HashMap;
import java.util.Map;

import static arc.util.Time.*;
import static mindustry.Vars.*;

public class ElectricFence extends Block {
    public boolean air = false;
    public float maxLength = 50;
    public int maxConnect = 2;
    public float eleDamage = 1 * 60f;
    public StatusEffect statusEffect = StatusEffects.burning;
    public float statusTime = 240;
    public float maxFenceSize = 15;
    public float backTime = 600;

    public ElectricFence(String name) {
        super(name);

        update = solid = true;

        config(Point2.class, ElectricFenceBuild::getLink);
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
            lines.remove(l);
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

            lines.forEach((i, l) -> {
                ElectricFenceBuild[] bs = l.twoPoint();
                if (bs[0] == null || bs[1] == null) {
                    lines.remove(i);
                } else {
                    l.update();
                }
            });
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

            point = new Line2(rotate, half);

            maxFenceSize = max;
            broken = false;
            stopUnits = new Seq<>();
        }

        public void broken() {
            float sum = 0;
            for (Unit u : stopUnits) {
                sum += u.hitSize;
            }
            if (sum >= maxFenceSize) {
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
            if (angle <= 5f && len * Math.cos(Math.toRadians(angle)) <= half) {
                if (air && !(u.physref.body.layer == 4)) {
                    return true;
                } else return !air && u.isGrounded();
            }
            return false;
        }
    }

    public class ElectricFenceBuild extends Building {
        public static FenceNet owner = new FenceNet();
        public float maxFenceSizes;
        public static float damageTimer = 0;

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

        public void getLink(Point2 p) {
            if (!within(p.x, p.y, maxLength)) {
                return;
            }
            Building b = world.buildWorld(p.x, p.y);
            if ( b instanceof ElectricFenceBuild efb) {
                FenceLine fl = new FenceLine(team, Math.max(maxFenceSizes, efb.maxFenceSizes), this, efb);
                if (owner.find(fl.point) == null) {
                    owner.addLine(fl);
                } else {
                    owner.removeLine(fl.point);
                }
            }
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
            damageTimer = 0;
            maxFenceSizes = maxFenceSize;

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
