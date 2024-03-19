package Floor.FTools;

import java.util.Random;

public class UnitUpGrade {
    private static final String[] list = {
            "healthy", "damage", "reload", "speed", "again", "shield"
    };

    private UnitUpGrade() {
    }

    public static void getPower(FUnitUpGrade uug, int number, boolean get, boolean full) {
        if (uug.getLevel() < 60) {
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
                    getPower(uug, index);
                }
            }
        } else {
            uug.number();
        }
    }

    private static void getPower(FUnitUpGrade uug, int index) {
        switch (list[index]) {
            case "healthy": {
                if (uug.getHealthLevel() >= 10) {
                    getPower(uug, 1);
                } else {
                    uug.setHealthLevel(uug.getHealthLevel() + 1);
                }
                break;
            }
            case "damage": {
                if (uug.getDamageLevel() >= 10) {
                    getPower(uug, 2);
                } else {
                    uug.setDamageLevel(uug.getDamageLevel() + 1);
                }
                break;
            }
            case "reload": {
                if (uug.getReloadLevel() >= 10) {
                    getPower(uug, 3);
                } else {
                    uug.setReloadLevel(uug.getReloadLevel() + 1);
                }
                break;
            }
            case "speed": {
                if (uug.getSpeedLevel() >= 10) {
                    getPower(uug, 4);
                } else {
                    uug.setSpeedLevel(uug.getSpeedLevel() + 1);
                }
                break;
            }
            case "again": {
                if (uug.getAgainLevel() >= 10) {
                    getPower(uug, 5);
                } else {
                    uug.setAgainLevel(uug.getAgainLevel() + 1);
                }
                break;
            }
            case "shield": {
                if (uug.getShieldLevel() < 10) {
                    uug.setShieldLevel(uug.getShieldLevel() + 1);
                    uug.sfa(uug.getShieldLevel());
                }
                break;
            }
        }
    }

    private static int getIndex(FUnitUpGrade uug) {
        float[] p = new float[]{100f / (uug.getHealthLevel() + 1), 100f / (uug.getDamageLevel() + 1),
                100f / (uug.getReloadLevel() + 1), 100f / (uug.getSpeedLevel() + 1),
                100f / (uug.getAgainLevel() + 1), 100f / (uug.getShieldLevel() + 1)};
        Random ra = new Random();
        int sum = (int) (100f / (uug.getHealthLevel() + 1) + 100f / (uug.getDamageLevel() + 1) +
                100f / (uug.getReloadLevel() + 1) + 100f / (uug.getSpeedLevel() + 1) +
                100f / (uug.getAgainLevel() + 1) + 100f / (uug.getShieldLevel() + 1));
        int l = ra.nextInt(sum) + 1;
        int index = 0;
        while (l > p[index]) {
            l -= (int) p[index];
            index++;
        }
        return index;
    }
}
