package Floor.FEntities.FUnit.F;

import Floor.FTools.interfaces.OwnerSpawner;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.gen.UnitEntity;

public class FollowUnit extends UnitEntity implements OwnerSpawner {
    int ownerId = -1;
    Unit owner;

    public static FollowUnit create() {
        return new FollowUnit();
    }

    @Override
    public int classId() {
        return 120;
    }

    @Override
    public void update() {
        if (ownerId >= 0) {
            owner = Groups.unit.getByID(ownerId);
            ownerId = -1;
        }
        if (owner == null || owner.dead) {
            health -= maxHealth * 0.001f;
        }
        if (health <= 0) {
            kill();
        }
        super.update();
    }

    @Override
    public void read(Reads read) {
        super.read(read);
        ownerId = read.i();
    }

    @Override
    public void write(Writes write) {
        super.write(write);
        write.i(owner == null ? -1 : owner.id);
    }

    @Override
    public void spawner(Unit u) {
        this.owner = u;
    }

    @Override
    public Unit spawner() {
        return this.owner;
    }
}
