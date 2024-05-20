package Floor.FEntities.FBullet;

import Floor.FContent.FStatusEffects;
import arc.math.Mathf;
//import arc.math.Rand;
//import arc.math.geom.Vec2;
//import arc.struct.Seq;
import arc.util.Time;
import arc.util.pooling.Pools;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.core.World;
import mindustry.entities.Effect;
import mindustry.entities.Lightning;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.StaticWall;

import static mindustry.Vars.world;

public class EMPBullet extends Bullet {
    public Effect lightning = new Effect(5, e -> {
        for (int i = 0; i < 16; i++) {
            Lightning.create(team, Pal.techBlue, 0, e.x, e.y, Mathf.range(360f), 25);
        }
    });
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
            lightning.at(this);
            Units.nearbyEnemies(team, x, y, 100, u -> u.apply(FStatusEffects.StrongStop, 600));
            Units.nearbyBuildings(x, y, 100, b -> {
                if (b.team != this.team && b.block.canOverdrive) {
                    b.applySlowdown(0, 1);
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
        if (on instanceof Building) {
            this.vel.setZero();
        } else if (on instanceof Unit u) {
            this.vel.set(u.vel);
        }
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
        this.type.update(this);
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
