package Floor.FEntities.FBullet;

import Floor.FContent.FStatusEffects;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.pooling.Pools;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.core.World;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.WaveEffect;
import mindustry.gen.*;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.StaticWall;

import static mindustry.Vars.world;

public class EMPBullet extends Bullet {
    public Effect lightning = new Effect(30, e -> {
        if (e.data instanceof Seq<?> s) {
            Seq<Vec2> points = new Seq<>();
            int index = (int) (e.fin() * s.size);
            for (int i = 0; i < index; i++) {
                points.add((Vec2) s.get(i));
            }
            Fx.lightning.at(e.x, e.y, e.rotation, Color.valueOf("99111188"), points);
        }
    });
    public float onAngle;
    public float onLen;
    public float onRotate;
    public float timer = 0;
    public Entityc on = null;

    protected EMPBullet() {
        super();
    }

    public static EMPBullet create() {
        return Pools.obtain(EMPBullet.class, EMPBullet::new);
    }

    @Override
    public void collision(Hitboxc other, float x, float y) {
        if (this.on == null) {
            this.type.hit(this, x, y);
            this.collided.add(other.id());
            if (collided.size >= type.pierceCap + 1 || !type.pierce) {
                on = other;
                if (other instanceof Unit u) {
                    this.onAngle = u.rotation - Angles.angle(x, y, u.x, u.y);
                    this.onLen = (float) Math.sqrt((u.x - x) * (u.x - x) + (u.y - y) * (u.y - y));
                    this.onRotate = u.rotation - this.rotation;
                }
            }
            BulletType var10000 = this.type;
            float var10003;
            if (other instanceof Healthc h) {
                var10003 = h.health();
            } else {
                var10003 = 0.0F;
            }
            var10000.hitEntity(this, other, var10003);
        }
    }

    public void tileRaycast(int x1, int y1, int x2, int y2) {
        int x = x1;
        int dx = Math.abs(x2 - x);
        int sx = x < x2 ? 1 : -1;
        int y = y1;
        int dy = Math.abs(y2 - y);
        int sy = y < y2 ? 1 : -1;
        int err = dx - dy;
        int ww = world.width();
        int wh = world.height();
        while (x >= 0 && y >= 0 && x < ww && y < wh) {
            Building build = world.build(x, y);
            if (this.type.collideFloor || this.type.collideTerrain) {
                Tile tile = world.tile(x, y);
                if (this.type.collideFloor && (tile == null || tile.floor().hasSurface() || tile.block() != Blocks.air) || this.type.collideTerrain && tile != null && tile.block() instanceof StaticWall) {
                    this.remove();
                    this.hit = true;
                    return;
                }
            }
            if (build != null && this.isAdded() && this.checkUnderBuild(build, (float) (x * 8), (float) (y * 8)) && build.collide(this) && this.type.testCollision(this, build) && !build.dead() && (this.type.collidesTeam || build.team != this.team) && (!this.type.pierceBuilding || !this.hasCollided(build.id))) {
                boolean remove = false;
                float health = build.health;
                if (build.team != this.team) {
                    remove = build.collision(this);
                }
                if (remove || this.type.collidesTeam) {
                    if (Mathf.dst2(this.lastX, this.lastY, (float) (x * 8), (float) (y * 8)) < Mathf.dst2(this.lastX, this.lastY, this.x, this.y)) {
                        this.x = (float) (x * 8);
                        this.y = (float) (y * 8);
                    }
                    if (!this.type.pierceBuilding) {
                        this.on = build;
                    } else {
                        this.collided.add(build.id);
                        if (collided.size >= type.pierceCap + 1) {
                            on = build;
                        }
                    }
                }
                this.type.hitTile(this, build, (float) (x * 8), (float) (y * 8), health, true);
                if (this.type.pierceBuilding) {
                    return;
                }
            }
            if (x == x2 && y == y2) {
                break;
            }
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x += sx;
            }
            if (e2 < dx) {
                err += dx;
                y += sy;
            }
        }
    }

    @Override
    public float damage() {
        return on == null ? super.damage() : 1;
    }

    @Override
    public void remove() {
        timer = 0;
        on = null;
        super.remove();
    }

    @Override
    public void update() {
        if (on != null) {
            timer += Time.delta;
        }
        if (this.timer >= 180) {
            new MultiEffect(new WaveEffect() {{
                lifetime = 16;
                sizeFrom = 0;
                sizeTo = 120;
                strokeFrom = 0;
                strokeTo = 9;
                colorTo = colorFrom = Color.valueOf("bb1111");
            }}, new Effect(40, e -> {
                Draw.color(Color.valueOf("bb1111"));
                Lines.stroke(9);
                Lines.poly(e.x, e.y, 72, 120, 0);
            }) {{
                startDelay = 16;
            }}, new WaveEffect() {{
                startDelay = 56;
                lifetime = 16;
                sizeFrom = 120;
                sizeTo = 120;
                strokeFrom = 9;
                strokeTo = 0;
                colorTo = colorFrom = Color.valueOf("bb1111");
            }}).at(this);

            Rand random = new Rand();
            for (int i = 0; i < 26; i++) {
                Seq<Vec2> lines = new Seq<>();

                float ro = random.range(360);
                float finalRo = ro;
                float lx = this.x, ly = this.y;
                for (int j = 0; j < 12; j++) {
                    lines.add(new Vec2(lx + Mathf.range(3f), ly + Mathf.range(3f)));
                    ro += random.range(20f);
                    lx += Angles.trnsx(ro, 12);
                    ly += Angles.trnsy(ro, 12);
                }

                lightning.at(x, y, finalRo, lines);
            }

            Units.nearbyEnemies(team, x, y, 100, u -> u.apply(FStatusEffects.StrongStop, 600));
            Units.nearbyBuildings(x, y, 100, b -> {
                if (b.team != this.team && b.block.canOverdrive) {
                    b.applySlowdown(0, 600);
                }
            });
            remove();
        }
        if (on != null && (!on.isAdded())) {
            if (on instanceof Healthc h && (h.health() <= 0 || h.dead())) {
                remove();
            }
            if (on instanceof Building b) {
                Building bu = world.build(b.pos());
                if (bu == null) {
                    remove();
                }
            } else if (on instanceof Unit u) {
                if (!u.isAdded()) {
                    remove();
                }
            }
        }
        if (this.on == null) {
            if (!Vars.net.client() || this.isLocal()) {
                float px = this.x;
                float py = this.y;
                this.move(this.vel.x * Time.delta, this.vel.y * Time.delta);
                if (Mathf.equal(px, this.x)) {
                    this.vel.x = 0.0F;
                }
                if (Mathf.equal(py, this.y)) {
                    this.vel.y = 0.0F;
                }
                this.vel.scl(Math.max(1.0F - this.drag * Time.delta, 0.0F));
            }
            if (this.mover != null) {
                this.mover.move(this);
            }
        } else {
            if (on instanceof Building) {
                this.vel.setZero();
            } else if (on instanceof Unit u) {
                this.vel.setZero();
                Vec2 pla = new Vec2(1, 1);
                pla.setAngle(u.rotation - onAngle);
                pla.setLength(onLen);
                this.rotation = u.rotation - onRotate;
                this.x = u.x + pla.x;
                this.y = u.y + pla.y;
            }
        }
        if (on == null) {
            this.type.update(this);
        } else {
            trail.length = 0;
            trail.update(x, y, 0);
        }
        if (this.type.collidesTiles && this.type.collides && this.type.collidesGround && on == null) {
            this.tileRaycast(World.toTile(this.lastX), World.toTile(this.lastY), this.tileX(), this.tileY());
        }
        if (this.keepAlive) {
            this.time -= Time.delta;
            this.keepAlive = false;
        }
        this.time = on != null ? time : Math.min(this.time + Time.delta, this.lifetime);
        if (this.time >= this.lifetime && on == null) {
            this.remove();
        }
    }
}
