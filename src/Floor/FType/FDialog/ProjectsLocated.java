package Floor.FType.FDialog;

import arc.func.Cons;
import arc.scene.ui.Dialog;
import arc.struct.Seq;
import mindustry.entities.abilities.Ability;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Unit;
import mindustry.type.Weapon;
import mindustry.ui.dialogs.BaseDialog;

import java.util.HashMap;

import static Floor.FContent.FItems.*;

public class ProjectsLocated extends BaseDialog {
    public static ProjectsLocated dialog;
    public static final Seq<ProjectsLocated.weaponPack> weapons = new Seq<>();
    public static final Seq<ProjectsLocated.abilityPack> abilities = new Seq<>();
    public static Cons<Unit> upper = u -> {
        Seq<Weapon> we = new Seq<>();
        Seq<Ability> ab = new Seq<>();
        for (ProjectsLocated.weaponPack wp : weapons) {
            we.add(wp.weapon);
        }
        for (ProjectsLocated.abilityPack ap : abilities) {
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

    public ProjectsLocated(String title, Dialog.DialogStyle style) {
        super(title, style);
        ProjectDialogUtils.init();
    }

    public ProjectsLocated(String title) {
        super(title);
        ProjectDialogUtils.init();
    }

    public static class weaponPack {
        Weapon weapon;
        WeaponDialog dialog;
        float heavy = 0;

        public weaponPack() {
            weapon = new Weapon();
            dialog = new WeaponDialog("", weapon, w -> weapon = w);
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
