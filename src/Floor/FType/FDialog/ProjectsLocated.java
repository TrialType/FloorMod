package Floor.FType.FDialog;

import arc.func.Cons;
import arc.struct.Seq;
import mindustry.entities.abilities.Ability;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Unit;
import mindustry.type.Weapon;
import mindustry.ui.dialogs.BaseDialog;

import java.util.HashMap;

import static Floor.FContent.FItems.*;

public class ProjectsLocated extends BaseDialog {
    public static float maxSize = 0;
    public static float freeSize = 0;
    public static final HashMap<String, heavyGetter> heavies = new HashMap<>();
    public static final HashMap<String, levelGetter> levels = new HashMap<>();
    public static final HashMap<String, Integer> maxLevel = new HashMap<>();
    public static final Seq<weaponPack> weapons = new Seq<>();
    public static final Seq<abilityPack> abilities = new Seq<>();
    public static Cons<Unit> upper = u -> {
        Seq<Weapon> we = new Seq<>();
        Seq<Ability> ab = new Seq<>();
        for (weaponPack wp : weapons) {
            we.add(wp.weapon);
        }
        for (abilityPack ap : abilities) {
            ab.add(ap.ability);
        }
        if (u.mounts == null) {
            u.mounts = new WeaponMount[we.size];
            for (int i = 0; i < we.size; i++) {
                u.mounts[i] = new WeaponMount(we.get(i));
            }
        } else {
            int len = u.mounts.length;
            WeaponMount[] wm = new WeaponMount[len + we.size];
            System.arraycopy(u.mounts, 0, wm, 0, len);
            for (int i = len; i < wm.length; i++) {
                wm[i] = new WeaponMount(we.get(i - len));
            }
            u.mounts = wm;
        }

        if (u.abilities == null) {
            u.abilities = ab.items;
        } else {
            int len = u.abilities.length;
            Ability[] a = new Ability[len + ab.size];
            System.arraycopy(u.abilities, 0, a, 0, len);
            for (int i = len; i < a.length; i++) {
                a[i] = ab.get(i - len);
            }
            u.abilities = a;
        }
    };

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
        heavies.put("knock", i -> i * 2);

        levels.put("bulletBase", f -> f <= 0 ? 0 : f <= 36 ? 1 : f <= 72 ? 2 : f <= 144 ? 3 : f <= 288 ? 4 : f <= 576 ? 5 : 6);
        levels.put("splash", f -> f <= 0 ? 0 : f <= 3 ? 1 : f <= 4.2 ? 2 : f <= 7 ? 3 : f <= 12 ? 4 : f <= 19 ? 5 : 6);
        levels.put("lightning", f -> f <= 0 ? 0 : f <= 2 ? 1 : f <= 4 ? 2 : f <= 8 ? 3 : f <= 12 ? 4 : f <= 20 ? 5 : 6);
        levels.put("percent", f -> f <= 0 ? 0 : f <= 0.1 ? 1 : f <= 0.15 ? 2 : f <= 0.3 ? 3 : f <= 0.45 ? 4 : f <= 0.65 ? 5 : 6);
        levels.put("frags", f -> f <= 0 ? 0 : f <= 1 ? 1 : f <= 2 ? 2 : f <= 3 ? 3 : f <= 4 ? 4 : f <= 5 ? 5 : 6);
        levels.put("emp", f -> f <= 0 ? 0 : f <= 60 ? 1 : f <= 90 ? 2 : f <= 130 ? 3 : f <= 180 ? 4 : f <= 260 ? 5 : 6);
        levels.put("knock", f -> f <= 0 ? 0 : f <= 0.1 ? 1 : f <= 0.15 ? 2 : f <= 0.2 ? 3 : f <= 0.25 ? 4 : f <= 0.3 ? 5 : 6);

        //weapon
        heavies.put("number", i -> i * 1.8f);
        heavies.put("reload", i -> i * 1.5f);
        heavies.put("target", i -> i * 1.5f);

        levels.put("number", f -> f <= 1 ? 0 : f <= 2 ? 1 : f <= 3 ? 2 : f <= 4 ? 3 : f <= 5 ? 4 : f <= 6 ? 5 : 6);
        levels.put("reload", f -> f >= 150 ? 0 : f >= 120 ? 1 : f >= 90 ? 2 : f >= 60 ? 3 : f >= 30 ? 4 : f >= 15 ? 5 : 6);
        levels.put("target", f -> f >= 60 ? 0 : f >= 50 ? 1 : f >= 40 ? 2 : f >= 30 ? 3 : f >= 20 ? 4 : f >= 10 ? 5 : 6);

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

        //frags
        maxLevel.put("frags", 0);
        for (int i = allFrag.length; i > 0; i--) {
            if (allFrag[i - 1].unlocked()) {
                maxLevel.put("frags", i);
                break;
            }
        }

        //lightning
        maxLevel.put("lightning", 0);
        for (int i = allLightning.length; i > 0; i--) {
            if (allLightning[i - 1].unlocked()) {
                maxLevel.put("lightning", i);
                break;
            }
        }

        //emp
        maxLevel.put("emp", 0);
        for (int i = allEmp.length; i > 0; i--) {
            if (allEmp[i - 1].unlocked()) {
                maxLevel.put("emp", i);
                break;
            }
        }

        //number
        maxLevel.put("number", 0);
        for (int i = allBulletNumber.length; i > 0; i--) {
            if (allBulletNumber[i - 1].unlocked()) {
                maxLevel.put("number", i);
                break;
            }
        }

        //reload
        maxLevel.put("reload", 0);
        for (int i = allReload.length; i > 0; i--) {
            if (allReload[i - 1].unlocked()) {
                maxLevel.put("reload", i);
                break;
            }
        }

        //target
        maxLevel.put("target", 0);
        for (int i = allTargetInterval.length; i > 0; i--) {
            if (allTargetInterval[i - 1].unlocked()) {
                maxLevel.put("target", i);
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
        WeaponDialog dialog;
        float heavy = 0;

        public weaponPack() {
            weapon = new Weapon();
            dialog = new WeaponDialog("", this);
        }
    }

    public static class abilityPack {
        Ability ability;
        AbilityDialog dialog;
        float heavy = 0;

        public abilityPack() {
            dialog = new AbilityDialog("", this);
        }
    }
}
