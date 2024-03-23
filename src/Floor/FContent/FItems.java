package Floor.FContent;

import mindustry.type.Item;

public class FItems {
    public static Item damagePower, reloadPower, healthPower, speedPower, shieldPower, againPower;
    public static void load(){
        damagePower = new Item("damagePower"){{
            alwaysUnlocked = true;
        }};
        reloadPower = new Item("reloadPower"){{
            alwaysUnlocked = true;
        }};
        healthPower = new Item("healthPower"){{
            alwaysUnlocked = true;
        }};
        speedPower = new Item("speedPower"){{
            alwaysUnlocked = true;
        }};
        shieldPower = new Item("shieldPower"){{
            alwaysUnlocked = true;
        }};
        againPower = new Item("AgainPower"){{
            alwaysUnlocked = true;
        }};
    }
}
