package Floor.FTools;

import arc.struct.IntMap;

public interface RangePure {
    int plan();
    boolean couldUse();

    IntMap<Integer> protects();

    IntMap<Integer> timeBoost();

    IntMap<Integer> withBoost();
}
