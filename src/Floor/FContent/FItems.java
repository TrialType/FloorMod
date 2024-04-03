package Floor.FContent;

import mindustry.type.Item;

public class FItems {
    public static Item damagePower, reloadPower, healthPower, speedPower, shieldPower, againPower;

    public static void load() {
        damagePower = new Item("damage_power") {{
            alwaysUnlocked = true;
        }};
        reloadPower = new Item("reload_power") {{
            alwaysUnlocked = true;
        }};
        healthPower = new Item("health_power") {{
            alwaysUnlocked = true;
        }};
        speedPower = new Item("speed_power") {{
            alwaysUnlocked = true;
        }};
        shieldPower = new Item("shield_power") {{
            alwaysUnlocked = true;
        }};
        againPower = new Item("again_power") {{
            alwaysUnlocked = true;
        }};
    }
}
