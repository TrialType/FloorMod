package Floor.FTools;

import arc.struct.IntMap;
import arc.struct.IntSeq;

public interface RangePure {
    boolean couldUse();
    IntMap<IntSeq> protects();
}
