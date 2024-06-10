package Floor.FType.FDialog;

import Floor.FEntities.FAbility.SprintingAbility;
import arc.Core;
import arc.func.Cons;
import arc.scene.actions.Actions;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import mindustry.entities.abilities.Ability;
import mindustry.entities.abilities.ForceFieldAbility;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;
import mindustry.type.Weapon;
import mindustry.ui.dialogs.BaseDialog;

import static Floor.FType.FDialog.ProjectUtils.*;
import static mindustry.Vars.ui;

public class ProjectsLocated extends BaseDialog implements EffectTableGetter {
    public static ProjectsLocated projects;
    public static Cons<Unit> app = u -> {
    };
    public Table effect;
    public StatusEffect heal = new StatusEffect("floor-project-heal") {{
        show = false;
        permanent = true;
    }}, speed = new StatusEffect("floor-project-speed") {{
        show = false;
        permanent = true;
    }};
    public float healthBoost, speedBoost;
    public float healD, speedD;
    public SprintingAbility boost;
    public final Seq<weaponPack> weapons = new Seq<>();
    public final Seq<abilityPack> abilities = new Seq<>();
    public Cons<Unit> upper = u -> {
        heal.healthMultiplier = healthBoost;
        speed.speedMultiplier = speedBoost;
        u.apply(heal);
        u.apply(speed);
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

    protected int seed;
    protected Table located, LWeapon, LAbility, base;

    private weaponPack pushW = null;
    private abilityPack pushA = null;

    public ProjectsLocated(String title, int seed) {
        super(title);

        projects = this;

        init(seed);
        ProjectUtils.init();

        shown(this::rebuild);
        hidden(() -> app = upper);
        hidden(this::removeTables);

        buttons.button("@back", Icon.left, () -> {
            read();
            hide();
        }).width(100);
        buttons.button(Core.bundle.get("@apply"), Icon.right, () -> {
            write();
            hide();
        }).width(100);
        buttons.button(Core.bundle.get("dialog.weapon.add"), Icon.add, () -> {
            if (freeSize >= 0.5f) {
                weapons.add(new weaponPack());
                freeSize -= 0.5f;
                rebuildWeapon();
                rebuildLocated();
            }
        }).width(200);
        buttons.button(Core.bundle.get("dialog.ability.add"), Icon.add, () -> {
            if (freeSize >= 0.5f) {
                abilities.add(new abilityPack());
                freeSize -= 0.5f;
                rebuildAbility();
                rebuildLocated();
            }
        }).width(200);
        buttons.button(Core.bundle.get("dialog.unit.base"), Icon.pencil, () -> {
            BaseDialog bd = new BaseDialog("");
            healD = healthBoost;
            speedD = speedBoost;
            bd.cont.pane(t -> {
                base = t;
                bd.setBackground(Tex.buttonEdge1);
                rebuildBase();
            });

            bd.buttons.button("@back", Icon.left, bd::hide).width(100);
            bd.buttons.button(Core.bundle.get("@apply"), Icon.right, () -> {
                if ((getHeavy("speed", speedD) + getHeavy("health", healD)) <= freeSize) {
                    healthBoost = healD;
                    speedBoost = speedD;
                    bd.hide();
                } else {
                    ui.showInfo(Core.bundle.get("@tooHeavy"));
                }
            }).width(100);
            bd.shown(() -> freeSize += getBaseHeavy(false));
            bd.hidden(() -> freeSize -= getBaseHeavy(true));
            bd.show();
        }).width(200);
    }

    public void rebuildBase() {
        base.clear();
        base.table(t -> {
            createLevDialog(t, "unit", "health", "healBoost", healD, f -> healD = f, this::rebuildBase,
                    () -> {
                    }, () -> couldUse("health", healD),
                    () -> getBaseHeavy(true) <= freeSize);
            createLevDialog(t, "unit", "speed", "speedBoost", speedD, f -> speedD = f, this::rebuildBase,
                    () -> {
                    }, () -> couldUse("speed", speedD),
                    () -> getBaseHeavy(true) <= freeSize);
        });

        base.row();
        base.table(t -> {
            t.label(() -> Core.bundle.get("dialog.unit.boost")).left().width(150);
            t.label(() -> Core.bundle.get("@heavyUse") + ": " + (boost == null ? 0 :
                    getHeavy("boost", boost.damage + boost.maxLength) +
                            getHeavy("boostReload", (60 / boost.reload)))).left().width(150);
            if (boost == null) {
                t.button(Icon.add, () -> {
                    boost = new SprintingAbility();
                    rebuildBase();
                }).left().pad(5);
                t.row();
            } else {
                t.button(Icon.trash, () -> {
                    boost = null;
                    rebuildBase();
                }).pad(5);
                t.row();
                createLevDialog(t, "unit", "boost", "damage", boost.damage,
                        f -> boost.damage = f, this::rebuildBase, () -> {
                        }, () -> couldUse("boost", boost.damage + boost.maxLength),
                        () -> getBaseHeavy(true) <= freeSize);
                createLevDialog(t, "unit", "boost", "maxLength", boost.maxLength,
                        f -> boost.maxLength = f, this::rebuildBase, () -> {
                        }, () -> couldUse("boost", boost.damage + boost.maxLength),
                        () -> getBaseHeavy(true) <= freeSize);
                createLevDialog(t, "unit", "boostReload", "reload", boost.reload,
                        f -> boost.reload = f, this::rebuildBase, () -> {
                        }, () -> couldUse("boostReload", (60 / boost.reload)),
                        () -> getBaseHeavy(true) <= freeSize);
                t.row();
                createNumberDialog(t, "unit", "powerReload", boost.powerReload,
                        f -> boost.powerReload = f, this::rebuildBase);
                createEffectList(t, this, "unit", "powerEffect",
                        () -> boost.powerEffect, e -> boost.powerEffect = e);
                createEffectList(t, this, "unit", "maxPowerEffect",
                        () -> boost.maxPowerEffect, e -> boost.maxPowerEffect = e);
            }
        }).width(1350);
    }

    public float getBaseHeavy(boolean write) {
        if (write) {
            return getHeavy("speed", speedD) + getHeavy("health", healD) + (boost == null ? 0 :
                    (getHeavy("boost", boost.damage + boost.maxLength) +
                            getHeavy("boostReload", (60 / boost.reload))));
        } else {
            return getHeavy("speed", speedBoost) + getHeavy("health", healthBoost) + (boost == null ? 0 :
                    (getHeavy("boost", boost.damage + boost.maxLength) +
                            getHeavy("boostReload", (60 / boost.reload))));
        }
    }

    public void rebuild() {
        cont.clear();
        cont.pane(t -> {
            t.setBackground(Tex.buttonEdge1);
            located = t;
        }).width(900).height(900);
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

    public void removeTables() {
        for (weaponPack wp : weapons) {
            if (wp.on != null) {
                wp.on.actions(Actions.fadeOut(0), Actions.remove());
                Core.app.post(wp.on::remove);
            }
        }
        for (abilityPack ap : abilities) {
            if (ap.on != null) {
                ap.on.actions(Actions.fadeOut(0), Actions.remove());
                Core.app.post(ap.on::remove);
            }
        }
    }

    public void rebuildLocated() {
        located.clear();
        located.image(Core.atlas.find("gamma", "")).width(700).height(700);

        for (int i = 0; i < weapons.size; i++) {
            int finalI = i;
            weaponPack wp = weapons.get(finalI);
            if (wp.on == null) {
                Table t = new Table();
                t.top().pane(w -> {
                    w.top();
                    w.setSize(200, 30);

                    w.label(() -> Core.bundle.get("dialog.weapon.index") + ": ");
                    w.label(() -> Core.bundle.get("dialog.weapon.index") + finalI).pad(5);
                });
                t.update(() -> {
                    t.setBackground(pushW == wp ? Tex.buttonDown : Tex.windowEmpty);
                    t.setPosition(wp.weapon.x + located.x + located.getWidth() / 2,
                            wp.weapon.y + located.y + located.getHeight() / 2);

                    t.keepInStage();
                    t.invalidateHierarchy();
                    t.pack();
                });
                Core.scene.add(t);
                t.actions(Actions.alpha(0), Actions.fadeIn(0));
                t.pack();
                wp.on = t;
            }
        }
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
                    if (wp.on != null) {
                        Core.app.post(wp.on::remove);
                    }
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
                    if (ap.on != null) {
                        Core.app.post(ap.on::remove);
                    }
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
        Core.settings.put("floor-project-healthBoost-" + seed, healthBoost);
        Core.settings.put("floor-project-speedBoost-" + seed, speedBoost);
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
        healthBoost = Core.settings.getFloat("floor-project-healthBoost-" + seed, 1);
        speedBoost = Core.settings.getFloat("floor-project-speedBoost-" + seed, 1);
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

    @Override
    public Table get() {
        return effect;
    }

    @Override
    public void set(Table table) {
        effect = table;
    }

    public class weaponPack {
        public Table on;
        public Weapon weapon;
        public WeaponDialog dialog;
        public float heavy;

        public weaponPack() {
            weapon = new Weapon();
            weapon.reload = 500;
            weapon.targetSwitchInterval = weapon.targetInterval = 500;
            dialog = new WeaponDialog("", weapon, w -> weapon = w, f -> heavy = f);
            dialog.hidden(ProjectsLocated.this::rebuild);
            heavy = 0.5f + dialog.bulletHeavy;
        }

        public weaponPack(Weapon weapon) {
            this.weapon = weapon;
            dialog = new WeaponDialog("", this.weapon, w -> this.weapon = w, f -> heavy = f);
            dialog.hidden(ProjectsLocated.this::rebuild);
            heavy = dialog.heavy + getShootVal(weapon.shoot) * dialog.bulletHeavy;
        }
    }

    public class abilityPack {
        public Table on;
        public Ability ability;
        public AbilityDialog dialog;
        public float heavy;

        public abilityPack() {
            ability = new ForceFieldAbility(90, 0, 0, Float.MAX_VALUE, 0, 0);
            dialog = new AbilityDialog("", () -> ability, a -> ability = a, f -> heavy = f);
            dialog.hidden(ProjectsLocated.this::rebuild);
            heavy = dialog.heavy;
        }

        public abilityPack(Ability ability) {
            this.ability = ability;
            dialog = new AbilityDialog("", () -> this.ability, a -> this.ability = a, f -> heavy = f);
            dialog.hidden(ProjectsLocated.this::rebuild);
            heavy = dialog.heavy + dialog.bulletHeavy;
        }
    }
}