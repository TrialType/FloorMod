package Floor.FTools;

import arc.struct.IntMap;
import arc.struct.IntSeq;

public interface RangePure {
    boolean couldUse();
    float maxPower();
    IntMap<IntSeq> protects();
}
