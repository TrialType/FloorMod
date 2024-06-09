package Floor.FType.FDialog;

import arc.Core;
import arc.func.Cons;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import mindustry.entities.abilities.Ability;
import mindustry.entities.abilities.ForceFieldAbility;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.gen.Unit;
import mindustry.type.Weapon;
import mindustry.ui.dialogs.BaseDialog;

import static Floor.FType.FDialog.ProjectUtils.*;
import static mindustry.Vars.ui;

public class ProjectsLocated extends BaseDialog {
    public static ProjectsLocated projects;
    public final Seq<weaponPack> weapons = new Seq<>();
    public final Seq<abilityPack> abilities = new Seq<>();
    public Cons<Unit> upper = u -> {
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

    protected int seed;
    protected Table located, LWeapon, LAbility;

    private weaponPack pushW = null;
    private abilityPack pushA = null;

    public ProjectsLocated(String title, int seed) {
        super(title);

        projects = this;

        init(seed);
        ProjectUtils.init();

        shown(this::rebuild);

        buttons.button("@back", Icon.left, this::hide).width(100);
        buttons.button(Core.bundle.get("@apply"), Icon.right, () -> {
            hide();
            write();
        }).width(100);
        buttons.button(Core.bundle.get("dialog.weapon.add"), Icon.add, () -> {
            if (freeSize >= 0.5f) {
                weapons.add(new weaponPack());
                freeSize -= 0.5f;
                rebuildWeapon();
            }
        }).width(300);
        buttons.button(Core.bundle.get("dialog.ability.add"), Icon.add, () -> {
            if (freeSize >= 0.5f) {
                abilities.add(new abilityPack());
                freeSize -= 0.5f;
                rebuildAbility();
            }
        }).width(300);
    }

    public void rebuild() {
        cont.clear();
        cont.pane(t -> {
            t.setBackground(Tex.buttonEdge1);
            located = t;
        }).width(1000).height(1000);
        cont.pane(t -> {
            t.table(w -> {
                w.setBackground(Tex.buttonEdge1);
                LWeapon = w;
            }).width(500).growY();
            t.table(a -> {
                a.setBackground(Tex.buttonEdge1);
                LAbility = a;
            }).width(500).growY();
        }).width(1000);

        rebuildLocated();
        rebuildWeapon();
        rebuildAbility();
    }

    public void rebuildLocated() {
        located.clear();
        located.image(Core.atlas.find("gamma", "")).width(950).height(950);
    }

    public void rebuildWeapon() {
        LWeapon.clear();
        LWeapon.table(t -> {
            t.setBackground(Tex.buttonEdge1);
            t.label(() -> Core.bundle.get("@heavyUse") + getWeaponHeavy() + "/" + maxSize);
        }).width(500).row();
        for (int i = 0; i < weapons.size; i++) {
            weaponPack wp = weapons.get(i);
            int finalI = i;
            LWeapon.table(t -> {
                t.setBackground(pushW == wp ? Tex.buttonDown : Tex.windowEmpty);

                t.clicked(() -> {
                    pushW = pushW == wp ? null : wp;
                    rebuildLocated();
                    rebuildWeapon();
                });
                t.label(() -> Core.bundle.get("dialog.weapon.index") + ": " + finalI).left().width(100).pad(5);
                t.label(() -> Core.bundle.get("@heavyUse") + wp.heavy).left().width(100).pad(5);
                t.button(Icon.pencil, () -> {
                    if (freeSize > 0) {
                        wp.dialog.show();
                    } else {
                        ui.showInfo(Core.bundle.get("tooHeavyDrop"));
                    }
                }).pad(5);
                t.button(Icon.trash, () -> {
                    freeSize += wp.heavy;
                    weapons.remove(finalI);
                    pushW = pushW == wp ? null : pushW;
                    rebuildLocated();
                    rebuildWeapon();
                }).pad(5);
            }).width(500);
            LWeapon.row();
        }
    }

    public void rebuildAbility() {
        LAbility.clear();
        LAbility.table(t -> {
            t.setBackground(Tex.buttonEdge1);
            t.label(() -> Core.bundle.get("@heavyUse") + getAbilityHeavy() + "/" + maxSize);
        }).width(500).row();
        for (int i = 0; i < abilities.size; i++) {
            int finalI = i;
            abilityPack ap = abilities.get(finalI);
            LAbility.table(t -> {
                t.setBackground(ap == pushA ? Tex.buttonDown : Tex.windowEmpty);

                t.clicked(() -> {
                    pushA = pushA == ap ? null : ap;
                    rebuildLocated();
                    rebuildAbility();
                });
                t.label(() -> Core.bundle.get("dialog.ability.index") + ": " + finalI).left().width(100).pad(5);
                t.label(() -> Core.bundle.get("@heavyUse") + ap.heavy);
                t.button(Icon.pencil, () -> {
                    if (freeSize > 0) {
                        ap.dialog.show();
                    } else {
                        ui.showInfo(Core.bundle.get("tooHeavyDrop"));
                    }
                }).pad(5);
                t.button(Icon.trash, () -> {
                    freeSize += ap.heavy;
                    abilities.remove(finalI);
                    pushA = pushA == ap ? null : pushA;
                    rebuildLocated();
                    rebuildAbility();
                }).pad(5);
            }).width(500).row();
        }
    }

    public void init(int seed) {
        this.seed = seed;
        read();
    }

    public void write() {
        Seq<Weapon> w = new Seq<>();
        Seq<Ability> a = new Seq<>();
        for (weaponPack p : weapons) {
            w.add(p.weapon);
        }
        for (abilityPack p : abilities) {
            a.add(p.ability);
        }
        Core.settings.putJson("floor-project-weapons-" + seed, Seq.class, w);
        Core.settings.putJson("floor-project-abilities-" + seed, Seq.class, a);
    }

    public void read() {
        weapons.clear();
        abilities.clear();
        Seq<Weapon> w;
        Seq<Ability> a;
        //noinspection unchecked
        w = (Seq<Weapon>) Core.settings.getJson("floor-project-weapons-" + seed, Seq.class, Seq::new);
        //noinspection unchecked
        a = (Seq<Ability>) Core.settings.getJson("floor-project-abilities-" + seed, Seq.class, Seq::new);
        for (Weapon ww : w) {
            weapons.add(new weaponPack(ww));
        }
        for (Ability ab : a) {
            abilities.add(new abilityPack(ab));
        }
    }

    public float getWeaponHeavy() {
        float h = 0;
        for (weaponPack wp : weapons) {
            h += wp.heavy;
        }
        return h;
    }

    public float getAbilityHeavy() {
        float heavy = 0;
        for (abilityPack ap : abilities) {
            heavy += ap.heavy;
        }
        return heavy;
    }

    public static class weaponPack {
        public Weapon weapon;
        public WeaponDialog dialog;
        public float heavy;

        public weaponPack() {
            weapon = new Weapon();
            weapon.reload = 500;
            weapon.targetSwitchInterval = weapon.targetInterval = 500;
            dialog = new WeaponDialog("", weapon, w -> weapon = w, f -> heavy = f);
            heavy = 0.5f + dialog.bulletHeavy;
        }

        public weaponPack(Weapon weapon) {
            this.weapon = weapon;
            dialog = new WeaponDialog("", this.weapon, w -> this.weapon = w, f -> heavy = f);
            heavy = dialog.heavy + getShootVal(weapon.shoot) * dialog.bulletHeavy;
        }
    }

    public static class abilityPack {
        public Ability ability;
        public AbilityDialog dialog;
        public float heavy;

        public abilityPack() {
            ability = new ForceFieldAbility(90, 0, 0, Float.MAX_VALUE, 0, 0);
            dialog = new AbilityDialog("", () -> ability, a -> ability = a, f -> heavy = f);
            heavy = dialog.heavy;
        }

        public abilityPack(Ability ability) {
            this.ability = ability;
            dialog = new AbilityDialog("", () -> this.ability, a -> this.ability = a, f -> heavy = f);
            heavy = dialog.heavy + dialog.bulletHeavy;
        }
    }
}