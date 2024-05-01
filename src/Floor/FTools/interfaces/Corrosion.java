package Floor.FTools.interfaces;

public interface Corrosion {
    float baseDamage();

    default float corrosionLevel() {
        return 1;
    }
}
