package Floor.FEntities.FBullet;

import Floor.FEntities.FBulletType.FlyContinuousLaserBulletType;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.pooling.Pools;
import mindustry.gen.*;
import mindustry.world.Block;

public class removeSpwanBullet extends Bullet {
    public boolean couldAgain = true;

    @Override
    public int classId() {
        return 99;
    }

    public static removeSpwanBullet create() {
        return Pools.obtain(removeSpwanBullet.class, removeSpwanBullet::new);
    }

    @Override
    public void update() {
        if (couldAgain && lifetime - time <= 5 * Time.delta) {
            FlyContinuousLaserBulletType clb = (FlyContinuousLaserBulletType) type;
            if (clb != null) {
                removeSpwanBullet rsb = clb.create(owner, team, x, y, rotation, 1, 1);
                rsb.lifetime = 300;
                rsb.couldAgain = false;
            }
            couldAgain = false;
            remove();
        } else if (!couldAgain) {
            vel.set(2 * Mathf.cos((float) Math.toRadians(rotation)), 2 * Mathf.sin((float) Math.toRadians(rotation)));
        }
        super.update();
    }
}
