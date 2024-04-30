package Floor.FTools;

import mindustry.gen.Unit;

public class GradeActives {
    public static int maxSize = 0, maxHealth = 0,
            maxSpeed = 0, maxCopper = 0, maxLaser = 0, maxReload = 0,
            maxShield = 0, maxSplash = 0, maxPrices = 0, maxSlow = 0,
            maxKnock = 0, maxPercent = 0;
    public final static active speed = u -> {
    }, health = u -> {
    }, copper = u -> {
    }, laser = u -> {
    }, reload = u -> {
    }, shield = u -> {
    }, splash = u -> {
    }, prices = u -> {
    }, slow = u -> {
    }, knock = u -> {
    }, percent = u -> {
    };

    private GradeActives() {}

    public interface active {
        void get(Unit unit);
    }
}
