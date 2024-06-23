package Floor.FTools.interfaces;

import arc.struct.Seq;
import mindustry.gen.Unit;

public interface OwnerSpawner {
    default Seq<Unit> unit() {
        return null;
    }

    default void spawner(Unit u) {
    }

    default Unit spawner() {
        return null;
    }
}