package Floor.FType.UponFloor;

import Floor.FTools.Corrosion;
import arc.struct.ObjectSet;
import arc.util.Time;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.world.blocks.environment.Floor;

import static arc.util.Time.delta;

public class CorrosionMist {
    public final static ObjectSet<Building> clearB = new ObjectSet<>();
    public final static ObjectSet<Unit> clearU = new ObjectSet<>();

    public static void init() {
        clearB.clear();
        clearU.clear();

        Time.run(delta * 60, CorrosionMist::update);
    }

    public static void update() {
        if (Vars.world == null || Vars.editor.isLoading()) return;

        Groups.unit.each(u -> {
            if(clearU.contains(u)){
                return;
            }
            Floor t = u.floorOn();
            if (t instanceof Corrosion) {
                u.apply(t.status, 30);
            }
        });

        Groups.build.each(b -> {
            if(clearB.contains(b)){
                return;
            }
            Floor t = b.floorOn();
            if (t instanceof Corrosion c) {
                b.damage(c.baseDamage());
            }
        });

        Time.run(delta * 30, CorrosionMist::update);
    }
}
