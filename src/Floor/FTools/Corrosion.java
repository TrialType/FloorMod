package Floor.FTools;

public interface Corrosion {
    float baseDamage();

    default float corrosionLevel() {
        return 1;
    }
}
