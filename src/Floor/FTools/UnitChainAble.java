package Floor.FTools;

import mindustry.gen.Healthc;
import mindustry.gen.Unit;

public interface UnitChainAble {
    Healthc order();

    void order(Healthc order);

    boolean upon();

    void upon(boolean b);

    Unit UnderUnit();

    void UnderUnit(Unit unit);
}
