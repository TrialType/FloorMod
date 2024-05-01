package Floor.FTools.interfaces;

import mindustry.gen.Healthc;
import mindustry.gen.Unit;

public interface ChainAble {
    Healthc order();

    void order(Healthc order);

    boolean upon();

    void upon(boolean b);

    Unit UnderUnit();

    void UnderUnit(Unit unit);
}
