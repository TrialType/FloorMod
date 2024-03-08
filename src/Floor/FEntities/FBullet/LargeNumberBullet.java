package Floor.FEntities.FBullet;

import arc.struct.Seq;
import mindustry.gen.Bullet;
import mindustry.gen.Groups;
import mindustry.gen.Healthc;

public class LargeNumberBullet extends Bullet {
    private static final Seq<LargeNumberBullet> freeBulletPool = new Seq<>();
    public static LargeNumberBullet create() {
        if (freeBulletPool.size >= 1) {
            LargeNumberBullet lnb = freeBulletPool.get(0);
            freeBulletPool.remove(0);
            return lnb;
        } else {
            return new LargeNumberBullet();
        }
    }
    public void remove() {
        if (this.added) {
            Groups.all.removeIndex(this, this.index__all);
            this.index__all = -1;
            Groups.bullet.removeIndex(this, this.index__bullet);
            this.index__bullet = -1;
            Groups.draw.removeIndex(this, this.index__draw);
            this.index__draw = -1;
            if (!Groups.isClearing) {
                if (!this.hit) {
                    this.type.despawned(this);
                }

                this.type.removed(this);
                this.collided.clear();
            }

            this.added = false;
            freeBulletPool.add(this);
        }
    }
    public void update() {
        super.update();
        if (owner == null || owner instanceof Healthc && (((Healthc) owner).dead() || ((Healthc) owner).health() <= 0)) {
            lifetime = 1800;
        }
    }
}
