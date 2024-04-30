package Floor.FTools;

import arc.struct.IntMap;
import arc.struct.Seq;
import mindustry.gen.Unit;

import static Floor.FTools.GradeActives.*;

public class CoreGradePackage {
    public static int maxSize = 0;
    public static final IntMap<Integer> located = new IntMap<>();
    public final static Seq<GradeActives.active> all = new Seq<>(new GradeActives.active[]{
            speed, health,
            copper, laser, reload, shield, splash, prices, slow, knock, percent
    });

    private CoreGradePackage() {
    }

    public static void apply(Unit unit) {
    }
}
