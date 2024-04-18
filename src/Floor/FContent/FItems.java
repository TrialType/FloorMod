package Floor.FContent;

import mindustry.type.Item;

public class FItems {
    public static Item damagePower, reloadPower, healthPower, speedPower, shieldPower, againPower;

    public static void load() {
        damagePower = new Item("damage_power") {{
            buildable = false;
            alwaysUnlocked = true;
        }};
        reloadPower = new Item("reload_power") {{
            buildable = false;
            alwaysUnlocked = true;
        }};
        healthPower = new Item("health_power") {{
            buildable = false;
            alwaysUnlocked = true;
        }};
        speedPower = new Item("speed_power") {{
            buildable = false;
            alwaysUnlocked = true;
        }};
        shieldPower = new Item("shield_power") {{
            buildable = false;
            alwaysUnlocked = true;
        }};
        againPower = new Item("again_power") {{
            buildable = false;
            alwaysUnlocked = true;
        }};
    }
}
