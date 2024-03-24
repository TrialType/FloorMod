package Floor.FTools;

import mindustry.type.UnitType;

import java.util.Random;

import static Floor.FContent.FUnits.*;
import static mindustry.content.UnitTypes.*;

public class UnitUpGrade {
    private static final String[] list = {
            "healthy", "damage", "reload", "speed", "again", "shield"
    };
    public static final UnitType[] uppers = {
            dagger, mace, fortress, scepter, reign,
            nova, pulsar, quasar, vela, corvus,
            crawler, atrax, spiroct, arkyid, toxopid,
            flare, horizon, zenith, antumbra, eclipse,
            poly, mega, quad, oct,
            risso, minke, bryde, sei, omura,
            retusa, oxynoe, cyerce, aegires, navanax,
            stell, locus, precept, vanquish, conquer,
            merui, cleroi, anthicus, tecta, collaris,
            elude, avert, obviate, quell, disrupt,

            bulletInterception,
            barb, hammer, buying, crazy, transition, shuttle, dive, befall,
            recluse
    };

    private UnitUpGrade() {
    }

    public static void getPower(FUnitUpGrade uug, int number, boolean get, boolean full) {
        if (full) {
            uug.setLevel(60);
            uug.setAgainLevel(10);
            uug.setDamageLevel(10);
            uug.setHealthLevel(10);
            uug.setReloadLevel(10);
            uug.setShieldLevel(10);
            uug.sfa(10);
            uug.setSpeedLevel(10);
        } else if (get) {
            for (int i = 0; i < number; i++) {
                int index = getIndex(uug);
                getPower(uug, index, index);
            }
        }
    }

    private static void getPower(FUnitUpGrade uug, int index, int start) {
        switch (list[index]) {
            case "healthy": {
                if (uug.getHealthLevel() >= 10) {
                    if (start != 1) {
                        getPower(uug, 1, start);
                    }
                } else {
                    uug.setHealthLevel(uug.getHealthLevel() + 1);
                }
                break;
            }
            case "damage": {
                if (uug.getDamageLevel() >= 10) {
                    if (start != 2) {
                        getPower(uug, 2, start);
                    }
                } else {
                    uug.setDamageLevel(uug.getDamageLevel() + 1);
                }
                break;
            }
            case "reload": {
                if (uug.getReloadLevel() >= 10) {
                    if (start != 3) {
                        getPower(uug, 3, start);
                    }
                } else {
                    uug.setReloadLevel(uug.getReloadLevel() + 1);
                }
                break;
            }
            case "speed": {
                if (uug.getSpeedLevel() >= 10) {
                    if (start != 4) {
                        getPower(uug, 4, start);
                    }
                } else {
                    uug.setSpeedLevel(uug.getSpeedLevel() + 1);
                }
                break;
            }
            case "again": {
                if (uug.getAgainLevel() >= 10) {
                    if (start != 5) {
                        getPower(uug, 5, start);
                    }
                } else {
                    uug.setAgainLevel(uug.getAgainLevel() + 1);
                }
                break;
            }
            case "shield": {
                if (uug.getShieldLevel() < 10) {
                    uug.setShieldLevel(uug.getShieldLevel() + 1);
                    uug.sfa(uug.getShieldLevel());
                } else {
                    if (start != 0) {
                        getPower(uug, 0, start);
                    }
                }
                break;
            }
        }
    }

    private static int getIndex(FUnitUpGrade uug) {
        int damage = uug.getDamageLevel() + 1;
        int speed = uug.getSpeedLevel() + 1;
        int reload = uug.getReloadLevel() + 1;
        int health = uug.getHealthLevel() + 1;
        int shield = uug.getShieldLevel() + 1;
        int again = uug.getAgainLevel() + 1;
        int sum = 83160 / health + 83160 / damage + 83160 / reload + 83160 / speed + 83160 / again + 83160 / shield;
        int[] k = new int[]{83160 / health, 83160 / damage, 83160 / reload, 83160 / speed, 83160 / again, 83160 / shield};
        int power = new Random().nextInt(sum) + 1;
        int index = 0;
        while (power > k[index]) {
            power -= k[index];
            index++;
        }
        return index;
    }
}
