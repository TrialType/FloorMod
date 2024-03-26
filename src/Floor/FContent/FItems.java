package Floor.FContent;

import mindustry.type.Item;

public class FItems {
    public static Item
            v0 = new Item("v0") {{
        hidden = true;
    }},
            v1 = new Item("v1") {{
                hidden = true;
            }},
            v2 = new Item("v2") {{
                hidden = true;
            }},
            v3 = new Item("v3") {{
                hidden = true;
            }},
            v4 = new Item("v4") {{
                hidden = true;
            }},
            v5 = new Item("v5") {{
                hidden = true;
            }},
            v6 = new Item("v6") {{
                hidden = true;
            }},
            v7 = new Item("v7") {{
                hidden = true;
            }},
            v8 = new Item("v8") {{
                hidden = true;
            }},
            v9 = new Item("v9") {{
                hidden = true;
            }},
            v10 = new Item("v10") {{
                hidden = true;
            }};
    public final static Item[] numberItems = new Item[]{v0, v1, v2, v3, v4, v5, v6, v7, v8, v9, v10};
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

    public static int findIndex(Item item) {
        for (int i = 0; i < numberItems.length; i++) {
            if (numberItems[i] == item) {
                return i;
            }
        }
        return -1;
    }
}
