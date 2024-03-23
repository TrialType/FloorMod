package Floor.FTools;

public interface FUnitUpGrade {
    int getLevel();

    void setLevel(int level);

    float getExp();

    void addExp(float exp);

    int number();

    int getDamageLevel();

    void setDamageLevel(int damageLevel);

    int getSpeedLevel();

    void setSpeedLevel(int speedLevel);

    int getHealthLevel();

    void setHealthLevel(int healthLevel);

    int getReloadLevel();

    void setReloadLevel(int reloadLevel);

    int getAgainLevel();

    void setAgainLevel(int againLevel);

    int getShieldLevel();

    void setShieldLevel(int shieldLevel);

    void sfa(int level);

    int baseLevel();
}
