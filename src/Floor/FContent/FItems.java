package Floor.FContent;

import mindustry.type.Item;

public class FItems {
    public static Item damagePower, reloadPower, healthPower, speedPower, shieldPower, againPower;
    public static Item
            blueprint1, blueprint2, blueprint3, blueprint4, blueprint5,
            sizeProject1, sizeProject2, sizeProject3, sizeProject4, sizeProject5, sizeProject6, sizeProject7, sizeProject8, sizeProject9, sizeProject10,
            speedProject1, speedProject2, speedProject3, speedProject4, speedProject5,
            healthProject1, healthProject2, healthProject3, healthProject4, healthProject5,
            bulletProject1, bulletProject2, bulletProject3, bulletProject4, bulletProject5,
            reloadProject1, reloadProject2, reloadProject3, reloadProject4, reloadProject5,
            shieldProject1, shieldProject2, shieldProject3, shieldProject4, shieldProject5,
            splashProject1, splashProject2, splashProject3, splashProject4, splashProject5,
            pricesProject1, pricesProject2, pricesProject3, pricesProject4, pricesProject5,
            knockProject1, knockProject2, knockProject3, knockProject4, knockProject5,
            percentProject1, percentProject2, percentProject3, percentProject4, percentProject5;
    public static Item[] allSize, allSpeed, allHealth, allDamage, allReload,
            allShield, allSplash, allPrices, allKnock, allPercent;
    public static Item[][] allBullet, allAbility, allBase, allWeapon;

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

        blueprint1 = new Item("blue-print1") {{
            buildable = false;
        }};
        blueprint2 = new Item("blue-print2") {{
            buildable = false;
        }};
        blueprint3 = new Item("blue-print3") {{
            buildable = false;
        }};
        blueprint4 = new Item("blue-print4") {{
            buildable = false;
        }};
        blueprint5 = new Item("blue-print5") {{
            buildable = false;
        }};

        sizeProject1 = new Item("size-project1") {{
            hidden = true;
            buildable = false;
        }};
        sizeProject2 = new Item("size-project2") {{
            hidden = true;
            buildable = false;
        }};
        sizeProject3 = new Item("size-project3") {{
            hidden = true;
            buildable = false;
        }};
        sizeProject4 = new Item("size-project4") {{
            hidden = true;
            buildable = false;
        }};
        sizeProject5 = new Item("size-project5") {{
            hidden = true;
            buildable = false;
        }};
        sizeProject6 = new Item("size-project6") {{
            hidden = true;
            buildable = false;
        }};
        sizeProject7 = new Item("size-project7") {{
            hidden = true;
            buildable = false;
        }};
        sizeProject8 = new Item("size-project8") {{
            hidden = true;
            buildable = false;
        }};
        sizeProject9 = new Item("size-project9") {{
            hidden = true;
            buildable = false;
        }};
        sizeProject10 = new Item("size-project10") {{
            hidden = true;
            buildable = false;
        }};
        speedProject1 = new Item("speed-project1") {{
            hidden = true;
            buildable = false;
        }};
        speedProject2 = new Item("speed-project2") {{
            hidden = true;
            buildable = false;
        }};
        speedProject3 = new Item("speed-project3") {{
            hidden = true;
            buildable = false;
        }};
        speedProject4 = new Item("speed-project4") {{
            hidden = true;
            buildable = false;
        }};
        speedProject5 = new Item("speed-project5") {{
            hidden = true;
            buildable = false;
        }};
        healthProject1 = new Item("health-project1") {{
            hidden = true;
            buildable = false;
        }};
        healthProject2 = new Item("health-project2") {{
            hidden = true;
            buildable = false;
        }};
        healthProject3 = new Item("health-project3") {{
            hidden = true;
            buildable = false;
        }};
        healthProject4 = new Item("health-project4") {{
            hidden = true;
            buildable = false;
        }};
        healthProject5 = new Item("health-project5") {{
            hidden = true;
            buildable = false;
        }};
        bulletProject1 = new Item("copper-project1") {{
            hidden = true;
            buildable = false;
        }};
        bulletProject2 = new Item("copper-project2") {{
            hidden = true;
            buildable = false;
        }};
        bulletProject3 = new Item("copper-project3") {{
            hidden = true;
            buildable = false;
        }};
        bulletProject4 = new Item("copper-project4") {{
            hidden = true;
            buildable = false;
        }};
        bulletProject5 = new Item("copper-project5") {{
            hidden = true;
            buildable = false;
        }};
        reloadProject1 = new Item("reload-project1") {{
            hidden = true;
            buildable = false;
        }};
        reloadProject2 = new Item("reload-project2") {{
            hidden = true;
            buildable = false;
        }};
        reloadProject3 = new Item("reload-project3") {{
            hidden = true;
            buildable = false;
        }};
        reloadProject4 = new Item("reload-project4") {{
            hidden = true;
            buildable = false;
        }};
        reloadProject5 = new Item("reload-project5") {{
            hidden = true;
            buildable = false;
        }};
        shieldProject1 = new Item("shield-project1") {{
            hidden = true;
            buildable = false;
        }};
        shieldProject2 = new Item("shield-project2") {{
            hidden = true;
            buildable = false;
        }};
        shieldProject3 = new Item("shield-project3") {{
            hidden = true;
            buildable = false;
        }};
        shieldProject4 = new Item("shield-project4") {{
            hidden = true;
            buildable = false;
        }};
        shieldProject5 = new Item("shield-project5") {{
            hidden = true;
            buildable = false;
        }};
        splashProject1 = new Item("splash-project1") {{
            hidden = true;
            buildable = false;
        }};
        splashProject2 = new Item("splash-project2") {{
            hidden = true;
            buildable = false;
        }};
        splashProject3 = new Item("splash-project3") {{
            hidden = true;
            buildable = false;
        }};
        splashProject4 = new Item("splash-project4") {{
            hidden = true;
            buildable = false;
        }};
        splashProject5 = new Item("splash-project5") {{
            hidden = true;
            buildable = false;
        }};
        pricesProject1 = new Item("prices-project1") {{
            hidden = true;
            buildable = false;
        }};
        pricesProject2 = new Item("prices-project2") {{
            hidden = true;
            buildable = false;
        }};
        pricesProject3 = new Item("prices-project3") {{
            hidden = true;
            buildable = false;
        }};
        pricesProject4 = new Item("prices-project4") {{
            hidden = true;
            buildable = false;
        }};
        pricesProject5 = new Item("prices-project5") {{
            hidden = true;
            buildable = false;
        }};
        knockProject1 = new Item("knock-project1") {{
            hidden = true;
            buildable = false;
        }};
        knockProject2 = new Item("knock-project2") {{
            hidden = true;
            buildable = false;
        }};
        knockProject3 = new Item("knock-project3") {{
            hidden = true;
            buildable = false;
        }};
        knockProject4 = new Item("knock-project4") {{
            hidden = true;
            buildable = false;
        }};
        knockProject5 = new Item("knock-project5") {{
            hidden = true;
            buildable = false;
        }};
        percentProject1 = new Item("percent-project1") {{
            hidden = true;
            buildable = false;
        }};
        percentProject2 = new Item("percent-project2") {{
            hidden = true;
            buildable = false;
        }};
        percentProject3 = new Item("percent-project3") {{
            hidden = true;
            buildable = false;
        }};
        percentProject4 = new Item("percent-project4") {{
            hidden = true;
            buildable = false;
        }};
        percentProject5 = new Item("percent-project5") {{
            hidden = true;
            buildable = false;
        }};

        allSize = new Item[]{
                sizeProject1, sizeProject2, sizeProject3, sizeProject4, sizeProject5,
                sizeProject6, sizeProject7, sizeProject8, sizeProject9, sizeProject10
        };
        allHealth = new Item[]{healthProject1, healthProject2, healthProject3, healthProject4, healthProject5};
        allSpeed = new Item[]{speedProject1, speedProject2, speedProject3, speedProject4, speedProject5};
        allDamage = new Item[]{bulletProject1, bulletProject2, bulletProject3, bulletProject4, bulletProject5};
        allReload = new Item[]{reloadProject1, reloadProject2, reloadProject3, reloadProject4, reloadProject5};
        allShield = new Item[]{shieldProject1, shieldProject2, shieldProject3, shieldProject4, shieldProject5};
        allSplash = new Item[]{splashProject1, splashProject2, splashProject3, splashProject4, splashProject5};
        allPrices = new Item[]{pricesProject1, pricesProject2, pricesProject3, pricesProject4, pricesProject5};
        allKnock = new Item[]{knockProject1, knockProject2, knockProject3, knockProject4, knockProject5};
        allPercent = new Item[]{percentProject1, percentProject2, percentProject4, percentProject5};

        allBullet = new Item[][]{allDamage, allSplash, allPrices, allKnock, allPercent};
        allWeapon = new Item[][]{allReload};
        allAbility = new Item[][]{allShield};
        allBase = new Item[][]{allSize, allHealth, allSpeed};
    }
}
