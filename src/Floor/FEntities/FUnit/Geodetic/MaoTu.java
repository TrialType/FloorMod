package Floor.FEntities.FUnit.Geodetic;

import Floor.FEntities.FUnit.Override.FLegsUnit;
import arc.math.Rand;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.StaticWall;

import static mindustry.Vars.world;

public class MaoTu extends FLegsUnit {
    public int life = 3;
    public float growTimer = 0;

    @Override
    public int classId() {
        return 124;
    }

    public static MaoTu create() {
        return new MaoTu();
    }

    public void update() {
        super.update();
        growTimer += Time.delta;
        if (growTimer >= 3600) {
            life++;
            growTimer = 0;
        }
    }

    public void updateDrowning(){}

    @Override
    public void kill() {
        if (life > 0) {
            MaoTu mt = (MaoTu) type.create(team);
            Rand rand = new Rand();
            Tile t = null;
            while (t == null || t.floor().isDeep() || t.block() instanceof StaticWall || within(t, 500) || t.isDarkened()) {
                float nx = rand.range(world.width() * 8);
                float ny = rand.range(world.width() * 8);
                t = world.tileWorld(nx, ny);
            }

            mt.set(t.worldx(), t.worldy());
            if (t.build != null) {
                t.build.kill();
            }
            mt.setAgainLevel(this.againLevel);
            mt.setDamageLevel(this.damageLevel);
            mt.setHealthLevel(this.healthLevel);
            mt.setReloadLevel(this.reloadLevel);
            mt.setShieldLevel(this.shieldLevel);
            mt.setSpeedLevel(this.speedLevel);
            mt.setLevel(this.level);
            mt.addExp(this.exp);
            mt.heal();
            mt.life = this.life - 1;
            mt.growTimer = 0;
            mt.add();
        }
        super.kill();
    }

    @Override
    public void write(Writes write) {
        super.write(write);
        write.i(this.life);
        write.f(this.growTimer);
    }

    @Override
    public void read(Reads read) {
        super.read(read);
        life = read.i();
        growTimer = read.f();
    }
}
