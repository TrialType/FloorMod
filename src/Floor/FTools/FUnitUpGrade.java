package Floor.FTools;

import java.util.Map;

public interface FUnitUpGrade {
    Map<String,Integer> getMap();
    int getLevel();
    void setLevel(int level);
    float getExp();
    void addExp(float exp);
    int number();
}
