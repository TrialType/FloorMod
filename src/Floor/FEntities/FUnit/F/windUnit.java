package Floor.FEntities.FUnit.F;

import Floor.FEntities.FBullet.LargeNumberBullet;
import Floor.FEntities.FBulletType.WindMoveBulletType;
import Floor.FEntities.FUnit.Override.FUnitEntity;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Bits;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.Vars;
import mindustry.ctype.ContentType;
import mindustry.entities.Units;
import mindustry.gen.Healthc;
import mindustry.gen.Teamc;
import mindustry.world.blocks.storage.CoreBlock;

import java.util.*;

import static java.lang.Math.*;
import static mindustry.Vars.world;

public class windUnit extends FUnitEntity {
    public final Map<LargeNumberBullet, Float> bullets = new HashMap<>();
    public Teamc target;
    private final WindMoveBulletType small = new WindMoveBulletType() {{
        width = 30;
        height = 30;
        shrinkY = 0;
        lifetime = Float.MAX_VALUE;
        damage = 80;
        splashDamage = 40;
        splashDamageRadius = 15;
        speed = 0.8F;
    }};
    private final WindMoveBulletType mid = new WindMoveBulletType() {{
        width = 40;
        height = 40;
        shrinkY = 0;
        lifetime = Float.MAX_VALUE;
        damage = 160;
        splashDamage = 80;
        splashDamageRadius = 20;
        speed = 0.6F;
        fragAngle = 90;
        fragBullets = 3;
        fragBullet = small;
        fragOnAbsorb = fragOnHit = true;
    }};
    private final WindMoveBulletType large = new WindMoveBulletType() {{
        width = 50;
        height = 50;
        shrinkY = 0;
        lifetime = Float.MAX_VALUE;
        damage = 400;
        splashDamage = 200;
        splashDamageRadius = 25;
        speed = 0.4F;
        fragAngle = 90;
        fragBullets = 3;
        fragBullet = mid;
        fragOnAbsorb = fragOnHit = true;
    }};

    private float createTimer;

    protected windUnit() {
        this.applied = new Bits(Vars.content.getBy(ContentType.status).size);
        this.resupplyTime = Mathf.random(10.0F);
        this.statuses = new Seq<>();
        large.load();
        mid.load();
        small.load();
    }

    public static FUnitEntity create() {
        return new windUnit();
    }

    @Override
    public int classId() {
        return 116;
    }

    @Override
    public void update() {
        Vec2 vec = new Vec2();

        if (target != null && target instanceof Healthc && (((Healthc) target).dead() || ((Healthc) target).health() <= 0)) {
            target = null;
        }
        super.update();

        Units.nearbyEnemies(team, x, y, range() * 5, u -> {
            if (u.within(this, range() * 5)) {
                float ux = u.x;
                float uy = u.y;
                vec.set(ux - x, uy - y);
                float l = (float) sqrt((x - ux) * (x - ux) + (y - uy) * (y - uy));
                float power = (range() * 5 - l) / range() / 15;
                vec.setLength(power * hitSize * 2 / u.hitSize / u.hitSize);
                u.vel.add(vec);
            }
        });

        createTimer = createTimer + Time.delta;
        if (createTimer >= 420) {
            if (rotation < 0) {
                rotation = rotation + 360;
            }
            if (rotation < 180 && rotation > 0 && rotation != 90) {
                if (rotation < 90) {
                    createBullets(0, 0);
                } else if (rotation > 90) {
                    createBullets(world.width() * 8, 0);
                }
            } else if (rotation > 180 && rotation < 360 && rotation != 270) {
                if (rotation < 270) {
                    createBullets(world.width() * 8, world.height() * 8);
                } else if (rotation > 270) {
                    createBullets(0, world.height() * 8);
                }
            } else {
                if (rotation == 0) {
                    createBullets(0, -1);
                } else if (rotation == 90) {
                    createBullets(-1, 0);
                } else if (rotation == 180) {
                    createBullets(world.width() * 8, -1);
                } else if (rotation == 270) {
                    createBullets(-1, world.height() * 8);
                }
            }
            createTimer = 0;
        }
        Seq<LargeNumberBullet> rmb = new Seq<>();
        for (LargeNumberBullet b : bullets.keySet()) {
            if (b.x < -3 || b.y < -3 || b.x > world.width() * 8 + 3 || b.y > world.height() * 8 + 3) {
                rmb.add(b);
            }
        }
        for (LargeNumberBullet b : rmb) {
            bullets.remove(b);
            b.remove();
            b.type.removed(b);
        }
        for (LargeNumberBullet b : bullets.keySet()) {
            if (target != null) {
                vec.set(target.x() - b.x, target.y() - b.y);
                vec.setLength(bullets.get(b) / 1.1F);
                b.rotation(Angles.angle(b.x, b.y, target.x(), target.y()));
                b.move(vec);
            } else {
                b.rotation(rotation);
            }
        }
    }

    private void createBullets(int x, int y) {
        Random ra = new Random();
        int number = (int) (ra.nextInt((int) ((world.width() + world.height()) * 0.1 + 1)) + (world.width() + world.height()) * 0.01);
        number = target == null ? number : target instanceof CoreBlock.CoreBuild ? number * 4 : number * 2;
        for (int i = 0; i < number; i++) {
            int size = ra.nextInt(31) + 1;
            float bx, by;
            if (target != null) {
                if (ra.nextInt(2) == 0) {
                    if (ra.nextInt(2) == 0) {
                        bx = 0;
                    } else {
                        bx = world.width() * 8;
                    }
                    by = ra.nextInt(world.height() * 8 + 1);
                } else {
                    if (ra.nextInt(2) == 0) {
                        by = 0;
                    } else {
                        by = world.height() * 8;
                    }
                    bx = ra.nextInt(world.width() * 8 + 1);
                }
            } else if (x < 0) {
                bx = ra.nextInt(world.width() * 8 + 1);
                by = y;
            } else if (y < 0) {
                bx = x;
                by = ra.nextInt(world.height() * 8 + 1);
            } else {
                int pose = ra.nextInt(world.width() * 8 + world.height() * 8 + 1);
                if (pose <= world.width() * 8) {
                    bx = pose;
                    by = y;
                } else {
                    bx = x;
                    by = pose - world.width() * 8;
                }
            }
            LargeNumberBullet b;
            if (size == 31) {
                b = large.create(this, null, this.team, bx, by, rotation, large.damage, 1, 1, null, null, -1, -1);
            } else if (size <= 25) {
                b = small.create(this, null, this.team, bx, by, rotation, large.damage, 1, 1, null, null, -1, -1);
            } else {
                b = mid.create(this, null, this.team, bx, by, rotation, large.damage, 1, 1, null, null, -1, -1);
            }
            bullets.put(b, b.type.speed);
            b.add();
        }
    }
}
