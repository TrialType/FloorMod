package Floor.FTools;

import arc.struct.IntMap;

public interface RangePure {

    default int protectLevel() {
        return 1;
    }

    default int protectPos() {
        return -1;
    }
    int plan();
    boolean couldUse();

    IntMap<Integer> protects();

    IntMap<Integer> timeBoost();

    IntMap<Integer> withBoost();
}
