package Floor.FTools;

import arc.struct.IntMap;

public interface RangePure {
    default int protectLevel() {
        return 1;
    }

    default int protectPos() {
        return -1;
    }

    default IntMap<Integer> protects() {
        return null;
    }

    default IntMap<Integer> withBoost() {
        return null;
    }

    default Object[] timeBoost() {
        return null;
    }

    int plan();

    boolean couldUse();
}
