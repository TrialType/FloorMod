package Floor.FType.FDialog;

import arc.func.Cons;
import arc.struct.Seq;
import mindustry.entities.abilities.Ability;
import mindustry.game.EventType;
import mindustry.gen.Unit;
import mindustry.type.Weapon;
import mindustry.ui.dialogs.BaseDialog;

import java.util.HashMap;

import static Floor.FContent.FItems.*;

public class ProjectsLocated extends BaseDialog {
    public static Cons<Unit> upper = u -> {
    };
    public static float maxSize = 0;
    public static float freeSize = 0;
    public static final HashMap<String, heavyGetter> heavies = new HashMap<>();
    public static final HashMap<String, levelGetter> levels = new HashMap<>();
    public static final HashMap<String, Integer> maxLevel = new HashMap<>();
    public static final Seq<weaponPack> weapons = new Seq<>();
    public static final Seq<abilityPack> abilities = new Seq<>();

    public ProjectsLocated(String title, DialogStyle style) {
        super(title, style);
    }

    public ProjectsLocated(String title) {
        super(title);
    }

    static {
        //just for effect
        heavies.put("none", i -> 0);

        levels.put("none", f -> 0);

        //bullet
        heavies.put("bulletBase", i -> i * 1f);
        heavies.put("splash", i -> i * 1.5f);
        heavies.put("lightning", i -> i * 1.2f);
        heavies.put("percent", i -> i * 1.2f);
        heavies.put("frags", i -> i * 1.8f);
        heavies.put("emp", i -> i);

        levels.put("bulletBase", f -> f <= 0 ? 0 : f <= 36 ? 1 : f <= 72 ? 2 : f <= 144 ? 3 : f <= 288 ? 4 : f <= 576 ? 5 : 6);
        levels.put("splash", f -> f <= 0 ? 0 : f <= 3 ? 1 : f <= 4.2 ? 2 : f <= 7 ? 3 : f <= 12 ? 4 : f <= 19 ? 5 : 6);
        levels.put("lightning", f -> f <= 0 ? 0 : f <= 2 ? 1 : f <= 4 ? 2 : f <= 8 ? 3 : f <= 12 ? 4 : f <= 20 ? 5 : 6);
        levels.put("percent", f -> f <= 0 ? 0 : f <= 0.1 ? 1 : f <= 0.15 ? 2 : f <= 0.3 ? 3 : f <= 0.45 ? 4 : f <= 0.65 ? 5 : 6);
        levels.put("frags", f -> f <= 0 ? 0 : f <= 1 ? 1 : f <= 2 ? 2 : f <= 4 ? 3 : f <= 6 ? 4 : f <= 10 ? 5 : 6);
        levels.put("emp", f -> f <= 0 ? 0 : f <= 60 ? 1 : f <= 90 ? 2 : f <= 130 ? 3 : f <= 180 ? 4 : f <= 260 ? 5 : 6);

        //weapon
        heavies.put("number", i -> i * 1.8f);

        levels.put("number", f -> f <= 0 ? 0 : f <= 1 ? 1 : f <= 2 ? 2 : f <= 4 ? 3 : f <= 6 ? 4 : f <= 10 ? 5 : 6);

        updateMaxLevel();
        updateHeavy();
    }

    public static float getHeavy(String type, float val) {
        if (heavies.get(type) == null) {
            return 0;
        }
        return heavies.get(type).get(levels.get(type).get(val));
    }

    public static boolean couldUse(String type, float val) {
        if (maxLevel.isEmpty()) {
            updateMaxLevel();
        }
        return levels.get(type).get(val) <= maxLevel.computeIfAbsent(type, name -> 0);
    }

    public static void updateHeavy() {
        //test
        maxSize = 2;
        for (int i = allSize.length; i > 0; i--) {
            if (allSize[i - 1].unlocked()) {
                maxSize = 2 + (i == 1 ? 1 : i == 2 ? 2 : i == 3 ? 3 : i == 4 ? 4 : i == 5 ? 5 : i == 6 ? 6 : i == 7 ? 7 :
                        i == 8 ? 8 : i == 9 ? 9 : 10);
                break;
            }
        }
        freeSize = maxSize;
        for (weaponPack wp : weapons) {
            freeSize -= wp.heavy;
        }
        for (abilityPack ap : abilities) {
            freeSize -= ap.heavy;
        }
    }

    public static void updateMaxLevel() {
        //bulletBase
        maxLevel.put("bulletBase", 0);
        for (int i = allBulletBase.length; i > 0; i--) {
            if (allBulletBase[i - 1].unlocked()) {
                maxLevel.put("bulletBase", i);
                break;
            }
        }

        //splash
        maxLevel.put("splash", 0);
        for (int i = allSplash.length; i > 0; i--) {
            if (allSplash[i - 1].unlocked()) {
                maxLevel.put("splash", i);
                break;
            }
        }

        //knock
        maxLevel.put("knock", 0);
        for (int i = allKnock.length; i > 0; i--) {
            if (allKnock[i - 1].unlocked()) {
                maxLevel.put("knock", i);
                break;
            }
        }

        //percent
        maxLevel.put("percent", 0);
        for (int i = allPercent.length; i > 0; i--) {
            if (allPercent[i - 1].unlocked()) {
                maxLevel.put("percent", i);
                break;
            }
        }
    }

    public interface heavyGetter {
        float get(int lev);
    }

    public interface levelGetter {
        int get(float val);
    }

    public static class weaponPack {
        Weapon weapon;
        float heavy = 0;
    }

    public static class abilityPack {
        Ability ability;
        float heavy = 0;
    }
}
