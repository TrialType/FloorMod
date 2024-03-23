package Floor.FContent;

import mindustry.type.Item;

public class FItems {
    public static Item damagePower, reloadPower, healthPower, speedPower, shieldPower, againPower;
    public static void load(){
        damagePower = new Item("damagePower");
        reloadPower = new Item("reloadPower");
        healthPower = new Item("healthPower");
        speedPower = new Item("speedPower");
        shieldPower = new Item("shieldPower");
        againPower = new Item("AgainPower");
    }
}
